package io.github.charlietap.chasm.fixture.runtime.dispatch

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun dispatchableInstruction(
    executor: ((InstructionPointer, ValueStack, ControlStack, Store, ExecutionContext) -> InstructionPointer)? = null,
): DispatchableInstruction = executor?.let {
    { ip, vstack, cstack, store, context ->
        executor(ip, vstack, cstack, store, context)
    }
} ?: NoOpDispatchableInstruction

private object NoOpDispatchableInstruction : DispatchableInstruction {
    override fun invoke(
        ip: InstructionPointer,
        vstack: ValueStack,
        cstack: ControlStack,
        store: Store,
        ctx: ExecutionContext,
    ): InstructionPointer {
        return InstructionPointer(0)
    }
}
