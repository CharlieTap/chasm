package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefIsNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefIsNull,
) {

    val (stack) = context
    val value = stack.popValue().bind()

    if (value is ReferenceValue) {
        if (value is ReferenceValue.Null) {
            stack.push(NumberValue.I32(1))
        } else {
            stack.push(NumberValue.I32(0))
        }
    } else {
        Err(InvocationError.ReferenceValueExpected).bind()
    }
}
