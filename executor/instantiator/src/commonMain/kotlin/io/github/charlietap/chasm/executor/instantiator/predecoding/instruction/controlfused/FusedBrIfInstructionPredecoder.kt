package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.controlfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.LoadFactory
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrIfDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.FusedControlInstruction.BrIf
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction

internal fun FusedBrIfInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedControlInstruction.BrIf,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedBrIfInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        brIfDispatcher = ::BrIfDispatcher,
    )

internal inline fun FusedBrIfInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedControlInstruction.BrIf,
    crossinline loadFactory: LoadFactory,
    crossinline brIfDispatcher: Dispatcher<BrIf>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val operand = loadFactory(context, instruction.operand)

    brIfDispatcher(
        BrIf(
            operand = operand,
            labelIndex = instruction.labelIndex,
        ),
    )
}
