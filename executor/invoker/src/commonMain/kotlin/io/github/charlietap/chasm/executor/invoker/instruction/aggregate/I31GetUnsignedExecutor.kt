package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun I31GetUnsignedExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.I31GetUnsigned,
) = I31GetExecutor(
    vstack = vstack,
    context = context,
    extender = UInt::extendUnsigned,
)
