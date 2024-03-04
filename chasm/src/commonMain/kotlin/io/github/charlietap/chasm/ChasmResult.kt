package io.github.charlietap.chasm

import io.github.charlietap.chasm.error.ChasmError
import kotlin.jvm.JvmInline

sealed interface ChasmResult<out S, out E>
    where E : ChasmError {
    @JvmInline
    value class Success<out S>(val result: S) : ChasmResult<S, Nothing>

    @JvmInline
    value class Error<out E : ChasmError>(val error: E) : ChasmResult<Nothing, E>
}

fun <E : ChasmError, S, T> ChasmResult<S, E>.map(transformation: (S) -> T): ChasmResult<T, E> {
    return when (this) {
        is ChasmResult.Success -> ChasmResult.Success(transformation(this.result))
        is ChasmResult.Error -> this
    }
}

fun <E : ChasmError, S, T> ChasmResult<S, E>.flatMap(transformation: (S) -> ChasmResult<T, E>): ChasmResult<T, E> {
    return when (this) {
        is ChasmResult.Success -> transformation(this.result)
        is ChasmResult.Error -> this
    }
}
