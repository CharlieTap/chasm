@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefIsNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefIsNull,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val value = stack.popValue().bind().value

    if (value is ReferenceValue) {
        if (value is ReferenceValue.Null) {
            stack.push(Stack.Entry.Value(NumberValue.I32(1)))
        } else {
            stack.push(Stack.Entry.Value(NumberValue.I32(0)))
        }
    } else {
        Err(InvocationError.ReferenceValueExpected).bind<Unit>()
    }
}
