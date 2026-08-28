package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.executor.invoker.function.initializeLocals
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
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
    val cstack = ControlStack()
    val vstack = ValueStack(callStrategy.frameSlots)
    val context = ExecutionContext(
        cstack = cstack,
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
        var ip = callStrategy.entryIp
        val instructions = store.program.instructions
        dispatch@ while (true) {
            try {
                // Three may seem arbitrary, but it is intentional. Executing several
                // instructions per iteration amortises the cost of the jump back to the
                // top of the loop.
                // Adding iterations is not free: every slot adds another indirect call site,
                // exit branch, and more compiled code. On HotSpot these call sites are
                // megamorphic and also require their own profiling and safepoint metadata.
                ip = instructions[ip](vstack, context, ip + 1)
                if (ip.toUInt() >= instructions.size.toUInt()) {
                    break@dispatch
                }
                ip = instructions[ip](vstack, context, ip + 1)
                if (ip.toUInt() >= instructions.size.toUInt()) {
                    break@dispatch
                }
                ip = instructions[ip](vstack, context, ip + 1)
                if (ip.toUInt() >= instructions.size.toUInt()) {
                    break@dispatch
                }
            } catch (_: HostRaisedWasmException) {
                ip = ThrowRefValueExecutor(
                    vstack = vstack,
                    context = context,
                    ref = store.heap.takePendingExceptionReference(),
                )
            }
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    } catch (_: GuestHeapOutOfMemoryException) {
        Err(InvocationError.GuestHeapOutOfMemory).bind()
    }

    vstack.shrink(preserveTopN = 0, depth = results)

    if (cstack.handlersDepth() != 0 || vstack.sp != results) {
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

private const val ROOT_FP = 0
