@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadInvocation
import io.github.charlietap.chasm.executor.invoker.thread.traceableTrap
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.resource.gc.GarbageCollector
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun CompiledFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> = CompiledFunctionInvoker(
    config = config,
    store = store,
    instance = instance,
    function = store.function(address),
    values = values,
)

fun CompiledFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    function: FunctionInstance,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> = FunctionInvoker(
    config = config,
    store = store,
    instance = instance,
    function = function,
    values = values,
    wasmFunctionExecutor = ::executeCompiled,
)

private fun executeCompiled(
    config: RuntimeConfig,
    store: Store,
    function: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
): Result<List<Long>, InvocationError> {
    if (function.callStrategy.compiledEntry == null) return Err(InvocationError.InterpreterUnavailable)
    return if (config.debugInfo) {
        ThreadInvocation(
            config = config,
            store = store,
            instance = function,
            values = values,
            nested = null,
            garbageCollector = ::GarbageCollector,
            run = { entryIp, context -> runCompiledTraceable(entryIp, context) },
            trap = { exception, vstack -> traceableTrap(store, vstack, exception) },
        )
    } else {
        ThreadInvocation(
            config = config,
            store = store,
            instance = function,
            values = values,
            nested = null,
            garbageCollector = ::GarbageCollector,
            run = { entryIp, context -> runCompiled(entryIp, context) },
            trap = { exception, _ -> exception.error },
        )
    }
}

private fun runCompiled(
    entryIp: Int,
    context: ExecutionContext,
) {
    val next = context.store.program.instructions[entryIp](context.vstack, context, entryIp + 1)
    if (next != EXIT_IP) throw InvocationException(InvocationError.InterpreterUnavailable)
}

private fun runCompiledTraceable(
    entryIp: Int,
    context: ExecutionContext,
) {
    try {
        runCompiled(entryIp, context)
    } catch (exception: InvocationException) {
        throw exception.at(entryIp)
    } catch (_: GuestHeapOutOfMemoryException) {
        throw InvocationException(InvocationError.GuestHeapOutOfMemory).at(entryIp)
    }
}
