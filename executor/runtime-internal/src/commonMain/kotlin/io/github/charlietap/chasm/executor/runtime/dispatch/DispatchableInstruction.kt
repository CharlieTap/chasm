package io.github.charlietap.chasm.executor.runtime.dispatch

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext

typealias DispatchableInstruction = (ExecutionContext) -> Result<Unit, InvocationError>
