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

    val depths = frame.depths
    stack.shrinkHandlers(0, depths.handlers)
    stack.shrinkInstructions(0, depths.instructions)
    stack.shrinkLabels(0, depths.labels)
    stack.shrinkValues(frame.arity, depths.values)
}
