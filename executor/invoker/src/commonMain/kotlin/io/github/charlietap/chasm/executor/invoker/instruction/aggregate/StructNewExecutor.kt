package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.allocateStruct
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun StructNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
) {
    val structType = instruction.structType
    val size = structType.fields.size

    val fields = LongArray(size)
    var index = size - 1
    while (index >= 0) {
        fields[index] = vstack.pop()
        index--
    }

    val instance = StructInstance(instruction.rtt, structType, fields)
    val address = store.allocateStruct(instance)
    val reference = ReferenceValue.Struct(address)

    vstack.push(reference.toLong())
}
