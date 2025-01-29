package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.runtime.encoder.isExternReference
import io.github.charlietap.chasm.executor.runtime.encoder.isNullableReference
import io.github.charlietap.chasm.executor.runtime.encoder.toExternReference
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun AnyConvertExternExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.AnyConvertExtern,
) {
    val stack = context.vstack
    val referenceValue = stack.pop()
    when {
        referenceValue.isNullableReference() -> {
            stack.pushReference(ReferenceValue.Null(AbstractHeapType.Any))
        }
        referenceValue.isExternReference() -> {
            val extern = referenceValue.toExternReference()
            stack.pushReference(extern.referenceValue)
        }
        else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
}
