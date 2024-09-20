@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun LabelInstructionExecutor(
    context: ExecutionContext,
    instruction: AdminInstruction.Label,
): Result<Unit, InvocationError> = binding {
    context.stack.popLabel().bind()
}
