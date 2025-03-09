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
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

internal fun AnyConvertExternExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.AnyConvertExtern,
) {
    val referenceValue = vstack.pop()
    when {
        referenceValue.isNullableReference() -> {
            vstack.push(ReferenceValue.Null(AbstractHeapType.Any).toLong())
        }
        referenceValue.isExternReference() -> {
            val extern = referenceValue.toExternReference()
            vstack.pushReference(extern.referenceValue)
        }
        else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
}
