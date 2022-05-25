package com.example.architecturesandbox.realm

import com.example.architecturesandbox.common.state.DataState
import io.realm.RealmObject
import io.realm.RealmQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealmBaseInteractor<T: RealmObject> @Inject constructor(objClass: Class<T>): RealmBaseContract<T> {

    private val dispatcher = Dispatchers.IO
    private val repository: RealmRepositoryImpl<T> = RealmRepositoryImpl(objClass = objClass)

    override suspend fun add(obj: T, primaryKey: String, realmId: Long?): Flow<DataState<T>> = flow { emit(repository.add(obj = obj, primaryKey = primaryKey, realmId = realmId)) }.flowOn(dispatcher)

    override suspend fun addList(list: List<T>): Flow<DataState<List<T>>> = flow { emit(repository.addList(list = list) ) }.flowOn(dispatcher)

    override suspend fun update(obj: T): Flow<DataState<T>> = flow { emit(repository.update(obj = obj)) }.flowOn(dispatcher)

    override suspend fun updateList(list: List<T>): Flow<DataState<List<T>>> = flow { emit(repository.updateList(list = list)) }.flowOn(dispatcher)

    override suspend fun retrieve(id: Long, primaryKey: String): Flow<DataState<T>> = flow { emit(repository.retrieve(id = id, primaryKey = primaryKey)) }.flowOn(dispatcher)

    override suspend fun retrieveAll(): Flow<DataState<List<T>>> = flow { emit(repository.retrieveAll()) }.flowOn(dispatcher)

    override suspend fun retrieveByQuery(query: RealmQuery<T>.() -> Unit): Flow<DataState<T>> = flow { emit(repository.retrieveByQuery(query)) }.flowOn(dispatcher)

    override suspend fun delete(id: Long, primaryKey: String) = withContext(dispatcher) { repository.delete(id = id, primaryKey = primaryKey) }

    override suspend fun deleteAll() = withContext(dispatcher) { repository.deleteAll() }

    override suspend fun deleteByQuery(query: (RealmQuery<T>) -> Unit) = withContext(Dispatchers.IO) { repository.deleteByQuery(query) }

}