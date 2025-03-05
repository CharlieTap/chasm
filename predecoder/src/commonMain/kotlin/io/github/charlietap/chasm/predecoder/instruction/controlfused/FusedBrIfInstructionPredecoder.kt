package io.github.charlietap.chasm.predecoder.instruction.controlfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrIfDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction.BrIf

internal fun FusedBrIfInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedControlInstruction.BrIf,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedBrIfInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        brIfDispatcher = ::BrIfDispatcher,
    )

internal inline fun FusedBrIfInstructionPredecoder(
    context: PredecodingContext,
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
