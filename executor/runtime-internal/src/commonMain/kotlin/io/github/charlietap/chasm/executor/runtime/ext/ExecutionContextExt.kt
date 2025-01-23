package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths

inline fun ExecutionContext.depth() =
    cstack.handlersDepth() +
        cstack.instructionsDepth() +
        cstack.labelsDepth() +
        vstack.depth()

inline fun ExecutionContext.depths() = FrameStackDepths(
    handlers = cstack.handlersDepth(),
    instructions = cstack.instructionsDepth(),
    labels = cstack.labelsDepth(),
    values = vstack.depth(),
)
