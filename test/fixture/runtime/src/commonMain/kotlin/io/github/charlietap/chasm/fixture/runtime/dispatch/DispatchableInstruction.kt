package io.github.charlietap.chasm.fixture.runtime.dispatch

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun dispatchableInstruction(
    executor: ((ValueStack, ControlStack, Store, ExecutionContext) -> Unit)? = null,
): DispatchableInstruction = executor?.let {
    DispatchableInstruction { vstack, cstack, store, context ->
        executor(vstack, cstack, store, context)
    }
} ?: NoOpDispatchableInstruction

private object NoOpDispatchableInstruction : DispatchableInstruction {
    override fun invoke(vstack: ValueStack, cstack: ControlStack, store: Store, context: ExecutionContext) {
        return
    }
}
