package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.executor.invoker.function.initializeLocals
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.NestedInterpreter
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.resource.gc.GarbageCollector
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

@InternalChasmApi
@Suppress("DEPRECATION")
inline fun ThreadInvocation(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
    nested: NestedInterpreter?,
    crossinline garbageCollector: GarbageCollector,
    crossinline run: (Int, ExecutionContext) -> Unit,
    crossinline trap: (InvocationException, ValueStack) -> InvocationError,
): Result<List<Long>, InvocationError> = binding {
    val callStrategy = instance.callStrategy
    val vstack = try {
        ValueStack(callStrategy.frameSlots, store.maxStackSlots)
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }
    val context = ExecutionContext(
        vstack = vstack,
        store = store,
        instance = instance.module,
        config = config,
    )
    context.nestedInterpreter = nested

    val results = instance.functionType.results.types.size
    vstack.writeRootActivationHeader(callStrategy.interfaceSlotCount)
    vstack.activateFrame(ROOT_FP, callStrategy.frameSlots)
    values.forEachIndexed { index, value ->
        vstack.setFrameSlot(index, value.toLongFromBoxed())
    }
    initializeLocals(vstack, callStrategy, ROOT_FP)
    try {
        run(callStrategy.entryIp, context)
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

@InternalChasmApi
fun traceableTrap(
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

@PublishedApi
internal const val ROOT_FP = 0
