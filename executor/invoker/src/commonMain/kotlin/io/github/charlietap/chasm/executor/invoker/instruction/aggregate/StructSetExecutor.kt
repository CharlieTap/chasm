package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal inline fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
) {
    val store = context.store
    val stack = context.vstack

    val executionValue = stack.pop()
    val structRef = stack.popStructReference()
    val structInstance = store.struct(structRef.address)

    structInstance.fields[instruction.fieldIndex.index()] = executionValue
}
