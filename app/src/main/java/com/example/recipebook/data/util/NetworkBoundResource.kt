package com.example.recipebook.data.util

import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    networkMonitor: NetworkMonitor
): Flow<Resource<ResultType>> = flow {
    emit(Resource.Loading())

    val data = query().first()

    if (shouldFetch(data) && networkMonitor.isConnected.value) {
        try {
            emit(Resource.Loading())
            val networkResult = fetch()
            saveFetchResult(networkResult)
        } catch (e: Exception) {
            emit(Resource.Error("Ошибка загрузки: ${e.localizedMessage}", data))
        }
    } else if (shouldFetch(data) && !networkMonitor.isConnected.value) {
        emit(Resource.Error("Нет подключения к интернету", data))
    }
    emitAll(query().map { Resource.Success(it) })
}