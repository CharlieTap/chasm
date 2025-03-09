package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun BrOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNonNull,
) = BrOnNonNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNonNull,
    crossinline breakExecutor: BreakExecutor,
) {
    val value = vstack.pop()

    if (!value.isNullableReference()) {
        vstack.push(value)
        breakExecutor(cstack, vstack, instruction.labelIndex)
    }
}
