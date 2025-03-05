package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.StackDepths

inline fun ExecutionContext.depth() =
    cstack.handlersDepth() +
        cstack.instructionsDepth() +
        cstack.labelsDepth() +
        vstack.depth()

inline fun ExecutionContext.depths() = StackDepths(
    handlers = cstack.handlersDepth(),
    instructions = cstack.instructionsDepth(),
    labels = cstack.labelsDepth(),
    values = vstack.depth(),
)
