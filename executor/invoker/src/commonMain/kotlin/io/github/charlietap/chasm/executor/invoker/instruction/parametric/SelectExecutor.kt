package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias SelectExecutor = (Stack) -> Result<Unit, InvocationError>
