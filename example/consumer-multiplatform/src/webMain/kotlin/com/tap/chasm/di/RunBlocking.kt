package com.tap.chasm.di

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

internal actual fun <T> runBlocking(block: suspend () -> T): T {
    var result: Result<T>? = null
    block.startCoroutine(
        Continuation(EmptyCoroutineContext) { outcome ->
            result = outcome
        },
    )
    return checkNotNull(result).getOrThrow()
}
