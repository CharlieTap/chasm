package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.packedField
import io.github.charlietap.chasm.executor.runtime.ext.popStructAddress
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.module.Index

internal fun StructGetUnsignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGetUnsigned,
) = StructGetUnsignedExecutor(
    context = context,
    fieldIndex = instruction.fieldIndex,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetUnsignedExecutor(
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val address = stack.popStructAddress()
    val structInstance = store.struct(address)

    val (packed, type) = structInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, false)

    stack.push(unpackedValue)
}
