package ru.rozum.gitTest.presentation.fragment.util

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

inline fun <T> collect(
    lifecycle: LifecycleOwner,
    consumer: Flow<T>,
    crossinline function: (T) -> Unit
) {
    lifecycle.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            consumer.collectLatest {
                function(it)
            }
        }
    }
}

fun <T> setVisibility(state: T, expected: T): Int =
    if (state == expected) View.VISIBLE else View.GONE