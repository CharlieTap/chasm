package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun ParametricInstructionExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction,
): Result<Unit, InvocationError> =
    ParametricInstructionExecutor(
        context = context,
        instruction = instruction,
        dropExecutor = ::DropExecutor,
        selectExecutor = ::SelectExecutor,
        selectWithTypeExecutor = ::SelectWithTypeExecutor,
    )

internal fun ParametricInstructionExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction,
    dropExecutor: DropExecutor,
    selectExecutor: SelectExecutor,
    selectWithTypeExecutor: SelectWithTypeExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    when (instruction) {
        is ParametricInstruction.Drop -> dropExecutor(stack).bind()
        is ParametricInstruction.Select -> selectExecutor(stack).bind()
        is ParametricInstruction.SelectWithType -> selectWithTypeExecutor(stack, instruction).bind()
    }
}
