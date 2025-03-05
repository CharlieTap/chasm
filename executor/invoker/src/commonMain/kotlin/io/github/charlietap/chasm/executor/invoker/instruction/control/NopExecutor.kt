package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

internal inline fun NopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Nop,
) = Unit
