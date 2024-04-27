@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popFrame

@Suppress("UNUSED_PARAMETER")
internal inline fun FrameInstructionExecutorImpl(
    frame: Stack.Entry.ActivationFrame,
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    stack.popFrame().bind()
}
