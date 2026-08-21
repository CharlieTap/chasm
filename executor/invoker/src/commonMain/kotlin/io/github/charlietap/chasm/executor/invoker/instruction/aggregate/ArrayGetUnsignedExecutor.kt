package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.PackedType

internal fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGetUnsigned,
) = ArrayGetUnsignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    packedType = instruction.packedType,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    packedType: PackedType,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val fieldIndex = vstack.popI32()
    val reference = vstack.pop()
    val packed = context.heap.getArrayElementTrusted(reference, fieldIndex)
    val unpackedValue = fieldUnpacker(packed, packedType, false)

    vstack.push(unpackedValue)
}
