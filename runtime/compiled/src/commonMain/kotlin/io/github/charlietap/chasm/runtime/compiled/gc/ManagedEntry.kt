package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance

fun enterObjects(
    context: ExecutionContext,
    function: FunctionInstance,
    words: LongArray,
    references: Array<Any?>,
): InvocationError? {

    val store = context.store
    store.heap.clearPendingException()
    if (store.compiledCallDepth == 0) store.referenceBridge?.releaseEscaped()

    val interrupt = store.interrupt
    val depth = interrupt.depth
    if (interrupt.enabled) {
        if (depth == 0 && interrupt.requested) interrupt.requested = false
        interrupt.depth = depth + 1
    }

    return try {
        context.compiledCallDepth = store.compiledCallDepth
        callObjects(context, context.instance, function, words, references, context.compiledCallDepth)
        if (context.compiledUnwinding) publishThrown(context) else null
    } catch (exception: InvocationException) {
        if (exception.error == InvocationError.ThrownException) publishThrown(context) else exception.error
    } catch (_: HostRaisedWasmException) {
        InvocationError.ThrownException
    } catch (_: ClassCastException) {
        InvocationError.FailedToCastReference
    } finally {
        if (interrupt.enabled) interrupt.depth = depth
    }
}

private fun publishThrown(
    context: ExecutionContext,
): InvocationError {

    val exception = context.managedException
    if (exception != null) {
        context.store.referenceBridge?.let { bridge -> context.heap.setPendingException(bridge.toHostResult(exception, false)) }
    }
    context.managedException = null
    context.compiledUnwinding = false

    return InvocationError.ThrownException
}
