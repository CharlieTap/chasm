package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGetSigned,
) = StructGetSignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    fieldIndex = instruction.fieldIndex,
    packedType = instruction.packedType,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
    packedType: io.github.charlietap.chasm.type.PackedType,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val reference = vstack.pop()
    val packed = context.heap.getStructFieldTrusted(reference, fieldIndex.idx.toInt())
    val unpackedValue = fieldUnpacker(packed, packedType, true)

    vstack.push(unpackedValue)
}
