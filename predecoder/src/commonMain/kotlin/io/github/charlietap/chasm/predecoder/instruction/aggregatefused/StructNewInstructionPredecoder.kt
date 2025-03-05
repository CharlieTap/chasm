package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructNewDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.StructNew
import io.github.charlietap.chasm.type.ext.structType

internal fun StructNewInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructNew,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructNewInstructionPredecoder(
        context = context,
        instruction = instruction,
        storeFactory = storeFactory,
        dispatcher = ::StructNewDispatcher,
    )

internal inline fun StructNewInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructNew,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<StructNew>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destination = storeFactory(context, instruction.destination)
    val definedType = context.types[instruction.typeIndex.idx]
    val structType = context.unroller(definedType).compositeType.structType() ?: Err(
        InvocationError.StructCompositeTypeExpected,
    ).bind()

    dispatcher(
        StructNew(
            destination = destination,
            structType = structType,
            definedType = definedType,
        ),
    )
}
