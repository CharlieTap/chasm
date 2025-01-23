package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun ArrayLenExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayLen,
) {
    val stack = context.vstack
    val arrayInstance = stack.popArrayReference()

    stack.push(NumberValue.I32(arrayInstance.fields.size))
}
