package com.example.recipebook.presentation.ui.util

import android.graphics.Color
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import androidx.core.graphics.toColorInt

fun Fragment.showError(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        .setBackgroundTint("#B00020".toColorInt())
        .setTextColor(Color.WHITE)
        .show()
}

fun Fragment.showOfflineBanner(onRetry: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(
        requireView(),
        "Нет подключения. Показаны локальные данные.",
        Snackbar.LENGTH_LONG
    )
        .setBackgroundTint("#FF6F00".toColorInt())
        .setTextColor(Color.WHITE)
    
    if (onRetry != null) {
        snackbar.setAction("Повторить") { onRetry() }
            .setActionTextColor(Color.WHITE)
    }
    snackbar.show()
}
