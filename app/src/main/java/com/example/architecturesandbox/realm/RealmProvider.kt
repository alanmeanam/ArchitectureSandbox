package com.example.architecturesandbox.realm

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.architecturesandbox.app.App
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.security.auth.x500.X500Principal

class RealmProvider {

    val pref = EncryptedSharedPreferences.create(
        RealmKeyProvider.SHARED_PREFERENCE_NAME,
        App.masterKeyAlias,
        App.INSTANCE,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    companion object {
        const val TAG = "RealmProvider"
        private const val REALM_NAME = "disk_cache"
        private var sRealmConfiguration: RealmConfiguration? = null

        fun isValidRealm(context: Context): Boolean {
            val keyProvider = RealmKeyProvider(context)
            val key: ByteArray = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".toByteArray()
            val existentKey = RealmProvider().pref.getString(RealmKeyProvider.ENCRYPTED_KEY, "")
            return if (!existentKey.isNullOrBlank() && key.isEmpty()) {
                sRealmConfiguration = RealmConfiguration.Builder()
                    .name(REALM_NAME)
                    .schemaVersion(Migration.SCHEMA_VERSION)
                    .migration(Migration())
                    .build()
                Realm.deleteRealm(sRealmConfiguration!!)
                RealmProvider().pref.edit {
                    clear()
                    apply()
                }
                false
            } else
                true
        }

        fun resetApp(context: Context) {
            sRealmConfiguration = RealmConfiguration.Builder()
                .name(REALM_NAME)
                .schemaVersion(Migration.SCHEMA_VERSION)
                .migration(Migration())
                .build()
            Realm.deleteRealm(sRealmConfiguration!!)
            RealmProvider().pref.edit {
                clear()
                apply()
            }
        }

        fun configureRealm(context: Context) {
            val keyProvider = RealmKeyProvider(context)
            val key: ByteArray = "0123456789012345678901234567890123456789012345678901234567890123".toByteArray()
            if (key.isNotEmpty()) {
                sRealmConfiguration = RealmConfiguration.Builder()
                    .name(REALM_NAME)
                    .allowQueriesOnUiThread(false)
                    .allowWritesOnUiThread(false)
                    .schemaVersion(Migration.SCHEMA_VERSION)
                    .migration(Migration())
                    .encryptionKey(key)
                    .build()
                Realm.removeDefaultConfiguration()
                Realm.setDefaultConfiguration(sRealmConfiguration!!)
            }
        }

        val instance: Realm
            get() = sRealmConfiguration?.let {
                Realm.getInstance(it)
            } ?: throw IllegalStateException("Must configure realm before instantiation.")
    }

    private class RealmKeyProvider constructor(private val mContext: Context) {

        companion object {
            private const val ANDROID_KEY_STORE = "AndroidKeyStore"
            private const val KEY_ALIAS = "RSA_KEYS"
            private const val RSA_MODE = "RSA/NONE/PKCS1Padding"
            const val SHARED_PREFERENCE_NAME = "com.example.architecturesandbox.realm.RealmKeyFile"
            const val ENCRYPTED_KEY = "RealmKey"
            private const val ENCRYPTION_ALGORITHM = "RSA"
            private const val KEY_ALGORITHM = "AES"
            private const val KEY_SIZE = 512

            private val BYTES = ByteArray(0)
            private fun rsaEncrypt(secret: ByteArray?): ByteArray {
                var values = BYTES
                try {
                    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
                    keyStore.load(null)
                    val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
                    val inputCipher = Cipher.getInstance(RSA_MODE)
                    inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)
                    val outputStream = ByteArrayOutputStream()
                    val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
                    cipherOutputStream.write(secret)
                    cipherOutputStream.close()
                    values = outputStream.toByteArray()
                } catch (e: UnrecoverableEntryException) {
                    Log.e(TAG, e.message, e.cause)
                } catch (e: NoSuchAlgorithmException) {
                    Log.e(TAG, e.message, e.cause)
                } catch (e: NoSuchPaddingException) {
                    Log.e(TAG, e.message, e.cause)
                } catch (e: CertificateException) {
                    Log.e(TAG, e.message, e.cause)
                } catch (e: IOException) {
                    Log.e(TAG, e.message, e.cause)
                } catch (e: InvalidKeyException) {
                    Log.e(TAG, e.message, e.cause)
                } catch (e: KeyStoreException) {
                    Log.e(TAG, e.message, e.cause)
                }
                return values
            }

            /**
             * Ref: https://android-developers.blogspot.se/2013/02/using-cryptography-to-store-credentials.html
             *
             * @return A random key to use in Realm encryption
             */
            private fun generateKey(): ByteArray? {
                var key: SecretKey? = null
                try {
                    val secureRandom = SecureRandom()
                    // Do *not* seed secureRandom! Automatically seeded from system entropy.
                    val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM)
                    if (Build.VERSION.SDK_INT == 31) keyGenerator.init(
                        256,
                        secureRandom
                    ) else keyGenerator.init(
                        KEY_SIZE, secureRandom
                    )
                    key = keyGenerator.generateKey()
                } catch (e: NoSuchAlgorithmException) {
                    Log.e("RealmProvider", e.message, e.cause)
                }
                return key?.encoded
            }

            fun generatePass(context: Context): String {
                val str1 = (Build.BOARD + Build.BRAND + Build.CPU_ABI + Build.DEVICE +
                        Build.DISPLAY + Build.FINGERPRINT + Build.HOST + Build.ID + Build.MANUFACTURER
                        +
                        Build.MODEL + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER)
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                val key2 = "$androidId-$str1"
                return key2
            }

            private fun rsaDecrypt(encrypted: ByteArray, key: String): ByteArray {
                val values = ArrayList<Byte>(64)
                try {
                    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
                    keyStore.load(null)
                    val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
                    val output = Cipher.getInstance(RSA_MODE)
                    output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
                    val cipherInputStream = CipherInputStream(ByteArrayInputStream(encrypted), output)
                    var nextByte: Int
                    while (cipherInputStream.read().also { nextByte = it } != -1) {
                        values.add(nextByte.toByte())
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e.cause)
                }
                val bytes = ByteArray(values.size)
                for (i in bytes.indices) {
                    bytes[i] = values[i]
                }
                return bytes
            }
        }

        init {
            initializeEncryptionKeys()
            generateAndStoreKey()
        }

        private fun initializeEncryptionKeys() {
            try {
                val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
                keyStore.load(null)
                if (!keyStore.containsAlias(KEY_ALIAS)) {
                    val start = Calendar.getInstance()
                    val end = Calendar.getInstance()
                    end.add(Calendar.YEAR, 30)
                    val spec = KeyPairGeneratorSpec.Builder(mContext)
                        .setAlias(KEY_ALIAS)
                        .setSubject(X500Principal("CN=" + KEY_ALIAS))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.time)
                        .setEndDate(end.time).build()
                    val kpg = KeyPairGenerator
                        .getInstance(ENCRYPTION_ALGORITHM, ANDROID_KEY_STORE)
                    kpg.initialize(spec)
                    kpg.generateKeyPair()
                }
            } catch (e: KeyStoreException) {
                Log.e(TAG, e.message, e.cause)
            } catch (e: IOException) {
                Log.e(TAG, e.message, e.cause)
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, e.message, e.cause)
            } catch (e: CertificateException) {
                Log.e(TAG, e.message, e.cause)
            } catch (e: NoSuchProviderException) {
                Log.e(TAG, e.message, e.cause)
            } catch (e: InvalidAlgorithmParameterException) {
                Log.e(TAG, e.message, e.cause)
            }
        }

        private fun generateAndStoreKey() {
            val existentKey = RealmProvider().pref.getString(ENCRYPTED_KEY, "")
            if (existentKey.isNullOrBlank()) {
                val encryptedKey = rsaEncrypt(generateKey())
                val enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
                RealmProvider().pref.edit {
                    putString(ENCRYPTED_KEY, enryptedKeyB64)
                    apply()
                }
            }
        }

        private val secretKey: ByteArray
            get() {
                var key = BYTES
                val existentKey = RealmProvider().pref.getString(ENCRYPTED_KEY, "")
                if (!existentKey.isNullOrBlank()) {
                    val enryptedKeyB64 = existentKey.toByteArray()
                    val encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT)
                    key = rsaDecrypt(encryptedKey, existentKey)
                }
                return key
            }
    }

}