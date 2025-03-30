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

internal inline fun ArrayNewExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
): InstructionPointer {
    val size = vstack.popI32()
    val value = vstack.pop()

    val fields = LongArray(size) {
        value
    }

    val instance = ArrayInstance(instruction.rtt, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    vstack.push(reference.toLong())

    return ip + 1
}
