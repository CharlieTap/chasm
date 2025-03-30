package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.StackDepths

inline fun ExecutionContext.depth() =
    cstack.handlersDepth() +
        vstack.depth()

inline fun ExecutionContext.depths() = StackDepths(
    handlers = cstack.handlersDepth(),
    values = vstack.depth(),
)
