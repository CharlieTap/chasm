package io.github.charlietap.chasm.executor.runtime.execution

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

typealias Executor<T> = (ExecutionContext, T) -> Result<Unit, InvocationError>
