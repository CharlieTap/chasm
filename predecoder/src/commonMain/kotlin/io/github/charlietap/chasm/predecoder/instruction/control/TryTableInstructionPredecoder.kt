package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.TryTableDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.InstructionSequencePredecoder
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
        instructionSequencePredecoder = ::InstructionSequencePredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        tryTableDispatcher = ::TryTableDispatcher,
    )

internal inline fun TryTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.TryTable,
    crossinline instructionSequencePredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, Array<DispatchableInstruction>>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline tryTableDispatcher: Dispatcher<TryTable>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.runtimeTypes, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()

    tryTableDispatcher(
        TryTable(
            params = functionType.params.types.size,
            results = functionType.results.types.size,
            handlers = instruction.handlers,
            instructions = instructionSequencePredecoder(context, instruction.instructions).bind(),
            payloadDestinationSlots = instruction.payloadDestinationSlots,
        ),
    )
}
