package io.github.charlietap.chasm.stack

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
fun <T> Stack<T>.pop(): Result<T, StackError> = popOrNull().toResultOr {
    StackError.StackUnderflow
}

@InternalChasmApi
fun <T> Stack<T>.peek(): Result<T, StackError> = peekOrNull().toResultOr {
    StackError.StackUnderflow
}

@InternalChasmApi
fun <T> Stack<T>.peekNth(n: Int): Result<T, StackError> = peekNthOrNull(n).toResultOr {
    StackError.StackUnderflow
}
