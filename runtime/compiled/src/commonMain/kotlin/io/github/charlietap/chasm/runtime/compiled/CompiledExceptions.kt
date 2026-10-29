@file:JvmName("CompiledExceptions")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import kotlin.jvm.JvmName

fun throwTag(
    context: ExecutionContext,
    tagAddress: Int,
    firstPayloadSlot: Int,
) {
    val reference = context.heap.allocateExceptionFromFrame(context, Address.Tag(tagAddress), firstPayloadSlot)
    context.heap.setPendingException(reference)
    context.compiledUnwinding = true
}

fun throwRef(
    context: ExecutionContext,
    reference: Long,
) {
    if (reference.isNullableReference()) throw InvocationException(InvocationError.UnexpectedReferenceValue)
    context.heap.setPendingException(reference)
    context.compiledUnwinding = true
}

fun catchIn(
    context: ExecutionContext,
    failure: Throwable,
    table: FunctionExceptionTable,
    region: Int,
    payload: LongArray,
): Int {
    if (failure is InvocationException && failure.error != InvocationError.ThrownException) throw failure
    if (failure !is InvocationException && failure !is HostRaisedWasmException) throw failure
    val ordinal = match(context, table, region, payload)
    if (ordinal < 0) throw failure
    return ordinal
}

fun catchPending(
    context: ExecutionContext,
    table: FunctionExceptionTable,
    region: Int,
    payload: LongArray,
): Int {
    val ordinal = match(context, table, region, payload)
    if (ordinal >= 0) context.compiledUnwinding = false
    return ordinal
}

fun pendingTag(
    context: ExecutionContext,
): Int {
    val heap = context.heap
    val reference = heap.takePendingExceptionReference()
    heap.setPendingException(reference)
    return heap.exceptionTagAddress(reference).address
}

fun takeException(
    context: ExecutionContext,
    payload: LongArray,
    fields: Int,
): Long {
    val heap = context.heap
    val reference = heap.takePendingExceptionReference()
    for (field in 0 until fields) payload[field] = heap.getExceptionFieldTrusted(reference, field)
    context.compiledUnwinding = false
    return reference
}

fun wasmException(
    failure: Throwable,
): Throwable {
    if (failure is InvocationException && failure.error == InvocationError.ThrownException) return failure
    if (failure is HostRaisedWasmException) return failure
    throw failure
}

private fun match(
    context: ExecutionContext,
    table: FunctionExceptionTable,
    region: Int,
    payload: LongArray,
): Int {
    val heap = context.heap
    val reference = heap.takePendingExceptionReference()
    val tagAddress = heap.exceptionTagAddress(reference).address
    var regionIndex = region
    while (regionIndex >= 0) {
        val candidate = table.regions[regionIndex]
        candidate.catches.forEachIndexed { index, handler ->
            if (handler.tagAddress == CompiledCatch.CATCH_ALL_TAG || handler.tagAddress == tagAddress) {
                val slots = handler.payloadSlots
                val fields = slots.size - if (handler.includeExceptionReference) 1 else 0
                if (handler.tagAddress != CompiledCatch.CATCH_ALL_TAG) {
                    for (field in 0 until fields) payload[field] = heap.getExceptionFieldTrusted(reference, field)
                }
                if (handler.includeExceptionReference) payload[slots.size - 1] = reference
                return table.firstCatchOrdinals[regionIndex] + index
            }
        }
        regionIndex = candidate.parentRegion
    }
    heap.setPendingException(reference)
    return -1
}
