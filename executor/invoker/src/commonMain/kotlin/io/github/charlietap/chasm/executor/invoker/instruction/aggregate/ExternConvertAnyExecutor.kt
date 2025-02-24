package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.isNullableReference
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

internal fun ExternConvertAnyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ExternConvertAny,
) {
    val stack = context.vstack
    val referenceValue = stack.pop()
    when {
        referenceValue.isNullableReference() -> {
            stack.push(ReferenceValue.Null(AbstractHeapType.Extern).toLong())
        }
        else -> {
            val extern = referenceValue.toReferenceValue()
            stack.pushReference(ReferenceValue.Extern(extern))
        }
    }
}
