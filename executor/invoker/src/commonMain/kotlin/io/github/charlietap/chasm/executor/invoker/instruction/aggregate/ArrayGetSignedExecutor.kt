package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayGetSignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGetSigned,
) =
    ArrayGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        signedUnpack = true,
        fieldUnpacker = ::FieldUnpacker,
    )
