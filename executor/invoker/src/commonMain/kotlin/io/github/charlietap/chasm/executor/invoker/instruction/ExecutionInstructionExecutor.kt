package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction

internal fun ExecutionInstructionExecutor(
    context: ExecutionContext,
    instruction: ExecutionInstruction,
): Result<Unit, InvocationError> =
    ExecutionInstructionExecutor(
        context = context,
        instruction = instruction,
        adminInstructionExecutor = ::AdminInstructionExecutor,
        moduleInstructionExecutor = ::ModuleInstructionExecutor,
    )

internal fun ExecutionInstructionExecutor(
    context: ExecutionContext,
    instruction: ExecutionInstruction,
    adminInstructionExecutor: Executor<AdminInstruction>,
    moduleInstructionExecutor: Executor<ModuleInstruction>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AdminInstruction -> adminInstructionExecutor(context, instruction).bind()
        is ModuleInstruction -> moduleInstructionExecutor(context, instruction).bind()
    }
}
