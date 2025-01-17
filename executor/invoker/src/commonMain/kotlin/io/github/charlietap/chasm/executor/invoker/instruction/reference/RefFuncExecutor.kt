package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.ext.functionAddress
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefFuncExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefFunc,
) {

    val (stack) = context
    val frame = stack.peekFrame()

    val functionAddress = frame.instance
        .functionAddress(instruction.funcIdx)

    stack.push(ReferenceValue.Function(functionAddress))
}
