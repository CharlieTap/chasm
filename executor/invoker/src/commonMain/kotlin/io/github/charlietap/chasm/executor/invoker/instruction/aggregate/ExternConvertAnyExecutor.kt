package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ExternConvertAnyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ExternConvertAny,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    when (val referenceValue = stack.popReference().bind()) {
        is ReferenceValue.Null -> {
            stack.pushValue(ReferenceValue.Null(AbstractHeapType.Extern))
        }
        else -> {
            stack.pushValue(ReferenceValue.Extern(referenceValue))
        }
    }
}
