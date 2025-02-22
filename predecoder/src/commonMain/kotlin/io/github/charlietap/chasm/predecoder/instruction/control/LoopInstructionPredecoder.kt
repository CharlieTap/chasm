package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Loop
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.predecoder.InstructionPredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.type.ir.expansion.BlockTypeExpander

internal fun LoopInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Loop,
): Result<DispatchableInstruction, ModuleTrapError> =
    LoopInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionPredecoder = ::InstructionPredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        loopDispatcher = ::LoopDispatcher,
    )

internal inline fun LoopInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Loop,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline loopDispatcher: Dispatcher<Loop>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()

    val instructions: Array<DispatchableInstruction> = Array(instruction.instructions.size) { idx ->
        val reversedIndex = instruction.instructions.size - 1 - idx
        val predispatch = instruction.instructions[reversedIndex]
        instructionPredecoder(context, predispatch).bind()
    }

    loopDispatcher(
        Loop(
            params = functionType.params.types.size,
            instructions = instructions,
        ),
    )
}
