package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.popStructAddress
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun StructGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGetUnsigned,
) = StructGetUnsignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    fieldIndex = instruction.fieldIndex,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val address = vstack.popStructAddress()
    val structInstance = store.struct(address)

    val (packed, type) = structInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, false)

    vstack.push(unpackedValue)
}
