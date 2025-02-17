package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.isExternReference
import io.github.charlietap.chasm.executor.runtime.ext.isNullableReference
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.ext.toExternReference
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.ir.type.AbstractHeapType

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
