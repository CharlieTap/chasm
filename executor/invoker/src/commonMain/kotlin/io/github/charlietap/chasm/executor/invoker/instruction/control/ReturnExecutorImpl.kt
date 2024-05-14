@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.ext.popInstruction
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

internal inline fun ReturnExecutorImpl(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    val frame = stack.popFrame().bind()

    val results = List(frame.arity.value) {
        stack.popValue().bind()
    }

    do {
        val instruction = stack.popInstruction().bind()
    } while (instruction.instruction !is AdminInstruction.Frame)

    while (stack.labelsDepth() > frame.stackLabelsDepth) {
        stack.popLabel().bind()
    }

    while (stack.valuesDepth() > frame.stackValuesDepth) {
        stack.popValue().bind()
    }

    results.asReversed().forEach { value ->
        stack.push(value)
    }
}
