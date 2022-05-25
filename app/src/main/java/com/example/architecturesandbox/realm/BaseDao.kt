package com.example.architecturesandbox.realm

import com.example.architecturesandbox.common.state.DataState
import io.realm.RealmQuery

abstract class BaseDao<T> {

    /**
     * ADD
     */

    abstract suspend fun add(obj: T, primaryKey: String, realmId: Long?): DataState<T>
    abstract suspend fun addList(list: List<T>): DataState<List<T>>

    /**
     * UPDATE
     */

    abstract suspend fun update(obj: T): DataState<T>
    abstract suspend fun updateList(list: List<T>): DataState<List<T>>

    /**
     * RETRIEVE
     */

    abstract suspend fun retrieve(id: Long, primaryKey: String): DataState<T>
    abstract suspend fun retrieveAll(): DataState<List<T>>
    abstract suspend fun retrieveByQuery(query: RealmQuery<T>.() -> Unit): DataState<T>

    /**
     * DELETE
     */

    abstract suspend fun delete(id: Long, primaryKey: String)
    abstract suspend fun deleteAll()
    abstract suspend fun deleteByQuery(query: (RealmQuery<T>) -> Unit)

}