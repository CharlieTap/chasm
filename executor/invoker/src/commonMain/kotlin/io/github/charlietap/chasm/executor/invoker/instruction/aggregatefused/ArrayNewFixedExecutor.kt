package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayNewFixed,
) {
    val store = context.store
    val stack = context.vstack

    val size = instruction.size
    val fields = LongArray(size)
    var index = size - 1
    while (index >= 0) {
        fields[index] = stack.pop()
        index--
    }

    val instance = ArrayInstance(instruction.definedType, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    instruction.destination(reference.toLong(), stack)
}
