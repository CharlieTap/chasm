package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDefaultDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewDefault
import io.github.charlietap.chasm.type.ext.arrayType

internal fun ArrayNewDefaultInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewDefault,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewDefaultInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayNewDefaultDispatcher,
    )

internal inline fun ArrayNewDefaultInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewDefault,
    crossinline dispatcher: Dispatcher<ArrayNewDefault>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()
    val field = arrayType.fieldType.default(context)

    dispatcher(ArrayNewDefault(definedType, arrayType, field))
}
