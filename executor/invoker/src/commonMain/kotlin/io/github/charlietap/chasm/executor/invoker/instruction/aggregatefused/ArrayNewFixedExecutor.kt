package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ArrayNewFixedExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayNewFixed,
): InstructionPointer {
    val size = instruction.size
    val fields = LongArray(size)
    var index = size - 1
    while (index >= 0) {
        fields[index] = vstack.pop()
        index--
    }

    val instance = ArrayInstance(instruction.rtt, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    instruction.destination(reference.toLong(), vstack)

    return ip + 1
}
