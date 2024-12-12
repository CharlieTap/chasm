package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.extendSigned
import io.github.charlietap.chasm.executor.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun I31GetUnsignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.I31GetUnsigned,
): Result<Unit, InvocationError> =
    I31GetExecutor(
        context = context,
        signedExtension = false,
        i31SignedExtender = ReferenceValue.I31::extendSigned,
        i31UnsignedExtender = ReferenceValue.I31::extendUnsigned,
    )
