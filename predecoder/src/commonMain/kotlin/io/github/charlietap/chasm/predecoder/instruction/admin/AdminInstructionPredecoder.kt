package io.github.charlietap.chasm.predecoder.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndBlockDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PauseDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PauseIfDispatcher
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.CopySlots
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.EndBlock
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.EndFunction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.Pause
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.PauseIf

internal fun AdminInstructionPredecoder(
    context: PredecodingContext,
    instruction: AdminInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    AdminInstructionPredecoder(
        context = context,
        instruction = instruction,
        copySlotsDispatcher = ::CopySlotsDispatcher,
        endBlockDispatcher = ::EndBlockDispatcher,
        endFunctionDispatcher = ::EndFunctionDispatcher,
        pauseInstructionDispatcher = ::PauseDispatcher,
        pauseIfInstructionDispatcher = ::PauseIfDispatcher,
    )

internal inline fun AdminInstructionPredecoder(
    context: PredecodingContext,
    instruction: AdminInstruction,
    crossinline copySlotsDispatcher: Dispatcher<CopySlots>,
    crossinline endBlockDispatcher: Dispatcher<EndBlock>,
    crossinline endFunctionDispatcher: Dispatcher<EndFunction>,
    crossinline pauseInstructionDispatcher: Dispatcher<Pause>,
    crossinline pauseIfInstructionDispatcher: Dispatcher<PauseIf>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AdminInstruction.CopySlots -> copySlotsDispatcher(
            CopySlots(
                sourceSlots = instruction.sourceSlots,
                destinationSlots = instruction.destinationSlots,
            ),
        )
        is AdminInstruction.EndBlock -> endBlockDispatcher(EndBlock)
        is AdminInstruction.EndFunction -> endFunctionDispatcher(EndFunction)
        is AdminInstruction.PushHandler -> error("push_handler requires instruction-sequence predecoding")
        is AdminInstruction.PopHandler -> error("pop_handler requires instruction-sequence predecoding")
        is AdminInstruction.Pause -> pauseInstructionDispatcher(Pause)
        is AdminInstruction.PauseIf -> pauseIfInstructionDispatcher(PauseIf)
        is AdminInstruction.Jump -> error("jump requires instruction-sequence predecoding")
        is AdminInstruction.JumpIf -> error("jump_if requires instruction-sequence predecoding")
        is AdminInstruction.JumpOnCast -> error("jump_on_cast requires instruction-sequence predecoding")
        is AdminInstruction.JumpOnCastFail -> error("jump_on_cast_fail requires instruction-sequence predecoding")
        is AdminInstruction.JumpOnNonNull -> error("jump_on_non_null requires instruction-sequence predecoding")
        is AdminInstruction.JumpOnNull -> error("jump_on_null requires instruction-sequence predecoding")
        is AdminInstruction.JumpTable -> error("jump_table requires instruction-sequence predecoding")
    }
}
