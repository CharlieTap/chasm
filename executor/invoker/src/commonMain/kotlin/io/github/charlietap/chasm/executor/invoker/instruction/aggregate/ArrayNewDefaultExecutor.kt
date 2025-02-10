package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun ArrayNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewDefault,
) {
    val stack = context.vstack
    val store = context.store
    val size = stack.popI32()

    val fields = LongArray(size) {
        instruction.field
    }

    val instance = ArrayInstance(instruction.definedType, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    stack.push(reference.toLong())
}
