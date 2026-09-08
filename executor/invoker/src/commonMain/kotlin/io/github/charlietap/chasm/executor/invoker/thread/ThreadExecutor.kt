package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
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
        Err(exception.error).bind()
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
