@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValue

internal typealias SelectWithTypeExecutor = (Stack, ParametricInstruction.SelectWithType) -> Result<Unit, InvocationError>

internal inline fun SelectWithTypeExecutor(
    stack: Stack,
    instruction: ParametricInstruction.SelectWithType,
): Result<Unit, InvocationError> = binding {
    val select = stack.popI32().bind()

    val value2 = stack.popValue().bind()

    if (select == 0) {
        stack.popValue().bind()
        stack.push(value2)
    }
}
