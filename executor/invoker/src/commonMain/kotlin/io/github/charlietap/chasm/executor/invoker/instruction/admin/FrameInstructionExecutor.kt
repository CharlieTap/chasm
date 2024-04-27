package io.github.charlietap.chasm.executor.invoker.instruction.admin

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias FrameInstructionExecutor = (Stack.Entry.ActivationFrame, Stack) -> Result<Unit, InvocationError>
