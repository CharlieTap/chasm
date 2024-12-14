package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefEqExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefEq,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val referenceValue1 = stack.popValue().bind().value
    val referenceValue2 = stack.popValue().bind().value

    val equal = when {
        referenceValue1 is ReferenceValue.Array && referenceValue2 is ReferenceValue.Array -> referenceValue1.instance === referenceValue2.instance
        referenceValue1 is ReferenceValue.I31 && referenceValue2 is ReferenceValue.I31 -> referenceValue1.value == referenceValue2.value
        referenceValue1 is ReferenceValue.Null && referenceValue2 is ReferenceValue.Null -> true
        referenceValue1 is ReferenceValue.Struct && referenceValue2 is ReferenceValue.Struct -> referenceValue1.instance === referenceValue2.instance
        else -> referenceValue1 === referenceValue2
    }

    if (equal) {
        stack.pushValue(NumberValue.I32(1))
    } else {
        stack.pushValue(NumberValue.I32(0))
    }
}
