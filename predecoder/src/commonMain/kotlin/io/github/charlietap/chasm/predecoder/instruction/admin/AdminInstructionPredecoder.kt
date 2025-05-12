package io.github.charlietap.chasm.predecoder.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndBlockDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PauseDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PauseIfDispatcher
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
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
        endBlockDispatcher = ::EndBlockDispatcher,
        endFunctionDispatcher = ::EndFunctionDispatcher,
        pauseInstructionDispatcher = ::PauseDispatcher,
        pauseIfInstructionDispatcher = ::PauseIfDispatcher,
    )

internal inline fun AdminInstructionPredecoder(
    context: PredecodingContext,
    instruction: AdminInstruction,
    crossinline endBlockDispatcher: Dispatcher<EndBlock>,
    crossinline endFunctionDispatcher: Dispatcher<EndFunction>,
    crossinline pauseInstructionDispatcher: Dispatcher<Pause>,
    crossinline pauseIfInstructionDispatcher: Dispatcher<PauseIf>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AdminInstruction.EndBlock -> endBlockDispatcher(EndBlock)
        is AdminInstruction.EndFunction -> endFunctionDispatcher(EndFunction)
        is AdminInstruction.Pause -> pauseInstructionDispatcher(Pause)
        is AdminInstruction.PauseIf -> pauseIfInstructionDispatcher(PauseIf)
        is AdminInstruction.Jump -> TODO()
        is AdminInstruction.JumpIf -> TODO()
        is AdminInstruction.JumpOnCast -> TODO()
        is AdminInstruction.JumpOnCastFail -> TODO()
        is AdminInstruction.JumpOnNonNull -> TODO()
        is AdminInstruction.JumpOnNull -> TODO()
        is AdminInstruction.JumpTable -> TODO()
    }
}
