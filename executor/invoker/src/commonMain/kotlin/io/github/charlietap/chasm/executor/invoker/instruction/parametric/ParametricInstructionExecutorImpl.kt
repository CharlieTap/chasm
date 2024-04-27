package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction

internal fun ParametricInstructionExecutorImpl(
    instruction: ParametricInstruction,
    stack: Stack,
): Result<Unit, InvocationError> =
    ParametricInstructionExecutorImpl(
        instruction = instruction,
        stack = stack,
        dropExecutor = ::DropExecutorImpl,
        selectExecutor = ::SelectExecutorImpl,
        selectWithTypeExecutor = ::SelectWithTypeExecutorImpl,
    )

internal fun ParametricInstructionExecutorImpl(
    instruction: ParametricInstruction,
    stack: Stack,
    dropExecutor: DropExecutor,
    selectExecutor: SelectExecutor,
    selectWithTypeExecutor: SelectWithTypeExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ParametricInstruction.Drop -> dropExecutor(stack).bind()
        is ParametricInstruction.Select -> selectExecutor(stack).bind()
        is ParametricInstruction.SelectWithType -> selectWithTypeExecutor(stack, instruction).bind()

        else -> Err(InvocationError.UnimplementedInstruction(ModuleInstruction(instruction))).bind<Unit>()
    }
}
