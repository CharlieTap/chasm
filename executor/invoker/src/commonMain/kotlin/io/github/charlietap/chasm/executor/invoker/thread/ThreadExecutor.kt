package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
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
    val cstack = ControlStack()
    val vstack = ValueStack(instance.function.frameSlots)
    val context = ExecutionContext(
        cstack = cstack,
        vstack = vstack,
        store = store,
        instance = instance.module,
        config = config,
    )

    values.forEach { value ->
        vstack.push(value.toLongFromBoxed())
    }

    val params = instance.functionType.params.types.size
    val results = instance.functionType.results.types.size
    val interfaceSlots = maxOf(params, results)
    cstack.push(
        ActivationFrame(
            arity = results,
            handlerDepth = 0,
            valueDepth = 0,
            instance = instance.module,
            returnIp = EXIT_IP,
        ),
    )

    vstack.framePointer = 0
    vstack.reserveFrame(instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(interfaceSlots + index, value)
    }

    try {
        var ip = instance.function.body.entryIp
        val instructions = store.program.instructions
        dispatch@ while (true) {
            // Three may seem arbitrary, but it is intentional. Executing several
            // instructions per iteration amortises the cost of the jump back to the
            // top of the loop.
            // Adding iterations is not free: every slot adds another indirect call site,
            // exit branch, and more compiled code. On HotSpot these call sites are
            // megamorphic and also require their own profiling and safepoint metadata.
            ip = instructions[ip](vstack, cstack, store, context, ip + 1)
            if (ip == EXIT_IP) {
                break@dispatch
            }
            ip = instructions[ip](vstack, cstack, store, context, ip + 1)
            if (ip == EXIT_IP) {
                break@dispatch
            }
            ip = instructions[ip](vstack, cstack, store, context, ip + 1)
            if (ip == EXIT_IP) {
                break@dispatch
            }
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (cstack.framesDepth() != 0 || cstack.handlersDepth() != 0 || vstack.depth() != results) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<Long>>()
    }

    if (
        instance.function.collectGarbageAfterInvocation &&
        store.heap.sizeInBytes >= config.gcThreshold.bytes
    ) {
        garbageCollector(store, vstack).bind()
    }

    List(results) {
        vstack.pop()
    }.asReversed()
}
