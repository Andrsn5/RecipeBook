package com.example.recipebook.data.util

import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline  fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,              // load from DB
    crossinline fetch: suspend () -> RequestType,           // load from network
    crossinline saveFetchResult: suspend (RequestType) -> Unit, // save to DB
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    networkMonitor: NetworkMonitor
): Flow<Resource<ResultType>> = flow {
    emit(Resource.Loading())

    val data = query().first()

    val isOnline = networkMonitor.isConnected.value
    if (shouldFetch(data) && isOnline) {
        try {
            val networkResult = fetch()
            saveFetchResult(networkResult)
            emitAll(query().map { Resource.Success(it) })
        } catch (e: Exception) {
            emit(Resource.Error("Ошибка сети: ${e.localizedMessage}", data))
        }
    } else {
        if (!isOnline) {
            emit(Resource.Error("Нет подключения к интернету", data))
        }
        emitAll(query().map { Resource.Success(it) })
    }
}