package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun StructNewExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructNew,
) {
    val store = context.store
    val stack = context.vstack
    val structType = instruction.structType
    val size = structType.fields.size

    val fields = LongArray(size)
    var index = size - 1
    while (index >= 0) {
        fields[index] = stack.pop()
        index--
    }

    val instance = StructInstance(instruction.definedType, structType, fields)
    store.structs.add(instance)
    val reference = ReferenceValue.Struct(Address.Struct(store.structs.size - 1))

    instruction.destination(reference.toLong(), stack)
}
