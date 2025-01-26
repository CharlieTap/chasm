package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun AnyConvertExternExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.AnyConvertExtern,
) {
    val stack = context.vstack
    when (val referenceValue = stack.popReference()) {
        is ReferenceValue.Null -> {
            stack.pushReference(ReferenceValue.Null(AbstractHeapType.Any))
        }
        is ReferenceValue.Extern -> {
            stack.pushReference(referenceValue.referenceValue)
        }
        else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
}
