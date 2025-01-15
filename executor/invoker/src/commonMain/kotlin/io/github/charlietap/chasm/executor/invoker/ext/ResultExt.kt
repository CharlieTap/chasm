package io.github.charlietap.chasm.executor.invoker.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException

@Throws(InvocationException::class)
fun <T, E: InvocationError> Result<T, E>.bind(): T = this.getOrThrow { e -> InvocationException(e) }
