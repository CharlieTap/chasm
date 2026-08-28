package io.github.charlietap.chasm.runtime.dispatch

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

abstract class DispatchableInstruction {
    abstract operator fun invoke(
        vstack: ValueStack,
        cstack: ControlStack,
        store: Store,
        context: ExecutionContext,
        nextIp: Int,
    ): Int
}

@Suppress("FunctionName")
inline fun DispatchableInstruction(
    crossinline block: (
        vstack: ValueStack,
        cstack: ControlStack,
        store: Store,
        context: ExecutionContext,
        nextIp: Int,
    ) -> Int,
): DispatchableInstruction =
    object : DispatchableInstruction() {
        override fun invoke(
            vstack: ValueStack,
            cstack: ControlStack,
            store: Store,
            context: ExecutionContext,
            nextIp: Int,
        ): Int = block(vstack, cstack, store, context, nextIp)
    }
