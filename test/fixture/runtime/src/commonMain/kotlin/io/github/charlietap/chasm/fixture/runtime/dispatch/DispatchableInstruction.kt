package io.github.charlietap.chasm.fixture.runtime.dispatch

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ValueStack

fun dispatchableInstruction(
    executor: ((ValueStack, ExecutionContext) -> Unit)? = null,
): DispatchableInstruction = executor?.let {
    DispatchableInstruction { vstack, context, nextIp ->
        executor(vstack, context)
        nextIp
    }
} ?: NoOpDispatchableInstruction

private object NoOpDispatchableInstruction : DispatchableInstruction() {
    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = nextIp
}
