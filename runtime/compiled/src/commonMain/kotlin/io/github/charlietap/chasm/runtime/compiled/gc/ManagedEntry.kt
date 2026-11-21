package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.executor.invoker.thread.traceableTrap
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance

fun enterObjects(
    context: ExecutionContext,
    function: FunctionInstance,
    words: LongArray,
    references: Array<Any?>,
    pending: PendingExceptions,
): InvocationError? {

    val store = context.store
    val interrupt = store.interrupt
    val depth = interrupt.depth
    if (interrupt.enabled) {
        if (depth == 0 && interrupt.requested) interrupt.requested = false
        interrupt.depth = depth + 1
    }

    return try {
        context.compiledCallDepth = store.compiledCallDepth
        callObjects(context, context.instance, function, words, references, context.compiledCallDepth)
        if (context.compiledUnwinding) holdThrown(context, pending) else null
    } catch (exception: InvocationException) {
        if (context.config.debugInfo) traceableTrap(store, context.vstack, exception, entryIp(function)) else exception.error
    } catch (_: ClassCastException) {
        InvocationError.FailedToCastReference
    } finally {
        if (interrupt.enabled) interrupt.depth = depth
    }
}

private fun holdThrown(
    context: ExecutionContext,
    pending: PendingExceptions,
): InvocationError {

    if (context.managedException == null) {
        context.managedException = pending.take()
    }
    context.compiledUnwinding = false

    return InvocationError.ThrownException
}

private fun entryIp(function: FunctionInstance): Int {
    return (function as? FunctionInstance.WasmFunction)?.callStrategy?.entryIp ?: -1
}
