package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.allocateArray
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT

internal fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
) = ArrayNewFixedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    length = instruction.length.toInt(),
)

internal inline fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    rtt: RTT,
    arrayType: ArrayType,
    length: Int,
) {
    val fields = LongArray(length)
    var index = length - 1
    while (index >= 0) {
        fields[index] = vstack.pop()
        index--
    }

    val instance = ArrayInstance(rtt, arrayType, fields)
    val address = store.allocateArray(instance)
    val reference = ReferenceValue.Array(address)

    vstack.push(reference.toLong())
}
