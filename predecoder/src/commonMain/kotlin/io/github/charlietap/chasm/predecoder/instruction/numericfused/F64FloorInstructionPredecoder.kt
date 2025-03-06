package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64FloorDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Floor

internal fun F64FloorInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F64Floor,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    F64FloorInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::F64FloorDispatcher,
    )

internal inline fun F64FloorInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F64Floor,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<F64Floor>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operand = loadFactory(context, instruction.operand)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        F64Floor(
            operand = operand,
            destination = destination,
        ),
    )
}
