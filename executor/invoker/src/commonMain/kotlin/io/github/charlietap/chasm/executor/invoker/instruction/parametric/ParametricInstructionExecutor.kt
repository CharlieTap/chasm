package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias ParametricInstructionExecutor = (ParametricInstruction, Stack) -> Result<Unit, InvocationError>

internal fun ParametricInstructionExecutor(
    instruction: ParametricInstruction,
    stack: Stack,
): Result<Unit, InvocationError> =
    ParametricInstructionExecutor(
        instruction = instruction,
        stack = stack,
        dropExecutor = ::DropExecutor,
        selectExecutor = ::SelectExecutor,
        selectWithTypeExecutor = ::SelectWithTypeExecutor,
    )

internal fun ParametricInstructionExecutor(
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
    }
}
