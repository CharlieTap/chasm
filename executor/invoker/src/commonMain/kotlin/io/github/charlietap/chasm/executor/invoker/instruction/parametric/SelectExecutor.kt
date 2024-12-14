package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.Select,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val select = stack.popI32().bind()

    val value2 = stack.popValue().bind()

    if (select == 0) {
        stack.popValue().bind()
        stack.push(value2)
    }
}
