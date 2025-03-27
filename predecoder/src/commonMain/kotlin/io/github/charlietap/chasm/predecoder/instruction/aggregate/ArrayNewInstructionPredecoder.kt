package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNew
import io.github.charlietap.chasm.type.ext.arrayType

internal fun ArrayNewInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNew,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayNewDispatcher,
    )

internal inline fun ArrayNewInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNew,
    crossinline dispatcher: Dispatcher<ArrayNew>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]

    dispatcher(ArrayNew(rtt, arrayType))
}
