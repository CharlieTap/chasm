package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
) =
    StructNewExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun StructNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {
    val store = context.store
    val stack = context.vstack
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(instruction.typeIndex)
    val structType = definedTypeExpander(definedType).structType()

    val fieldTypes = structType.fields.asReversed()
    val fields = ArrayList<FieldValue>(fieldTypes.size)
    fieldTypes.forEachIndexed { idx, fieldType ->
        fields.add(fieldPacker(stack.pop(), fieldType))
    }

    val instance = StructInstance(definedType, structType, fields.asReversed())
    store.structs.add(instance)
    val reference = ReferenceValue.Struct(Address.Struct(store.structs.size - 1))

    stack.pushReference(reference)
}
