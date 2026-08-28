package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun StructGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGet,
) = StructGetExecutor(
    vstack = vstack,
    context = context,
    fieldIndex = instruction.fieldIndex,
)

internal inline fun StructGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
) {
    val reference = vstack.pop()
    vstack.push(context.heap.getStructFieldTrusted(reference, fieldIndex.idx.toInt()))
}
