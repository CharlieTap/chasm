package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun StructNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
) {
    val store = context.store
    val stack = context.vstack
    val structType = instruction.structType

    val fields = LongArray(structType.fields.size) { idx ->
        stack.pop()
    }
    fields.reverse()

    val instance = StructInstance(instruction.definedType, structType, fields)
    store.structs.add(instance)
    val reference = ReferenceValue.Struct(Address.Struct(store.structs.size - 1))

    stack.pushReference(reference)
}
