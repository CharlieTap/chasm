package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
) = ArrayNewFixedExecutor(
    context = context,
    definedType = instruction.definedType,
    arrayType = instruction.arrayType,
    size = instruction.size.toInt(),
)

internal inline fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    definedType: DefinedType,
    arrayType: ArrayType,
    size: Int,
) {
    val store = context.store
    val stack = context.vstack

    val fields = LongArray(size) { _ ->
        stack.pop()
    }
    fields.reverse()

    val instance = ArrayInstance(definedType, arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    stack.push(reference.toLong())
}
