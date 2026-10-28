package io.github.charlietap.chasm.executor.invoker.compiled

import io.github.charlietap.chasm.executor.invoker.function.initializeLocals
import io.github.charlietap.chasm.executor.invoker.function.invokeHost
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.activationHeader
import io.github.charlietap.chasm.runtime.type.RTT

const val INTERPRETED_CALL_COST = 256

const val COMPILED_CALL_COST = 4

fun interpretFunctionAt(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
    depth: Int,
) {
    val store = context.store
    val previous = context.compiledCallDepth
    val previousStore = store.compiledCallDepth
    context.compiledCallDepth = depth + INTERPRETED_CALL_COST
    store.compiledCallDepth = depth + INTERPRETED_CALL_COST
    try {
        interpretFunction(context, caller, function, values)
    } finally {
        context.compiledCallDepth = previous
        store.compiledCallDepth = previousStore
    }
}

fun callFunction(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
    depth: Int,
) {
    var target = function
    var targetCaller = caller
    var arguments = values
    while (true) {
        val compiled = (target as? FunctionInstance.WasmFunction)?.callStrategy?.compiledEntry
        if (compiled != null) {
            compiled.call(context, arguments, depth + COMPILED_CALL_COST)
            if (context.compiledUnwinding) return
        } else {
            interpretFunctionAt(context, targetCaller, target, arguments, depth)
        }
        val next = context.pendingTailFunction ?: break
        targetCaller = checkNotNull(context.pendingTailCaller)
        val nextArguments = checkNotNull(context.pendingTailArguments)
        clearPendingTailCall(context)
        val results = next.functionType.results.types.size
        target = next
        arguments = if (nextArguments.size >= results) nextArguments else nextArguments.copyOf(results)
    }
    if (arguments !== values) {
        val results = target.functionType.results.types.size
        arguments.copyInto(values, 0, 0, minOf(results, values.size))
    }
}

@OptIn(UnsafeHostApi::class)
fun interpretFunction(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
) {
    val vstack = context.vstack
    val callerFp = vstack.fp
    val callerSp = vstack.sp
    try {
        when (function) {
            is FunctionInstance.WasmFunction -> {
                val interpreter = context.nestedInterpreter
                    ?: throw InvocationException(InvocationError.InterpreterUnavailable)
                val strategy = function.callStrategy
                val calleeFp = callerSp
                val frameEnd = calleeFp + strategy.frameSlots
                vstack.ensureCapacity(frameEnd)
                val parameters = function.functionType.params.types.size
                values.copyInto(vstack.unsafeElements(), calleeFp, 0, parameters)
                initializeLocals(vstack, strategy, calleeFp)
                vstack.writeActivationHeader(calleeFp, strategy.interfaceSlotCount, activationHeader(EXIT_IP, calleeFp - callerFp))
                vstack.activateFrameAtDepth(calleeFp, frameEnd)
                interpreter.interpret(strategy.entryIp, context)
                val results = function.functionType.results.types.size
                vstack.unsafeElements().copyInto(values, 0, calleeFp, calleeFp + results)
            }
            is FunctionInstance.HostFunction -> {
                val parameters = function.functionType.params.types.size
                val results = function.functionType.results.types.size
                val base = callerSp
                vstack.ensureCapacity(base + maxOf(parameters, results))
                copySlots(values, 0, vstack.unsafeElements(), base, parameters)
                vstack.activateFrameAtDepth(callerFp, base + maxOf(parameters, results))
                function.function.invokeHost(vstack.unsafeElements(), base, base, caller, context)
                copySlots(vstack.unsafeElements(), base, values, 0, results)
            }
        }
    } catch (exception: InvocationException) {
        if (exception.error == InvocationError.ThrownException) vstack.activateFrameAtDepth(callerFp, callerSp)
        throw exception
    } catch (exception: HostRaisedWasmException) {
        vstack.activateFrameAtDepth(callerFp, callerSp)
        throw exception
    }
    vstack.activateFrameAtDepth(callerFp, callerSp)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun copySlots(
    source: LongArray,
    sourceOffset: Int,
    destination: LongArray,
    destinationOffset: Int,
    count: Int,
) {
    if (count > SMALL_COPY) {
        source.copyInto(destination, destinationOffset, sourceOffset, sourceOffset + count)
    } else {
        for (index in 0 until count) destination[destinationOffset + index] = source[sourceOffset + index]
    }
}

private const val SMALL_COPY = 16

fun resolveIndirectFunction(
    context: ExecutionContext,
    table: TableInstance,
    type: RTT,
    elementIndex: Int,
): FunctionInstance {
    val address = table.element(elementIndex).toFunctionAddress()
    val function = context.store.function(address)
    if (!context.heap.matchesRuntimeType(function.rtt, type)) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }
    return function
}

fun requestTailCall(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
) {
    context.pendingTailFunction = function
    context.pendingTailCaller = caller
    context.pendingTailArguments = values
}

fun completeTailCall(
    context: ExecutionContext,
    values: LongArray,
    depth: Int,
) {
    val function = checkNotNull(context.pendingTailFunction)
    val caller = checkNotNull(context.pendingTailCaller)
    val arguments = checkNotNull(context.pendingTailArguments)
    clearPendingTailCall(context)
    callFunction(context, caller, function, arguments, depth)
    if (context.compiledUnwinding) return
    val results = function.functionType.results.types.size
    arguments.copyInto(values, 0, 0, minOf(results, values.size))
}

internal fun clearPendingTailCall(
    context: ExecutionContext,
) {
    context.pendingTailFunction = null
    context.pendingTailCaller = null
    context.pendingTailArguments = null
}

fun raiseUnwinding(
    context: ExecutionContext,
): Nothing {
    context.compiledUnwinding = false
    throw HostRaisedWasmException.instance
}
