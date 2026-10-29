@file:JvmName("CompiledGc")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.extendSigned
import io.github.charlietap.chasm.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toI31
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.wrapI31
import io.github.charlietap.chasm.runtime.operations.Caster
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import kotlin.jvm.JvmName

fun structGet(
    context: ExecutionContext,
    reference: Long,
    field: Int,
): Long = context.heap.getStructFieldTrusted(reference, field)

fun structSet(
    context: ExecutionContext,
    reference: Long,
    field: Int,
    value: Long,
) = context.heap.setStructFieldTrusted(reference, field, value)

fun structGetStructGet(
    context: ExecutionContext,
    reference: Long,
    first: Int,
    second: Int,
): Long {
    val heap = context.heap
    return heap.getStructFieldTrusted(heap.getStructFieldTrusted(reference, first), second)
}

fun structNew(
    context: ExecutionContext,
    rtt: Int,
    fields: LongArray,
): Long = context.heap.allocateStruct(context, RTT(rtt), fields, 0)

fun structNewDefault(
    context: ExecutionContext,
    rtt: Int,
    defaults: LongArray,
): Long = context.heap.allocateStruct(context, RTT(rtt), defaults)

fun arrayGet(
    context: ExecutionContext,
    reference: Long,
    index: Int,
): Long = context.heap.getArrayElementTrusted(reference, index)

fun arraySet(
    context: ExecutionContext,
    reference: Long,
    index: Int,
    value: Long,
) = arrayBounds {
    context.heap.setArrayElementTrusted(reference, index, value)
}

fun arrayLen(
    context: ExecutionContext,
    reference: Long,
): Long = context.heap.arrayLengthTrusted(reference).toLong()

fun arrayNewDefault(
    context: ExecutionContext,
    rtt: Int,
    size: Int,
    default: Long,
): Long = arrayBounds {
    context.heap.allocateArrayFilled(context, RTT(rtt), size, default)
}

fun arrayCopy(
    context: ExecutionContext,
    source: Long,
    sourceOffset: Int,
    destination: Long,
    destinationOffset: Int,
    count: Int,
) = arrayBounds { context.heap.copyArrayChecked(source, sourceOffset, destination, destinationOffset, count) }

fun refCastStructGet(
    context: ExecutionContext,
    reference: Long,
    test: Any,
    field: Int,
): Long {
    if (!Caster(reference, test as ReferenceTypeTest, context)) throw InvocationException(InvocationError.FailedToCastReference)
    return context.heap.getStructFieldTrusted(reference, field)
}

fun refCast(
    context: ExecutionContext,
    reference: Long,
    test: Any,
): Long {
    if (!Caster(reference, test as ReferenceTypeTest, context)) throw InvocationException(InvocationError.FailedToCastReference)
    return reference
}

fun refTest(
    context: ExecutionContext,
    reference: Long,
    test: Any,
): Long = if (Caster(reference, test as ReferenceTypeTest, context)) 1L else 0L

fun castMatches(
    context: ExecutionContext,
    reference: Long,
    test: Any,
): Boolean = Caster(reference, test as ReferenceTypeTest, context)

fun refEq(
    first: Long,
    second: Long,
): Long = if ((first.isNullableReference() && second.isNullableReference()) || first == second) 1L else 0L

fun refIsNull(
    reference: Long,
): Long = if (reference.isNullableReference()) 1L else 0L

fun refAsNonNull(
    reference: Long,
): Long {
    if (reference.isNullableReference()) throw InvocationException(InvocationError.NonNullReferenceExpected)
    return reference
}

fun refI31(
    value: Int,
): Long = value.wrapI31().toLong()

fun i31GetSigned(
    reference: Long,
): Long = reference.toI31().extendSigned().toLong()

fun i31GetUnsigned(
    reference: Long,
): Long = reference.toI31().extendUnsigned().toLong()

internal inline fun <T> arrayBounds(
    operation: () -> T,
): T = try {
    operation()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
}
