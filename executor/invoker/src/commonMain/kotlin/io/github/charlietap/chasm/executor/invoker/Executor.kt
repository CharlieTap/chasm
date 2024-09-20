package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias Executor<T> = (ExecutionContext, T) -> Result<Unit, InvocationError>
