package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.function.withHostCallbackScope
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.withLiveStack

@InternalChasmApi
fun RawFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    function: FunctionInstance,
    words: LongArray,
): Result<Unit, InvocationError> {

    val invocationDepth = store.invocationDepth
    if (invocationDepth == 0) {
        store.heap.clearPendingException()
    }

    store.invocationDepth = invocationDepth + 1

    return try {
        val interrupt = store.interrupt
        if (!interrupt.enabled) {
            invokeRaw(config, store, instance, function, words)
        } else {
            val depth = interrupt.depth
            if (depth == 0 && interrupt.requested) {
                interrupt.requested = false
            }
            interrupt.depth = depth + 1
            try {
                invokeRaw(config, store, instance, function, words)
            } finally {
                interrupt.depth = depth
            }
        }
    } finally {
        store.invocationDepth = invocationDepth
    }
}

@OptIn(UnsafeHostApi::class)
private fun invokeRaw(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    function: FunctionInstance,
    words: LongArray,
): Result<Unit, InvocationError> {
    return when (function) {
        is FunctionInstance.HostFunction -> {

            val parameterCount = function.functionType.params.types.size
            val resultCount = function.functionType.results.types.size
            val stack = hostValueStack(maxOf(parameterCount, resultCount), store.maxStackSlots)
                ?: return Err(InvocationError.CallStackExhausted)
            stack.push(words.copyOf(parameterCount))
            stack.reserveDepth(resultCount)
            val context = ExecutionContext(stack, store, instance, config)

            try {
                store.withLiveStack(stack) {
                    context.withHostCallbackScope {

                        context(stack.unsafeElements(), instance, context) {
                            function.function.invoke(0, 0)
                        }

                        stack.unsafeElements().copyInto(words, 0, 0, resultCount)
                        Ok(Unit)
                    }
                }
            } catch (_: HostRaisedWasmException) {
                Err(InvocationError.ThrownException)
            } catch (e: HostFunctionException) {
                Err(InvocationError.HostFunctionError(e.reason))
            } catch (_: GuestHeapOutOfMemoryException) {
                Err(InvocationError.GuestHeapOutOfMemory)
            }
        }
        is FunctionInstance.WasmFunction -> RawWasmFunctionExecutor(config, store, function, words)
    }
}
