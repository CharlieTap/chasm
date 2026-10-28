@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.NestedInterpreter
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.resource.gc.GarbageCollector
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

internal typealias ThreadExecutor =
    (RuntimeConfig, Store, FunctionInstance.WasmFunction, List<ExecutionValue>) -> Result<List<Long>, InvocationError>

internal fun ThreadExecutor(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
) = ThreadExecutor(
    config = config,
    store = store,
    instance = instance,
    values = values,
    garbageCollector = ::GarbageCollector,
)

internal inline fun ThreadExecutor(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
    crossinline garbageCollector: GarbageCollector,
): Result<List<Long>, InvocationError> = ThreadInvocation(
    config = config,
    store = store,
    instance = instance,
    values = values,
    nested = NESTED_INTERPRETER,
    garbageCollector = garbageCollector,
    run = { entryIp, context -> interpret(entryIp, context) },
    trap = { exception, _ -> exception.error },
)

internal fun TraceableThreadExecutor(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
): Result<List<Long>, InvocationError> = ThreadInvocation(
    config = config,
    store = store,
    instance = instance,
    values = values,
    nested = NESTED_INTERPRETER,
    garbageCollector = ::GarbageCollector,
    run = { entryIp, context -> interpretTraceable(entryIp, context) },
    trap = { exception, vstack -> traceableTrap(store, vstack, exception) },
)

internal fun interpretNested(
    entryIp: Int,
    context: ExecutionContext,
) = if (context.config.debugInfo) interpretTraceable(entryIp, context) else interpret(entryIp, context)

private val NESTED_INTERPRETER = NestedInterpreter(::interpretNested)

// Keep dispatch separate so HotSpot OSR need not preserve invocation setup
// and result processing state across every handler call
private fun interpret(
    entryIp: Int,
    context: ExecutionContext,
) {
    var ip = entryIp
    val vstack = context.vstack
    val instructions = context.store.program.instructions
    dispatch@ while (true) {
        // Dispatch three instructions per iteration to amortise the loop branch.
        // Larger unrolls add indirect-call sites and safepoint metadata.
        ip = instructions[ip](vstack, context, ip + 1)
        if (ip < 0 || ip >= instructions.size) {
            break@dispatch
        }
        ip = instructions[ip](vstack, context, ip + 1)
        if (ip < 0 || ip >= instructions.size) {
            break@dispatch
        }
        ip = instructions[ip](vstack, context, ip + 1)
        if (ip < 0 || ip >= instructions.size) {
            break@dispatch
        }
    }
}

// Keeping the IP live for the handler costs a store per dispatch on HotSpot,
// so this copy of interpret only runs when trap diagnostics are enabled.
private fun interpretTraceable(
    entryIp: Int,
    context: ExecutionContext,
) {
    var ip = entryIp
    val vstack = context.vstack
    val instructions = context.store.program.instructions
    try {
        dispatch@ while (true) {
            ip = instructions[ip](vstack, context, ip + 1)
            if (ip < 0 || ip >= instructions.size) {
                break@dispatch
            }
            ip = instructions[ip](vstack, context, ip + 1)
            if (ip < 0 || ip >= instructions.size) {
                break@dispatch
            }
            ip = instructions[ip](vstack, context, ip + 1)
            if (ip < 0 || ip >= instructions.size) {
                break@dispatch
            }
        }
    } catch (exception: InvocationException) {
        throw exception.at(ip)
    } catch (_: GuestHeapOutOfMemoryException) {
        throw InvocationException(InvocationError.GuestHeapOutOfMemory).at(ip)
    }
}
