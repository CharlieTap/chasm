package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayGetUnsignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGetUnsigned,
) =
    ArrayGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        signedUnpack = false,
        fieldUnpacker = ::FieldUnpacker,
    )
