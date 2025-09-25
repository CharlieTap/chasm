package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64MulWideSDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64MulWideS

internal fun I64MulWideSInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I64MulWideS,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> = I64MulWideSInstructionPredecoder(
    context = context,
    instruction = instruction,
    loadFactory = loadFactory,
    storeFactory = storeFactory,
    dispatcher = ::I64MulWideSDispatcher,
)

internal inline fun I64MulWideSInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I64MulWideS,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<I64MulWideS>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val left = loadFactory(context, instruction.left)
    val right = loadFactory(context, instruction.right)
    val destLow = storeFactory(context, instruction.destinationLow)
    val destHigh = storeFactory(context, instruction.destinationHigh)

    dispatcher(
        I64MulWideS(
            left = left,
            right = right,
            destinationLow = destLow,
            destinationHigh = destHigh,
        ),
    )
}
