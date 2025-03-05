package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.pushReference
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

internal fun AnyConvertExternExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.AnyConvertExtern,
) {
    val stack = context.vstack
    val referenceValue = stack.pop()
    when {
        referenceValue.isNullableReference() -> {
            stack.push(ReferenceValue.Null(AbstractHeapType.Any).toLong())
        }
        referenceValue.isExternReference() -> {
            val extern = referenceValue.toExternReference()
            stack.pushReference(extern.referenceValue)
        }
        else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
}
