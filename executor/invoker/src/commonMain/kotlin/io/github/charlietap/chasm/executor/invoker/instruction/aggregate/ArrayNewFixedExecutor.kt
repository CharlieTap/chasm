package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
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
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
): InstructionPointer = ArrayNewFixedExecutor(
    ip = ip,
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    size = instruction.size.toInt(),
)

internal inline fun ArrayNewFixedExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    rtt: RTT,
    arrayType: ArrayType,
    size: Int,
): InstructionPointer {
    val fields = LongArray(size)
    var index = size - 1
    while (index >= 0) {
        fields[index] = vstack.pop()
        index--
    }

    val instance = ArrayInstance(rtt, arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    vstack.push(reference.toLong())
    return ip + 1
}
