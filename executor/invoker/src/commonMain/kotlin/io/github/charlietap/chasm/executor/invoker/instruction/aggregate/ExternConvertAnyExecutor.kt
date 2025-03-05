package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.pushReference
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue
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
