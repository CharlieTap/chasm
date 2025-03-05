package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
) {
    val stack = context.vstack
    val store = context.store

    val instance = StructInstance(instruction.definedType, instruction.structType, instruction.fields)
    store.structs.add(instance)
    val reference = ReferenceValue.Struct(Address.Struct(store.structs.size - 1))

    stack.push(reference.toLong())
}
