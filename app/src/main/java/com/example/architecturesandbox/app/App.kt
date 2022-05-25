package com.example.architecturesandbox.app

import android.app.Application
import androidx.security.crypto.MasterKeys
import com.example.architecturesandbox.realm.RealmProvider
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import org.jetbrains.anko.longToast

@HiltAndroidApp
class App: Application() {

    companion object {
        lateinit var INSTANCE: App
        lateinit var masterKeyAlias: String
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        Realm.init(this)

        if (RealmProvider.isValidRealm(this).not())
            longToast("Detectamos un cambio en la configuración del dispositivo, es necesario volver a configurar la app.")
        try {
            RealmProvider.configureRealm(this)
        } catch (e: Exception) {
            longToast("Hemos detectado un cambio en la configuración de tu dispositivo por lo que es necesario volver a configurar esta aplicación.")
            RealmProvider.resetApp(this)
        }
    }

}