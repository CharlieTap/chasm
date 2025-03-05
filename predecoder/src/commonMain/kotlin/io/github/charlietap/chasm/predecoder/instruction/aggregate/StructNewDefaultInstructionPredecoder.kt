package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructNewDefaultDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructNewDefault
import io.github.charlietap.chasm.type.ext.structType

internal fun StructNewDefaultInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructNewDefault,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructNewDefaultInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::StructNewDefaultDispatcher,
    )

internal inline fun StructNewDefaultInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructNewDefault,
    crossinline dispatcher: Dispatcher<StructNewDefault>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val definedType = context.types[instruction.typeIndex.idx]
    val structType = context.unroller(definedType).compositeType.structType() ?: Err(
        InvocationError.StructCompositeTypeExpected,
    ).bind()
    val fields = LongArray(structType.fields.size) { idx ->
        structType.fields[idx].default(context)
    }

    dispatcher(StructNewDefault(definedType, structType, fields))
}
