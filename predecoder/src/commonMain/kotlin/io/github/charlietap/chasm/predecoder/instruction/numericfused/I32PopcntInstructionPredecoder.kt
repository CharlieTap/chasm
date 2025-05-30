package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32PopcntDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Popcnt

internal fun I32PopcntInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I32Popcnt,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32PopcntInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::I32PopcntDispatcher,
    )

internal inline fun I32PopcntInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I32Popcnt,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<I32Popcnt>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operand = loadFactory(context, instruction.operand)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        I32Popcnt(
            operand = operand,
            destination = destination,
        ),
    )
}
