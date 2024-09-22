package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayGetSignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGetSigned,
): Result<Unit, InvocationError> =
    ArrayGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        signedUnpack = true,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldUnpacker = ::FieldUnpacker,
    )
