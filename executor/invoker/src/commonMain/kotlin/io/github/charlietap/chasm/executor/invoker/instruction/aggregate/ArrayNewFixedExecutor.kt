@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.weakref.weakReference

internal fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
): Result<Unit, InvocationError> =
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
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val (typeIndex, size) = instruction
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val fields = MutableList(size.toInt()) { _ ->
        val value = stack.popValue().bind().value
        fieldPacker(value, arrayType.fieldType).bind()
    }.asReversed()

    val instance = ArrayInstance(definedType, fields)

    store.arrays.add(weakReference(instance))

    val address = Address.Array(store.arrays.size - 1)
    val reference = ReferenceValue.Array(address, instance)

    stack.pushValue(reference)
}
