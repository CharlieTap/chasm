package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ValueStack

/** Correctness instrumentation has a separate body from uninstrumented execution. */
abstract class KotlinGeneratedInstruction : DispatchableInstruction() {
    abstract fun invokeCounted(vstack: ValueStack, context: ExecutionContext, nextIp: Int, onBlock: (Int) -> Unit): Int
}
