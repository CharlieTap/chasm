package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext

internal inline fun <T> ExecutionContext.withHostCallbackScope(block: () -> T): T {
    val marker = heap.beginScope(0)
    try {
        return block()
    } finally {
        heap.endScope(marker)
    }
}
