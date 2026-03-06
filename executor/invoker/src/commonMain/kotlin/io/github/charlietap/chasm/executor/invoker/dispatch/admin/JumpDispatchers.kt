package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnCastExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnNonNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpTableExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpDispatcher(
    instruction: AdminInstruction.Jump,
) = JumpDispatcher(
    instruction = instruction,
    executor = ::JumpExecutor,
)

internal inline fun JumpDispatcher(
    instruction: AdminInstruction.Jump,
    crossinline executor: Executor<AdminInstruction.Jump>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}

fun JumpDispatcher(instruction: AdminInstruction.JumpIfI) = dispatchJumpInstruction(instruction, ::JumpExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpIfS) = dispatchJumpInstruction(instruction, ::JumpExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpTableI) = dispatchJumpInstruction(instruction, ::JumpTableExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpTableS) = dispatchJumpInstruction(instruction, ::JumpTableExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnNullI) = dispatchJumpInstruction(instruction, ::JumpOnNullExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnNullS) = dispatchJumpInstruction(instruction, ::JumpOnNullExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnNonNullI) = dispatchJumpInstruction(instruction, ::JumpOnNonNullExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnNonNullS) = dispatchJumpInstruction(instruction, ::JumpOnNonNullExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnCastI) = dispatchJumpInstruction(instruction, ::JumpOnCastExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnCastS) = dispatchJumpInstruction(instruction, ::JumpOnCastExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnCastFailI) = dispatchJumpInstruction(instruction, ::JumpOnCastExecutor)

fun JumpDispatcher(instruction: AdminInstruction.JumpOnCastFailS) = dispatchJumpInstruction(instruction, ::JumpOnCastExecutor)

private inline fun <reified T : AdminInstruction> dispatchJumpInstruction(
    instruction: T,
    crossinline executor: Executor<T>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
