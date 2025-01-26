package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ExternConvertAnyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ExternConvertAny,
) {
    val stack = context.vstack
    when (val referenceValue = stack.popReference()) {
        is ReferenceValue.Null -> {
            stack.pushReference(ReferenceValue.Null(AbstractHeapType.Extern))
        }
        else -> {
            stack.pushReference(ReferenceValue.Extern(referenceValue))
        }
    }
}
