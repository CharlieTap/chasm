package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32RotrDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Rotr

internal fun I32RotrInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I32Rotr,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32RotrInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::I32RotrDispatcher,
    )

internal inline fun I32RotrInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I32Rotr,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<I32Rotr>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val left = loadFactory(context, instruction.left)
    val right = loadFactory(context, instruction.right)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        I32Rotr(
            left = left,
            right = right,
            destination = destination,
        ),
    )
}
