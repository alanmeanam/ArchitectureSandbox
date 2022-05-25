package com.example.architecturesandbox.realm

import com.example.architecturesandbox.common.state.DataState
import io.realm.RealmQuery
import kotlinx.coroutines.flow.Flow

interface RealmBaseContract<T> {
    suspend fun add(obj: T, primaryKey: String = "id", realmId: Long? = null): Flow<DataState<T>>
    suspend fun addList(list: List<T>): Flow<DataState<List<T>>>
    suspend fun update(obj: T): Flow<DataState<T>>
    suspend fun updateList(list: List<T>): Flow<DataState<List<T>>>
    suspend fun retrieve(id: Long, primaryKey: String = "id"): Flow<DataState<T>>
    suspend fun retrieveAll(): Flow<DataState<List<T>>>
    suspend fun retrieveByQuery(query: RealmQuery<T>.() -> Unit): Flow<DataState<T>>
    suspend fun delete(id: Long, primaryKey: String = "id")
    suspend fun deleteAll()
    suspend fun deleteByQuery(query: (RealmQuery<T>) -> Unit)
}