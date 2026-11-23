@file:JvmName("ManagedOps")

package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.executor.invoker.compiled.COMPILED_CALL_COST
import io.github.charlietap.chasm.executor.invoker.compiled.INTERPRETED_CALL_COST
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.runtime.compiled.BindingEntry
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ALLOCATION_INTERVAL
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.maximumArrayLength
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import kotlin.jvm.JvmName

fun castFailed(): Nothing {
    throw InvocationException(InvocationError.FailedToCastReference)
}

fun arrayOutOfBounds(): Nothing {
    throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
}

fun guestHeapExhausted(): Nothing {
    throw InvocationException(InvocationError.GuestHeapOutOfMemory)
}

fun refillAllocation(
    context: ExecutionContext,
    bytes: Int,
) {
    refillAllocation(context.store, bytes)
}

fun checkArrayLength(
    length: Int,
    shift: Int,
): Int {
    if (length < 0 || length > maximumArrayLength(shift)) guestHeapExhausted()
    return length
}

internal fun chargeAllocation(
    store: Store,
    bytes: Int,
) {
    val meter = store.allocation
    meter.remaining -= bytes
    if (meter.remaining < 0) refillAllocation(store, bytes)
}

private fun refillAllocation(
    store: Store,
    bytes: Int,
) {

    val meter = store.allocation
    val overdraft = -meter.remaining
    meter.counted += ALLOCATION_INTERVAL - meter.remaining.toLong()
    meter.remaining = ALLOCATION_INTERVAL

    val limit = store.gcHeapLimit
    if (limit != null && bytes > limit) guestHeapExhausted()
    if (hasHeadroom(overdraft)) return

    Runtime.getRuntime().gc()
    if (!hasHeadroom(overdraft)) guestHeapExhausted()
}

private fun hasHeadroom(
    overdraft: Int,
): Boolean {
    val runtime = Runtime.getRuntime()
    val maximum = runtime.maxMemory()
    val headroom = maximum - (runtime.totalMemory() - runtime.freeMemory())
    return headroom >= maxOf(MINIMUM_RESERVE, maximum / RESERVE_FRACTION) + overdraft
}

private const val MINIMUM_RESERVE = 4L shl 20

private const val EXCEPTION_BYTES = 64

private const val REFERENCE_BYTES = 4

private const val RESERVE_FRACTION = 16

fun sameI31(
    first: Any?,
    second: Any?,
): Boolean {
    return first is I31Reference && second is I31Reference && first.value == second.value
}

fun functionMatches(
    context: ExecutionContext,
    function: Any?,
    rtt: Int,
): Boolean {
    return function is ManagedFunction && (function.rtt == rtt || context.heap.matchesRuntimeType(RTT(function.rtt), RTT(rtt)))
}

fun referenceTarget(
    function: Any?,
): Any? {

    if (function == null) {
        throw InvocationException(InvocationError.NonNullReferenceExpected)
    }

    return (function as ManagedFunction).entry
}

private inline fun unwindOnWasmException(
    context: ExecutionContext,
    call: () -> Unit,
) {
    val vstack = context.vstack
    val fp = vstack.fp
    val sp = vstack.sp
    try {
        call()
    } catch (exception: HostRaisedWasmException) {
        vstack.activateFrameAtDepth(fp, sp)
        context.compiledUnwinding = true
    } catch (exception: InvocationException) {
        if (exception.error != InvocationError.ThrownException) throw exception
        vstack.activateFrameAtDepth(fp, sp)
        context.compiledUnwinding = true
    }
}

fun resultReferences(
    context: ExecutionContext,
    count: Int,
): Array<Any?> {

    val references = context.resultReferences
    if (references.size >= count) {
        return references
    }

    return arrayOfNulls<Any>(count).also { grown -> context.resultReferences = grown }
}

fun throwException(
    context: ExecutionContext,
    tag: Int,
    words: LongArray?,
    references: Array<Any?>?,
) {
    context.managedException = createException(context, tag, words, references)
    context.compiledUnwinding = true
}

fun createException(
    context: ExecutionContext,
    tag: Int,
    words: LongArray?,
    references: Array<Any?>?,
): WasmException {
    return createException(context.store, tag, words ?: NO_WORDS, references ?: NO_REFERENCES)
}

fun createException(
    store: Store,
    tag: Int,
    words: LongArray,
    references: Array<Any?>,
): WasmException {
    chargeAllocation(store, EXCEPTION_BYTES + words.size * Long.SIZE_BYTES + references.size * REFERENCE_BYTES)
    return WasmException(store, tag, words, references)
}

private val NO_WORDS = LongArray(0)

private val NO_REFERENCES = arrayOfNulls<Any>(0)

fun rethrowException(
    context: ExecutionContext,
    exception: Any?,
) {
    if (exception == null) throw InvocationException(InvocationError.UnexpectedReferenceValue)
    context.managedException = exception
    context.compiledUnwinding = true
}

fun pendingExceptionTag(
    context: ExecutionContext,
): Int {

    val pending = context.managedException ?: (context.store.referenceStorage as ManagedStore).pending.take()
    context.managedException = pending

    return (pending as WasmException).tag
}

fun takeException(
    context: ExecutionContext,
): WasmException {
    val exception = context.managedException as WasmException
    context.managedException = null
    context.compiledUnwinding = false
    return exception
}

fun callObjects(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
    references: Array<Any?>?,
    depth: Int,
) {
    unwindOnWasmException(context) {
        callObjectsUntilDone(context, caller, function, values, references ?: NO_REFERENCES, depth)
    }
}

private fun callObjectsUntilDone(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
    references: Array<Any?>,
    depth: Int,
) {
    var target = function
    var targetCaller = caller
    var words = values
    var objects = references
    while (true) {
        val compiled = (target as? FunctionInstance.WasmFunction)?.callStrategy?.compiledEntry
        if (compiled is BindingEntry) {
            compiled.callObjects(context, words, objects, depth + COMPILED_CALL_COST)
            if (context.compiledUnwinding) return
        } else {
            val lane = (target as FunctionInstance.HostFunction).function as ObjectLaneHostFunction
            callObjectLane(context, targetCaller, lane, words, objects, depth)
        }
        val next = context.pendingTailFunction ?: break
        targetCaller = checkNotNull(context.pendingTailCaller)
        val results = next.resultCount
        words = checkNotNull(context.pendingTailArguments).let { arguments -> if (arguments.size >= results) arguments else arguments.copyOf(results) }
        objects = (context.pendingTailReferences ?: arrayOfNulls(results)).let { arguments -> if (arguments.size >= results) arguments else arguments.copyOf(results) }
        context.pendingTailFunction = null
        context.pendingTailCaller = null
        context.pendingTailArguments = null
        context.pendingTailReferences = null
        target = next
    }
    if (words !== values) {
        val results = target.resultCount
        words.copyInto(values, 0, 0, minOf(results, values.size))
        objects.copyInto(references, 0, 0, minOf(results, references.size, objects.size))
    }
}

fun requestTailObjects(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
    references: Array<Any?>?,
) {
    context.pendingTailFunction = function
    context.pendingTailCaller = caller
    context.pendingTailArguments = values
    context.pendingTailReferences = references
}

fun requestTailIndirectObjects(
    context: ExecutionContext,
    caller: ModuleInstance,
    table: ReferenceTable,
    type: Int,
    elementIndex: Int,
    values: LongArray,
    references: Array<Any?>?,
) {
    requestTailObjects(context, caller, indirectFunction(context, table, type, elementIndex).instance, values, references)
}

fun requestTailReferenceObjects(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: Any?,
    values: LongArray,
    references: Array<Any?>?,
) {
    if (function == null) throw InvocationException(InvocationError.NonNullReferenceExpected)
    requestTailObjects(context, caller, (function as ManagedFunction).instance, values, references)
}

fun completeTailObjects(
    context: ExecutionContext,
    values: LongArray,
    references: Array<Any?>?,
    depth: Int,
) {
    val function = checkNotNull(context.pendingTailFunction)
    val caller = checkNotNull(context.pendingTailCaller)
    val results = function.resultCount
    val words = checkNotNull(context.pendingTailArguments).let { arguments -> if (arguments.size >= results) arguments else arguments.copyOf(results) }
    val objects = (context.pendingTailReferences ?: arrayOfNulls(results)).let { arguments -> if (arguments.size >= results) arguments else arguments.copyOf(results) }
    context.pendingTailFunction = null
    context.pendingTailCaller = null
    context.pendingTailArguments = null
    context.pendingTailReferences = null
    callObjects(context, caller, function, words, objects, depth)
    if (context.compiledUnwinding) return
    words.copyInto(values, 0, 0, minOf(results, values.size))
    if (references != null) objects.copyInto(references, 0, 0, minOf(results, references.size))
}

fun callIndirectObjects(
    context: ExecutionContext,
    caller: ModuleInstance,
    table: ReferenceTable,
    type: Int,
    elementIndex: Int,
    values: LongArray,
    references: Array<Any?>?,
    depth: Int,
) {
    callObjects(context, caller, indirectFunction(context, table, type, elementIndex).instance, values, references, depth)
}

fun callReferenceObjects(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: Any?,
    values: LongArray,
    references: Array<Any?>?,
    depth: Int,
) {
    if (function == null) throw InvocationException(InvocationError.NonNullReferenceExpected)
    callObjects(context, caller, (function as ManagedFunction).instance, values, references, depth)
}

private fun callObjectLane(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: ObjectLaneHostFunction,
    values: LongArray,
    references: Array<Any?>,
    depth: Int,
) {

    val store = context.store
    val previous = context.compiledCallDepth
    val previousStore = store.compiledCallDepth
    context.compiledCallDepth = depth + INTERPRETED_CALL_COST
    store.compiledCallDepth = depth + INTERPRETED_CALL_COST
    try {
        function.call(values, references, 0, 0, caller, context)
        function.checkResults((store.referenceStorage as ManagedStore).checks, references)
    } catch (exception: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(exception.reason))
    } finally {
        context.compiledCallDepth = previous
        store.compiledCallDepth = previousStore
    }
}
