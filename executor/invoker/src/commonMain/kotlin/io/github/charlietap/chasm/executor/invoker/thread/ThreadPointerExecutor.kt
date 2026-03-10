package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.depth
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

const val EXIT_IP = -1

internal typealias ThreadPointerExecutor =
    (RuntimeConfig, Store, FunctionInstance.WasmFunction, List<ExecutionValue>) -> Result<List<Long>, InvocationError>

internal fun ThreadPointerExecutor(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
): Result<List<Long>, InvocationError> = binding {
    val body = instance.function.body
    val fusedIpBody = body.fusedIpBody
        ?: Err(InvocationError.ProgramFinishedInconsistentState).bind()

    val cstack = ControlStack()
    val vstack = ValueStack()
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

    val frame = ActivationFrame(
        arity = results,
        depths = StackDepths(0, 0, 0, 0),
        instance = instance.module,
        previousFramePointer = 0,
        frameSlotMode = true,
        returnIp = EXIT_IP,
    )
    cstack.push(frame)

    vstack.framePointer = 0
    vstack.reserveFrame(instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(interfaceSlots + index, value)
    }

    try {
        var ip = fusedIpBody.arenaEntryIp
        val codeArena = store.codeArenaArray
        while (ip != EXIT_IP) {
            ip = codeArena[ip].execute(vstack, cstack, store, context)
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (context.depth() != results) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<Long>>()
    }

    List(results) {
        vstack.pop()
    }.asReversed()
}
