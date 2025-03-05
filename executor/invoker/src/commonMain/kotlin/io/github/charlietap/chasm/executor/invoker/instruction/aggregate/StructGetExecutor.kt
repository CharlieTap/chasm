package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.field
import io.github.charlietap.chasm.runtime.ext.popStructAddress
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal fun StructGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGet,
) = StructGetExecutor(
    context = context,
    fieldIndex = instruction.fieldIndex,
)

internal inline fun StructGetExecutor(
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
) {
    val store = context.store
    val stack = context.vstack

    val address = stack.popStructAddress()
    val structInstance = store.struct(address)
    val fieldValue = structInstance.field(fieldIndex)

    stack.push(fieldValue)
}
