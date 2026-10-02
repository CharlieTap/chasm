package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.executor.invoker.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.executor.invoker.function.initializeLocals
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
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
): Result<List<Long>, InvocationError> = executeThread(
    config = config,
    store = store,
    instance = instance,
    values = values,
    garbageCollector = garbageCollector,
    interpret = { entryIp, context -> interpret(entryIp, context) },
    trap = { exception, _ -> exception.error },
)

internal fun TraceableThreadExecutor(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
): Result<List<Long>, InvocationError> = executeThread(
    config = config,
    store = store,
    instance = instance,
    values = values,
    garbageCollector = ::GarbageCollector,
    interpret = { entryIp, context -> interpretTraceable(entryIp, context) },
    trap = { exception, vstack -> traceableTrap(store, vstack, exception) },
)

internal inline fun executeThread(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
    crossinline garbageCollector: GarbageCollector,
    crossinline interpret: (Int, ExecutionContext) -> Unit,
    crossinline trap: (InvocationException, ValueStack) -> InvocationError,
): Result<List<Long>, InvocationError> = binding {
    val callStrategy = instance.callStrategy
    val vstack = ValueStack(callStrategy.frameSlots)
    val context = ExecutionContext(
        vstack = vstack,
        store = store,
        instance = instance.module,
        config = config,
    )

    val results = instance.functionType.results.types.size
    vstack.writeRootActivationHeader(callStrategy.interfaceSlotCount)
    vstack.activateFrame(ROOT_FP, callStrategy.frameSlots)
    values.forEachIndexed { index, value ->
        vstack.setFrameSlot(index, value.toLongFromBoxed())
    }
    initializeLocals(vstack, callStrategy, ROOT_FP)
    try {
        interpret(callStrategy.entryIp, context)
    } catch (exception: InvocationException) {
        Err(trap(exception, vstack)).bind()
    } catch (_: GuestHeapOutOfMemoryException) {
        Err(InvocationError.GuestHeapOutOfMemory).bind()
    }

    if (vstack.fp != ROOT_FP || vstack.sp != results) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<Long>>()
    }

    if (
        config.gcStrategy == GCStrategy.ARENA &&
        context.heap.shouldCollectGarbage(config.gcThreshold.bytes)
    ) {
        garbageCollector(store, vstack).bind()
    }

    List(results) {
        vstack.pop()
    }
        .asReversed()
}

private fun traceableTrap(
    store: Store,
    vstack: ValueStack,
    exception: InvocationException,
): InvocationError {
    val error = exception.error
    // A guest exception escaping this way has already unwound its frames.
    if (error == InvocationError.ThrownException || exception.faultIp == InvocationException.UNKNOWN_FAULT_IP) {
        return error
    }
    return InvocationError.Trapped(error, TrapSnapshot(store, vstack, exception.faultIp))
}

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

private const val ROOT_FP = 0

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
