package com.example.architecturesandbox.realm

import com.example.architecturesandbox.base.BaseError
import com.example.architecturesandbox.common.ThreadInfoLogger
import com.example.architecturesandbox.common.state.DataState
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class RealmBaseDaoHandler<T: RealmObject>: BaseDao<T>() {

    /**
     * ADD
     */

    override suspend fun add(obj: T, primaryKey: String, realmId: Long?): DataState<T> =
        RealmProvider.instance.use { realm ->
            ThreadInfoLogger.logThreadInfo(message = "add DaoHandler")
            val id = modelClass.getDeclaredField(primaryKey)
            id.isAccessible = true
            realmId?.let {
                id.setLong(obj, it)
            } ?: run {
                val max = obtainMaxId(primaryKey = primaryKey, realm = realm)
                id.setLong(obj, max)
            }
            try {
                executeOperation(obj = obj, actionCallback = { o: T -> realm.insertOrUpdate(o) }, realm = realm)
            } catch (e: Exception) {
                executeOperation(obj = obj, actionCallback = { o: T -> realm.copyToRealmOrUpdate(o) }, realm = realm)
            }
        }

    override suspend fun addList(list: List<T>): DataState<List<T>> =
        RealmProvider.instance.use { realm ->
            try {
                executeOperationList(lst = list, actionCallback = { o: List<T> -> realm.insertOrUpdate(o) }, realm = realm)
            } catch (e: Exception) {
                executeOperationList(lst = list, actionCallback = { o: List<T> -> realm.copyToRealmOrUpdate(o) }, realm = realm)
            }
        }

    /**
     * UPDATE
     */

    override suspend fun update(obj: T): DataState<T> =
        RealmProvider.instance.use { r ->
            executeOperation(obj = obj, actionCallback = { o: T -> r.copyToRealmOrUpdate(o) }, realm = r)
        }

    override suspend fun updateList(list: List<T>): DataState<List<T>> =
        RealmProvider.instance.use { r ->
            executeOperationList(lst = list, actionCallback = { o: List<T> -> r.copyToRealmOrUpdate(o) }, realm = r)
        }

    /**
     * RETRIEVE
     */

    override suspend fun retrieve(id: Long, primaryKey: String): DataState<T> = suspendCoroutine { continuation ->
        RealmProvider.instance.use { realm ->
            try {
                realm.executeTransaction { r ->
                    val result = r.where(modelClass)?.equalTo(primaryKey, id)?.findFirst()
                    result?.let {
                        val obj = r.copyFromRealm(result)
                        continuation.resume(DataState.Success(data = obj))
                    } ?: continuation.resume(DataState.Error(error = BaseError(cause = "Non existent object")))
                }
            } catch (e: Exception) {
                continuation.resume(DataState.Error(error = BaseError(cause = e.message ?: "Exception Unknown", exception = e)))
            }
        }
    }

    override suspend fun retrieveAll(): DataState<List<T>> = suspendCoroutine { continuation ->
        RealmProvider.instance.use { realm ->
            try {
                realm.executeTransaction { r ->
                    val result = r.where(modelClass)?.findAll()
                    result?.let {
                        val obj = r.copyFromRealm(it)
                        continuation.resume(DataState.Success(data = obj))
                    } ?: continuation.resume(DataState.Error(error = BaseError(cause = "Non existent object")))
                }
            } catch (e: Exception) {
                continuation.resume(DataState.Error(error = BaseError(cause = e.message ?: "Exception Unknown", exception = e)))
            }
        }
    }

    override suspend fun retrieveByQuery(query: (RealmQuery<T>) -> Unit): DataState<T> = suspendCoroutine { continuation ->
        RealmProvider.instance.use { realm ->
            try {
                realm.executeTransaction { r ->
                    val result = r.where(modelClass).apply(query).findFirst()
                    result?.let {
                        val obj = r.copyFromRealm(it)
                        continuation.resume(DataState.Success(data = obj))
                    } ?: continuation.resume(DataState.Error(error = BaseError(cause = "Non existent object")))
                }
            } catch (e: Exception) {
                //continuation.resumeWithException(e)
                continuation.resume(DataState.Error(error = BaseError(cause = e.message ?: "Exception Unknown", exception = e)))
            }
        }
    }

    /**
     * DELETE
     */

    override suspend fun delete(id: Long, primaryKey: String) = suspendCoroutine<Unit> { continuation ->
        RealmProvider.instance. use { realm ->
            try {
                realm.executeTransaction {
                    it.where(modelClass)
                        .equalTo(primaryKey, id)
                        .findFirst()
                        ?.deleteFromRealm()
                    continuation.resume(Unit)
                }
            } catch (e: Exception) {
                println("not object to delete")
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun deleteAll() = suspendCoroutine<Unit> { continuation ->
        RealmProvider.instance. use { realm ->
            ThreadInfoLogger.logThreadInfo(message = "delete all context")
            try {
                realm.executeTransaction {
                    it.where(modelClass)
                        .findAll()
                        .deleteAllFromRealm()
                }
                continuation.resume(Unit)
            } catch (e: Exception) {
                println("not object to delete")
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun deleteByQuery(query: (RealmQuery<T>) -> Unit) = suspendCoroutine<Unit> { continuation ->
        RealmProvider.instance.use { realm ->
            try {
                realm.executeTransaction {
                    it.where(modelClass).apply(query).findFirst()?.deleteFromRealm()
                    continuation.resume(Unit)
                }
            } catch (e: Exception) {
                continuation.resume(Unit)
            }
        }
    }

    /**
     * GET MAX ID
     */

    private suspend fun getMaxId(primaryKey: String): Long = obtainMaxId(primaryKey = primaryKey, realm = RealmProvider.instance)

    /**
     * PRIVATE OPERATION FUNCTIONS
     */

    private suspend fun executeOperation(obj: T, actionCallback: (T) -> Unit, realm: Realm) : DataState<T> = suspendCoroutine { continuation ->
        ThreadInfoLogger.logThreadInfo(message = "performOperation context")
        try {
            realm.executeTransaction {
                actionCallback.invoke(obj)
                continuation.resume(DataState.Success(data = obj))
            }
        } catch (e: IllegalStateException) {
            continuation.resumeWithException(e)
        } catch (e: IllegalArgumentException) {
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            continuation.resume(DataState.Error(error = BaseError(cause = e.message ?: "Exception Unknown", exception = e)))
        }
    }

    private suspend fun executeOperationList(lst: List<T>, actionCallback: (List<T>) -> Unit, realm: Realm) : DataState<List<T>> = suspendCoroutine { continuation ->
        ThreadInfoLogger.logThreadInfo(message = "performOperation context")
        try {
            realm.executeTransaction {
                actionCallback.invoke(lst)
                continuation.resume(DataState.Success(data = lst))
            }
        } catch (e: IllegalStateException) {
            continuation.resumeWithException(e)
        } catch (e: IllegalArgumentException) {
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            continuation.resume(DataState.Error(error = BaseError(cause = e.message ?: "Exception Unknown", exception = e)))
        }
    }

    private suspend fun obtainMaxId(primaryKey: String = "id", realm: Realm) : Long =
        coroutineScope {
            withContext(Dispatchers.Unconfined) {
                ThreadInfoLogger.logThreadInfo(message = "obtainMaxId context")
                obtainMaxIdAsync(primaryKey = primaryKey, realm = realm).await()
            }
        }

    private fun CoroutineScope.obtainMaxIdAsync(primaryKey: String, realm: Realm) : Deferred<Long> = async {
        ThreadInfoLogger.logThreadInfo(message = "obtainMaxIdAsync context")
        val max = realm.where(modelClass)?.max(primaryKey)
        return@async  max?.let {
            it as Long + 1
        } ?: 0L
    }

    /**
     * GENERIC MODEL CLASS
     */

    abstract val modelClass: Class<T>

}