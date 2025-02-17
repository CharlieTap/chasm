package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popStructAddress
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.module.Index

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
