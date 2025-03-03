package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun ArrayNewExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayNew,
) {
    val stack = context.vstack
    val store = context.store

    val size = instruction.size(stack).toInt()
    val value = instruction.value(stack)

    val fields = LongArray(size) {
        value
    }

    val instance = ArrayInstance(instruction.definedType, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    instruction.destination(reference.toLong(), stack)
}
