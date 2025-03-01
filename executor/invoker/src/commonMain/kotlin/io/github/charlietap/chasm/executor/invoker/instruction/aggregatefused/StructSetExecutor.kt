package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.ext.toStructAddress
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

internal inline fun StructSetExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructSet,
) {
    val store = context.store
    val stack = context.vstack

    val value = instruction.value(stack)
    val address = instruction.address(stack).toStructAddress()
    val structInstance = store.struct(address)

    structInstance.fields[instruction.fieldIndex] = value
}
