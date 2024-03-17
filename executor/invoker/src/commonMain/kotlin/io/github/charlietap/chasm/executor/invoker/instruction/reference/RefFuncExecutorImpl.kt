@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.functionAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefFuncExecutorImpl(
    stack: Stack,
    instruction: ReferenceInstruction.RefFunc,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()

    val functionAddress = frame.state.module.functionAddress(instruction.funcIdx.index()).bind()

    stack.push(Stack.Entry.Value(ReferenceValue.FunctionAddress(functionAddress)))
}
