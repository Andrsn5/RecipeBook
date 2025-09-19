package com.example.recipebook.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<ResultType> = flow {
    val data = query().first()
    emit(data)

    if (shouldFetch(data)) {
        try {
            val fetched = fetch()
            saveFetchResult(fetched)
        } catch (e: Exception) {
            emitAll(query())
        }
    }

    emitAll(query())
}