package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncSatF32SDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32TruncSatF32S

internal fun I32TruncSatF32SInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I32TruncSatF32S,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32TruncSatF32SInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::I32TruncSatF32SDispatcher,
    )

internal inline fun I32TruncSatF32SInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I32TruncSatF32S,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<I32TruncSatF32S>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operand = loadFactory(context, instruction.operand)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        I32TruncSatF32S(
            operand = operand,
            destination = destination,
        ),
    )
}
