package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.instruction
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun CallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val value = stack.popFunctionAddress().bind()

    val instruction = store.instruction(value.address).bind()
    instruction(context).bind()
}
