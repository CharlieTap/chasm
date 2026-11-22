@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadInvocation
import io.github.charlietap.chasm.executor.invoker.thread.traceableTrap
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.NestedInterpreter
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.resource.gc.GarbageCollector
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun WasmFunctionExecutor(
    config: RuntimeConfig,
    store: Store,
    function: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
): Result<List<Long>, InvocationError> = WasmFunctionExecutor(
    config = config,
    store = store,
    function = function,
    values = values,
    garbageCollector = ::GarbageCollector,
)

fun WasmFunctionExecutor(
    config: RuntimeConfig,
    store: Store,
    function: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
    garbageCollector: GarbageCollector,
): Result<List<Long>, InvocationError> {
    val interpreter = store.interpreter
    if (interpreter == null && function.callStrategy.compiledEntry == null) return Err(InvocationError.InterpreterUnavailable)
    return ThreadInvocation(
        config = config,
        store = store,
        instance = function,
        values = values,
        nested = interpreter,
        garbageCollector = garbageCollector,
        run = { entryIp, context -> runEntry(interpreter, config, entryIp, context) },
        trap = { exception, vstack -> entryTrap(config, store, vstack, exception) },
    )
}

@OptIn(InternalChasmApi::class)
fun RawWasmFunctionExecutor(
    config: RuntimeConfig,
    store: Store,
    function: FunctionInstance.WasmFunction,
    words: LongArray,
): Result<Unit, InvocationError> {

    val interpreter = store.interpreter
    if (interpreter == null && function.callStrategy.compiledEntry == null) return Err(InvocationError.InterpreterUnavailable)
    val parameterCount = function.functionType.params.types.size

    return ThreadInvocation(
        config = config,
        store = store,
        instance = function,
        nested = interpreter,
        garbageCollector = ::GarbageCollector,
        arguments = { vstack ->
            var index = 0
            while (index < parameterCount) {
                vstack.setFrameSlot(index, words[index])
                index++
            }
        },
        run = { entryIp, context -> runEntry(interpreter, config, entryIp, context) },
        trap = { exception, vstack -> entryTrap(config, store, vstack, exception) },
        results = { vstack, count ->
            var index = count - 1
            while (index >= 0) {
                words[index] = vstack.pop()
                index--
            }
        },
    )
}

private fun runEntry(
    interpreter: NestedInterpreter?,
    config: RuntimeConfig,
    entryIp: Int,
    context: ExecutionContext,
) {
    when {
        interpreter != null -> interpreter.interpret(entryIp, context)
        config.debugInfo -> runCompiledTraceable(entryIp, context)
        else -> runCompiled(entryIp, context)
    }
}

private fun entryTrap(
    config: RuntimeConfig,
    store: Store,
    vstack: ValueStack,
    exception: InvocationException,
): InvocationError {
    return if (config.debugInfo) traceableTrap(store, vstack, exception) else exception.error
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
