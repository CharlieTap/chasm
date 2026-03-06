package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.IfDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.InstructionSequencePredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.If
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

internal fun IfInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.If,
): Result<DispatchableInstruction, ModuleTrapError> =
    IfInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionSequencePredecoder = ::InstructionSequencePredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        ifDispatcher = ::IfDispatcher,
    )

internal inline fun IfInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.If,
    crossinline instructionSequencePredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, Array<DispatchableInstruction>>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline ifDispatcher: Dispatcher<If>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()
    val thenInstructions = instructionSequencePredecoder(context, instruction.thenInstructions).bind()
    val elseInstructions = instruction.elseInstructions?.let { instructions ->
        instructionSequencePredecoder(context, instructions).bind()
    } ?: emptyArray()
    val instructions = arrayOf(elseInstructions, thenInstructions)

    ifDispatcher(
        If(
            params = functionType.params.types.size,
            results = functionType.results.types.size,
            instructions = instructions,
        ),
    )
}
