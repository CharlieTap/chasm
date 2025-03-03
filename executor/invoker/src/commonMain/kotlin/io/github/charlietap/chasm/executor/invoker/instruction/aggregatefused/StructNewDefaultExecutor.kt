package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructNewDefault,
) {
    val stack = context.vstack
    val store = context.store

    val instance = StructInstance(instruction.definedType, instruction.structType, instruction.fields)
    store.structs.add(instance)
    val reference = ReferenceValue.Struct(Address.Struct(store.structs.size - 1))

    instruction.destination(reference.toLong(), stack)
}
