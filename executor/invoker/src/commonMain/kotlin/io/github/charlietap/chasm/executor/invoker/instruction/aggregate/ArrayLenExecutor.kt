package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun ArrayLenExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayLen,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val arrayReference = stack.popArrayReference().bind()
    val arrayInstance = arrayReference.instance

    stack.pushValue(NumberValue.I32(arrayInstance.fields.size))
}
