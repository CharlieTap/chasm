package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AbsDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Abs

internal fun F32AbsInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F32Abs,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    F32AbsInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::F32AbsDispatcher,
    )

internal inline fun F32AbsInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F32Abs,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<F32Abs>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operand = loadFactory(context, instruction.operand)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        F32Abs(
            operand = operand,
            destination = destination,
        ),
    )
}
