package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun NopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Nop,
) = Unit
