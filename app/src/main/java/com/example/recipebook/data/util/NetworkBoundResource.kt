package com.example.recipebook.data.util

import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    networkMonitor: NetworkMonitor
): Flow<Resource<ResultType>> = channelFlow {
    send(Resource.Loading())

    val data = query().first()

    if (shouldFetch(data)) {
        if (networkMonitor.isConnected.value) {
            try {
                saveFetchResult(fetch())
            } catch (e: Exception) {
                send(Resource.Error("Ошибка загрузки: ${e.localizedMessage}", data))
            }
        } else {
            send(Resource.Error("Нет подключения к интернету", data))
        }
    }

    query()
        .flatMapLatest { flowOf(Resource.Success(it)) }
        .collect { send(it) }
}.flowOn(Dispatchers.IO)

