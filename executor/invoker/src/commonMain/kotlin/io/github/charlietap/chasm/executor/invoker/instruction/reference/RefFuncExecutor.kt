package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.functionAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefFuncExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefFunc,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.peekFrame().bind()

    val functionAddress = frame.instance
        .functionAddress(instruction.funcIdx)
        .bind()

    stack.push(ReferenceValue.Function(functionAddress))
}
