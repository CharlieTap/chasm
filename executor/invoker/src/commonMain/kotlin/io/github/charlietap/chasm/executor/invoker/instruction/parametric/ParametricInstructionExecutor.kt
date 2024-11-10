package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
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

internal inline fun ParametricInstructionExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction,
    crossinline dropExecutor: Executor<ParametricInstruction.Drop>,
    crossinline selectExecutor: Executor<ParametricInstruction.Select>,
    crossinline selectWithTypeExecutor: Executor<ParametricInstruction.SelectWithType>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ParametricInstruction.Drop -> dropExecutor(context, instruction).bind()
        is ParametricInstruction.Select -> selectExecutor(context, instruction).bind()
        is ParametricInstruction.SelectWithType -> selectWithTypeExecutor(context, instruction).bind()
    }
}
