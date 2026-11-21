package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext

@PublishedApi
internal inline fun <T> ExecutionContext.withHostCallbackScope(block: () -> T): T {
    val roots = references
    val marker = roots.beginScope(0)
    try {
        return block()
    } finally {
        roots.endScope(marker)
    }
}
