package io.github.charlietap.chasm.fixture.runtime.dispatch

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun dispatchableInstruction(
    executor: ((ValueStack, ControlStack, Store, ExecutionContext) -> Result<Unit, InvocationError>)? = null,
): DispatchableInstruction = executor?.let {
    { vstack, cstack, store, context ->
        executor(vstack, cstack, store, context)
    }
} ?: NoOpDispatchableInstruction

private object NoOpDispatchableInstruction : DispatchableInstruction {
    override fun invoke(vstack: ValueStack, cstack: ControlStack, store: Store, ctx: ExecutionContext) {
        return
    }
}
