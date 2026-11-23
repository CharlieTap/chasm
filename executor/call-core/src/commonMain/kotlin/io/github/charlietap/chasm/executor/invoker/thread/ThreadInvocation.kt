package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.InternalChasmApi
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
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.withLiveStack
import io.github.charlietap.chasm.runtime.value.ExecutionValue

@InternalChasmApi
inline fun ThreadInvocation(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
    nested: NestedInterpreter?,
    crossinline run: (Int, ExecutionContext) -> Unit,
    crossinline trap: (InvocationException, ValueStack) -> InvocationError,
): Result<List<Long>, InvocationError> {
    return ThreadInvocation(
        config = config,
        store = store,
        instance = instance,
        nested = nested,
        arguments = { vstack ->
            values.forEachIndexed { index, value ->
                vstack.setFrameSlot(index, value.toLongFromBoxed())
            }
        },
        run = run,
        trap = trap,
        results = { vstack, count ->
            List(count) {
                vstack.pop()
            }.asReversed()
        },
    )
}

@InternalChasmApi
inline fun <R> ThreadInvocation(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    nested: NestedInterpreter?,
    crossinline arguments: (ValueStack) -> Unit,
    crossinline run: (Int, ExecutionContext) -> Unit,
    crossinline trap: (InvocationException, ValueStack) -> InvocationError,
    crossinline results: (ValueStack, Int) -> R,
): Result<R, InvocationError> = binding {
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

    val resultCount = instance.functionType.results.types.size
    vstack.writeRootActivationHeader(callStrategy.interfaceSlotCount)
    vstack.activateFrame(ROOT_FP, callStrategy.frameSlots)
    arguments(vstack)
    initializeLocals(vstack, callStrategy, ROOT_FP)

    store.withLiveStack(vstack) {

        try {
            run(callStrategy.entryIp, context)
        } catch (exception: InvocationException) {
            Err(trap(exception, vstack)).bind()
        } catch (_: GuestHeapOutOfMemoryException) {
            Err(InvocationError.GuestHeapOutOfMemory).bind()
        }

        if (vstack.fp != ROOT_FP || vstack.sp != resultCount) {
            Err(InvocationError.ProgramFinishedInconsistentState).bind<Unit>()
        }
    }

    results(vstack, resultCount)
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
    return InvocationError.Trapped(error, TrapSnapshot(store, vstack, exception.faultIp, exception))
}

@InternalChasmApi
fun traceableTrap(
    store: Store,
    vstack: ValueStack,
    exception: InvocationException,
    entryIp: Int,
): InvocationError {

    val error = exception.error
    val faultIp = if (exception.faultIp == InvocationException.UNKNOWN_FAULT_IP) entryIp else exception.faultIp

    if (error == InvocationError.ThrownException || faultIp < 0) {
        return error
    }

    return InvocationError.Trapped(error, TrapSnapshot(store, vstack, faultIp, exception))
}

@PublishedApi
internal const val ROOT_FP = 0
