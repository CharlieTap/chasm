package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.InstructionSequencePredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Loop
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

internal fun LoopInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Loop,
): Result<DispatchableInstruction, ModuleTrapError> =
    LoopInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionSequencePredecoder = ::InstructionSequencePredecoder,
        blockTypeExpander = ::BlockTypeExpander,
        loopDispatcher = ::LoopDispatcher,
    )

internal inline fun LoopInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Loop,
    crossinline instructionSequencePredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, Array<DispatchableInstruction>>,
    crossinline blockTypeExpander: BlockTypeExpander,
    crossinline loopDispatcher: Dispatcher<Loop>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val functionType = blockTypeExpander(context.types, instruction.blockType)
        .toResultOr {
            InstantiationError.PredecodingError
        }.bind()

    loopDispatcher(
        Loop(
            params = functionType.params.types.size,
            instructions = instructionSequencePredecoder(context, instruction.instructions).bind(),
        ),
    )
}
