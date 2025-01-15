package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.extendSigned
import io.github.charlietap.chasm.executor.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.executor.runtime.ext.popI31Reference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun I31GetSignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.I31GetSigned,
) =
    I31GetExecutor(
        context = context,
        signedExtension = true,
        i31SignedExtender = ReferenceValue.I31::extendSigned,
        i31UnsignedExtender = ReferenceValue.I31::extendUnsigned,
    )

internal inline fun I31GetExecutor(
    context: ExecutionContext,
    signedExtension: Boolean,
    crossinline i31SignedExtender: (ReferenceValue.I31) -> Int,
    crossinline i31UnsignedExtender: (ReferenceValue.I31) -> Int,
) {

    val (stack) = context
    val value = stack.popI31Reference().bind()

    val extended = if (signedExtension) {
        i31SignedExtender(value)
    } else {
        i31UnsignedExtender(value)
    }

    stack.pushValue(NumberValue.I32(extended))
}
