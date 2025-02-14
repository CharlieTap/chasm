package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.numeric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.LoadFactory
import io.github.charlietap.chasm.executor.instantiator.predecoding.StoreFactory
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32SubDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction

internal fun FusedNumericInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedNumericInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedNumericInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        i32AddDispatcher = ::I32AddDispatcher,
        i32SubDispatcher = ::I32SubDispatcher,
        f32AbsDispatcher = ::F32AbsDispatcher,
    )

internal inline fun FusedNumericInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedNumericInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline i32AddDispatcher: Dispatcher<I32Add>,
    crossinline i32SubDispatcher: Dispatcher<I32Sub>,
    crossinline f32AbsDispatcher: Dispatcher<F32Abs>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedNumericInstruction.I32Add -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32AddDispatcher(
                I32Add(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.F32Abs -> {

            val operand = loadFactory(context, instruction.operand)
            val destination = storeFactory(context, instruction.destination)

            f32AbsDispatcher(
                F32Abs(
                    operand = operand,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Sub -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32SubDispatcher(
                I32Sub(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
    }
}
