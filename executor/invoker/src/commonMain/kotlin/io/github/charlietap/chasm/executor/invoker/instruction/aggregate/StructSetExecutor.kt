package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.popStructAddress
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal inline fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
) {
    val store = context.store
    val stack = context.vstack

    val executionValue = stack.pop()
    val address = stack.popStructAddress()
    val structInstance = store.struct(address)

    structInstance.fields[instruction.fieldIndex] = executionValue
}
