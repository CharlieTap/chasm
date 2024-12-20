package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
): Result<Unit, InvocationError> = TryTableExecutor(
    context = context,
    instruction = instruction,
    blockExecutor = ::InstructionBlockExecutor,
)

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
    crossinline blockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val params = instruction.functionType.params.types.size
    val results = instruction.functionType.results.types.size

    val label = Stack.Entry.Label(
        arity = results,
        depths = LabelStackDepths(
            instructions = stack.instructionsDepth(),
            labels = stack.labelsDepth(),
            values = stack.valuesDepth() - params,
        ),
        continuation = emptyList(),
    )

    val handler = ExceptionHandler(
        instructions = instruction.handlers,
        framesDepth = stack.framesDepth(),
        labelsDepth = stack.labelsDepth(),
        instructionsDepth = stack.instructionsDepth(),
    )

    blockExecutor(stack, label, instruction.instructions, handler).bind()
}
