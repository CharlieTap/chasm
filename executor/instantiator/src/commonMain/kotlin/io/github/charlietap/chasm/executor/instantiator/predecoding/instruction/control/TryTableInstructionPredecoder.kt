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
import io.github.charlietap.chasm.executor.invoker.dispatch.control.TryTableDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.TryTable
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

internal fun TryTableInstructionPredecoder(
    context: InstantiationContext,
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
    context: InstantiationContext,
    instruction: ControlInstruction.TryTable,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline tryTableDispatcher: Dispatcher<TryTable>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()

    tryTableDispatcher(
        TryTable(
            functionType = functionType,
            handlers = instruction.handlers,
            instructions = instruction.instructions.map { instructionPredecoder(context, it).bind() },
        ),
    )
}
