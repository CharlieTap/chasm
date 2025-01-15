package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun AnyConvertExternExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.AnyConvertExtern,
) {
    val (stack) = context
    when (val referenceValue = stack.popReference().bind()) {
        is ReferenceValue.Null -> {
            stack.pushValue(ReferenceValue.Null(AbstractHeapType.Any))
        }
        is ReferenceValue.Extern -> {
            stack.pushValue(referenceValue.referenceValue)
        }
        else -> Err(InvocationError.UnexpectedReferenceValue).bind()
    }
}
