package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ArrayNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
) {
    val stack = context.vstack
    val store = context.store

    val size = stack.popI32()
    val value = stack.pop()

    val fields = LongArray(size) {
        value
    }

    val instance = ArrayInstance(instruction.definedType, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    stack.push(reference.toLong())
}
