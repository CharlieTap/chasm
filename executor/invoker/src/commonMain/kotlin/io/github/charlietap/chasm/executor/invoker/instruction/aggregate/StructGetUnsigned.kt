package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun StructGetUnsignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGetUnsigned,
) = StructGetExecutor(
    context = context,
    typeIndex = instruction.typeIndex,
    fieldIndex = instruction.fieldIndex,
    signedUnpack = false,
    fieldUnpacker = ::FieldUnpacker,
)
