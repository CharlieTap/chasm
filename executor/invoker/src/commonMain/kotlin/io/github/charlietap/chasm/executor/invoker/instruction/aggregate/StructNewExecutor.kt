package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun StructNewExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
): InstructionPointer {
    val structType = instruction.structType
    val size = structType.fields.size

    val fields = LongArray(size)
    var index = size - 1
    while (index >= 0) {
        fields[index] = vstack.pop()
        index--
    }

    val instance = StructInstance(instruction.rtt, structType, fields)
    store.structs.add(instance)
    val reference = ReferenceValue.Struct(Address.Struct(store.structs.size - 1))

    vstack.push(reference.toLong())

    return ip + 1
}
