package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64GeUDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64GeU

internal fun I64GeUInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I64GeU,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    I64GeUInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::I64GeUDispatcher,
    )

internal inline fun I64GeUInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I64GeU,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<I64GeU>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val left = loadFactory(context, instruction.left)
    val right = loadFactory(context, instruction.right)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        I64GeU(
            left = left,
            right = right,
            destination = destination,
        ),
    )
}
