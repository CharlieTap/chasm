package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun ReturnExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Return,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.popFrame().bind()

    stack.shrinkLabels(0, frame.stackLabelsDepth)
    stack.shrinkInstructions(0, frame.stackInstructionsDepth)
    stack.shrinkValues(frame.arity.value, frame.stackValuesDepth)
}
