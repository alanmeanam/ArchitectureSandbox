package com.example.architecturesandbox.realm

import com.example.architecturesandbox.common.state.DataState
import io.realm.RealmObject
import io.realm.RealmQuery

class RealmRepositoryImpl<T: RealmObject>(private val objClass: Class<T>): RealmBaseDaoHandler<T>() {

    override suspend fun add(obj: T, primaryKey: String, realmId: Long?): DataState<T> = super.add(obj, primaryKey, realmId)

    override suspend fun addList(list: List<T>): DataState<List<T>> = super.addList(list)

    override suspend fun update(obj: T): DataState<T> = super.update(obj)

    override suspend fun updateList(list: List<T>): DataState<List<T>> = super.updateList(list)

    override suspend fun retrieve(id: Long, primaryKey: String): DataState<T> = super.retrieve(id, primaryKey)

    override suspend fun retrieveAll(): DataState<List<T>> = super.retrieveAll()

    override suspend fun retrieveByQuery(query: RealmQuery<T>.() -> Unit): DataState<T> = super.retrieveByQuery(query)

    override suspend fun delete(id: Long, primaryKey: String) = super.delete(id, primaryKey)

    override suspend fun deleteAll() = super.deleteAll()

    override suspend fun deleteByQuery(query: (RealmQuery<T>) -> Unit) = super.deleteByQuery(query)


    //---------------------------------------

    override val modelClass: Class<T>
        get() = objClass

}