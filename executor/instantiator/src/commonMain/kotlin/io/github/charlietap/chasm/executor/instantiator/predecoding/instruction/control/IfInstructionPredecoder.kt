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
import io.github.charlietap.chasm.executor.invoker.dispatch.control.IfDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.If
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

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

    ifDispatcher(
        If(
            functionType = functionType,
            thenInstructions = instruction.thenInstructions.map { instructionPredecoder(context, it).bind() },
            elseInstructions = instruction.elseInstructions?.map { instructionPredecoder(context, it).bind() },
        ),
    )
}
