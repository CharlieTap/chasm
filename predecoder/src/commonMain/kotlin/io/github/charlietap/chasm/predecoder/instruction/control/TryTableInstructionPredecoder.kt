package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.TryTableDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.predecoder.InstructionPredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.TryTable
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

internal fun TryTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.TryTable,
): Result<DispatchableInstruction, ModuleTrapError> =
    TryTableInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionPredecoder = ::InstructionPredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        tryTableDispatcher = ::TryTableDispatcher,
    )

internal inline fun TryTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.TryTable,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline tryTableDispatcher: Dispatcher<TryTable>,
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

    tryTableDispatcher(
        TryTable(
            params = functionType.params.types.size,
            results = functionType.results.types.size,
            handlers = instruction.handlers,
            instructions = instructions,
        ),
    )
}
