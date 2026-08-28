package io.github.charlietap.chasm.runtime.dispatch

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ValueStack

abstract class DispatchableInstruction {
    abstract operator fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int
}

@Suppress("FunctionName")
inline fun DispatchableInstruction(
    crossinline block: (
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ) -> Int,
): DispatchableInstruction =
    object : DispatchableInstruction() {
        override fun invoke(
            vstack: ValueStack,
            context: ExecutionContext,
            nextIp: Int,
        ): Int = block(vstack, context, nextIp)
    }
