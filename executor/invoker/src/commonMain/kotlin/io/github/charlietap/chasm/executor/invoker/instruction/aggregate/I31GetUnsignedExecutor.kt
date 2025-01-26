package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun I31GetUnsignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.I31GetUnsigned,
) = I31GetExecutor(
    context = context,
    extender = UInt::extendUnsigned,
)
