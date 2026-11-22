package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.UncaughtWasmException
import io.github.charlietap.chasm.embedding.error.WasmTrapException
import kotlin.jvm.JvmInline

sealed interface ChasmResult<out S, out E> {
    @JvmInline
    value class Success<out S>(val result: S) : ChasmResult<S, Nothing>

    @JvmInline
    value class Error<out E>(val error: E) : ChasmResult<Nothing, E>
}

fun <E, S, T> ChasmResult<S, E>.map(transformation: (S) -> T): ChasmResult<T, E> {
    return when (this) {
        is ChasmResult.Success -> ChasmResult.Success(transformation(this.result))
        is ChasmResult.Error -> this
    }
}

fun <E, S, T> ChasmResult<S, E>.flatMap(transformation: (S) -> ChasmResult<T, E>): ChasmResult<T, E> {
    return when (this) {
        is ChasmResult.Success -> transformation(this.result)
        is ChasmResult.Error -> this
    }
}

fun <S, E, T> ChasmResult<S, E>.fold(
    onSuccess: (S) -> T,
    onError: (E) -> T,
): T {
    return when (this) {
        is ChasmResult.Success -> onSuccess(result)
        is ChasmResult.Error -> onError(error)
    }
}

fun <S, E> ChasmResult<S, E>.getOrNull(): S? {
    return when (this) {
        is ChasmResult.Success -> this.result
        is ChasmResult.Error -> null
    }
}

fun <S, E> ChasmResult<S, E>.getOrElse(defaultValue: S): S {
    return when (this) {
        is ChasmResult.Success -> result
        is ChasmResult.Error -> defaultValue
    }
}

fun <S, E> ChasmResult<S, E>.expect(message: String): S {
    return when (this) {
        is ChasmResult.Success -> this.result
        is ChasmResult.Error -> throw expectFailure(message, error)
    }
}

private fun expectFailure(
    message: String,
    error: Any?,
): Throwable {
    return when (error) {
        is ExecutionError.Trapped -> WasmTrapException(error.trap, "$message: ${error.trap}")
        is ExecutionError.Thrown<*> -> UncaughtWasmException(error.exception, "$message: uncaught exception ${error.exception}")
        else -> IllegalStateException("$message: $error")
    }
}

fun <S, E> ChasmResult<S, E>.onSuccess(action: (S) -> Unit): ChasmResult<S, E> {
    if (this is ChasmResult.Success) {
        action(result)
    }
    return this
}

fun <S, E> ChasmResult<S, E>.onError(action: (E) -> Unit): ChasmResult<S, E> {
    if (this is ChasmResult.Error) {
        action(error)
    }
    return this
}
