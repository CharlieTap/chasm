package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun ExecutionInstructionExecutorImpl(
    instruction: ExecutionInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    ExecutionInstructionExecutorImpl(
        instruction = instruction,
        store = store,
        stack = stack,
        adminInstructionExecutor = ::AdminInstructionExecutorImpl,
        astInstructionExecutor = ::ModuleInstructionExecutorImpl,
    )

internal fun ExecutionInstructionExecutorImpl(
    instruction: ExecutionInstruction,
    store: Store,
    stack: Stack,
    adminInstructionExecutor: AdminInstructionExecutor,
    astInstructionExecutor: ModuleInstructionExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AdminInstruction -> adminInstructionExecutor(instruction, store, stack).bind()
        is ModuleInstruction -> astInstructionExecutor(instruction, store, stack).bind()
        else -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
