package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BlockDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Block
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

internal fun BlockInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction.Block,
): Result<DispatchableInstruction, ModuleTrapError> =
    BlockInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionPredecoder = ::InstructionPredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        blockDispatcher = ::BlockDispatcher,
    )

internal inline fun BlockInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction.Block,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline blockDispatcher: Dispatcher<Block>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()

    blockDispatcher(
        Block(
            functionType = functionType,
            instructions = instruction.instructions.map { instructionPredecoder(context, it).bind() },
        ),
    )
}
