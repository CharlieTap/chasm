package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun StructGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGet,
) = StructGetExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    fieldIndex = instruction.fieldIndex,
)

internal inline fun StructGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    fieldIndex: Index.FieldIndex,
) {
    val reference = vstack.pop()
    vstack.push(context.heap.getStructFieldTrusted(reference, fieldIndex.idx.toInt()))
}
