package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
) =
    ArrayNewFixedExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {
    val store = context.store
    val stack = context.vstack
    val (typeIndex, size) = instruction
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)
    val arrayType = definedTypeExpander(definedType).arrayType()

    val fields = MutableList(size.toInt()) { _ ->
        val value = stack.pop()
        fieldPacker(value, arrayType.fieldType)
    }.asReversed()

    val instance = ArrayInstance(definedType, arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    stack.push(reference.toLong())
}
