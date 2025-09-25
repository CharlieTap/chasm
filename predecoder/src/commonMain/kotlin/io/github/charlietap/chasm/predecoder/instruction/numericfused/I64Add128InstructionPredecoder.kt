package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64Add128Dispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64Add128

internal fun I64Add128InstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I64Add128,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> = I64Add128InstructionPredecoder(
    context = context,
    instruction = instruction,
    loadFactory = loadFactory,
    storeFactory = storeFactory,
    dispatcher = ::I64Add128Dispatcher,
)

internal inline fun I64Add128InstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.I64Add128,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<I64Add128>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val leftLow = loadFactory(context, instruction.leftLow)
    val leftHigh = loadFactory(context, instruction.leftHigh)
    val rightLow = loadFactory(context, instruction.rightLow)
    val rightHigh = loadFactory(context, instruction.rightHigh)
    val destLow = storeFactory(context, instruction.destinationLow)
    val destHigh = storeFactory(context, instruction.destinationHigh)

    dispatcher(
        I64Add128(
            leftLow = leftLow,
            leftHigh = leftHigh,
            rightLow = rightLow,
            rightHigh = rightHigh,
            destinationLow = destLow,
            destinationHigh = destHigh,
        ),
    )
}
