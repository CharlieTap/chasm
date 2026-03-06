package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BlockDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.InstructionSequencePredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Block
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

internal fun BlockInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Block,
): Result<DispatchableInstruction, ModuleTrapError> =
    BlockInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionSequencePredecoder = ::InstructionSequencePredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        blockDispatcher = ::BlockDispatcher,
    )

internal inline fun BlockInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Block,
    crossinline instructionSequencePredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, Array<DispatchableInstruction>>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline blockDispatcher: Dispatcher<Block>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()

    blockDispatcher(
        Block(
            params = functionType.params.types.size,
            results = functionType.results.types.size,
            instructions = instructionSequencePredecoder(context, instruction.instructions).bind(),
        ),
    )
}
