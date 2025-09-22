package com.example.recipebook.data.util

import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.delay
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

    // Всегда показываем данные из БД, если они есть
    if (data != null && data is Collection<*> && (data as Collection<*>).isNotEmpty()) {
        emit(Resource.Success(data))
    }

    // Загружаем из сети только если нужно и есть интернет
    if (shouldFetch(data) && networkMonitor.isConnected.value) {
        try {
            emit(Resource.Loading())
            val networkResult = fetch()
            saveFetchResult(networkResult)
            // После сохранения эмитим обновленные данные
            emitAll(query().map { Resource.Success(it) })
        } catch (e: Exception) {
            emit(Resource.Error("Ошибка загрузки: ${e.localizedMessage}", data))
        }
    } else if (shouldFetch(data) && !networkMonitor.isConnected.value) {
        // Нужны свежие данные, но нет интернета
        if (data == null || data is Collection<*> && (data as Collection<*>).isEmpty()) {
            emit(Resource.Error("Нет подключения к интернету"))
        }
    }
}