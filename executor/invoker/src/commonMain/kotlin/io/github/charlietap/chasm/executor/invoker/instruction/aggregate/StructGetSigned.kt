package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructGetSignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGetSigned,
): Result<Unit, InvocationError> =
    StructGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        fieldIndex = instruction.fieldIndex,
        signedUnpack = true,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldUnpacker = ::FieldUnpacker,
    )
