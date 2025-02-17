package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.packedField
import io.github.charlietap.chasm.executor.runtime.ext.popStructAddress
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.module.Index

internal fun StructGetSignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGetSigned,
) = StructGetSignedExecutor(
    context = context,
    fieldIndex = instruction.fieldIndex,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetSignedExecutor(
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val address = stack.popStructAddress()
    val structInstance = store.struct(address)

    val (packed, type) = structInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, true)

    stack.push(unpackedValue)
}
