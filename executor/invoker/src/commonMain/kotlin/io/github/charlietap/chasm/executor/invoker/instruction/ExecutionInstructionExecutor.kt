package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ExecutionInstructionExecutor = (ExecutionInstruction, Store, Stack) -> Result<Unit, InvocationError>

internal fun ExecutionInstructionExecutor(
    instruction: ExecutionInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    ExecutionInstructionExecutor(
        instruction = instruction,
        store = store,
        stack = stack,
        adminInstructionExecutor = ::AdminInstructionExecutor,
        moduleInstructionExecutor = ::ModuleInstructionExecutor,
    )

internal fun ExecutionInstructionExecutor(
    instruction: ExecutionInstruction,
    store: Store,
    stack: Stack,
    adminInstructionExecutor: AdminInstructionExecutor,
    moduleInstructionExecutor: ModuleInstructionExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AdminInstruction -> adminInstructionExecutor(instruction, store, stack).bind()
        is ModuleInstruction -> moduleInstructionExecutor(instruction, store, stack).bind()
    }
}
