package com.example.recipebook.presentation.ui.state


sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    object Empty : UiState<Nothing>()
    data class Error<T>(val message: String, val data: T? = null): UiState<T>()
}