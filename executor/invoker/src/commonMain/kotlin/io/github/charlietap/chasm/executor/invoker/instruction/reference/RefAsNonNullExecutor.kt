package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias RefAsNonNullExecutor = (Stack) -> Result<Unit, InvocationError>
