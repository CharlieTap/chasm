package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.IfDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.If
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.type.ir.expansion.BlockTypeExpander

internal fun IfInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction.If,
): Result<DispatchableInstruction, ModuleTrapError> =
    IfInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionPredecoder = ::InstructionPredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        ifDispatcher = ::IfDispatcher,
    )

internal inline fun IfInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction.If,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline ifDispatcher: Dispatcher<If>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()
    val thenInstructions: Array<DispatchableInstruction> = Array(instruction.thenInstructions.size) { idx ->
        val reversedIndex = instruction.thenInstructions.size - 1 - idx
        val predispatch = instruction.thenInstructions[reversedIndex]
        instructionPredecoder(context, predispatch).bind()
    }
    val elseInstructions: Array<DispatchableInstruction> = instruction.elseInstructions?.let { instructions ->
        Array(instructions.size) { idx ->
            val reversedIndex = instructions.size - 1 - idx
            val predispatch = instructions[reversedIndex]
            instructionPredecoder(context, predispatch).bind()
        }
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
