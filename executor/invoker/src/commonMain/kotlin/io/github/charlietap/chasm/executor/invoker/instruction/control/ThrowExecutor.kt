package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ThrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
): Int {
    val address = cstack.frameInstance()
        .tagAddress(instruction.tagIndex)
    return ThrowRefValueExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        ref = context.heap.allocateExceptionFromStack(context, address),
    )
}
