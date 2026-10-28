package io.github.charlietap.chasm.executor.invoker.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext

fun openFrame(
    context: ExecutionContext,
    frameSlots: Int,
): Long {
    val vstack = context.vstack
    val fp = vstack.fp
    val sp = vstack.sp
    if (frameSlots > Int.MAX_VALUE - sp) throw InvocationException(InvocationError.CallStackExhausted)
    vstack.ensureCapacity(sp + frameSlots)
    vstack.activateFrameAtDepth(sp, sp + frameSlots)
    return (fp.toLong() shl 32) or (sp.toLong() and 0xFFFF_FFFFL)
}

fun openRoots(
    context: ExecutionContext,
    size: Int,
): Long {
    val saved = openFrame(context, size)
    clearRoots(context, saved.toInt(), size)
    return saved
}

@OptIn(UnsafeHostApi::class)
fun clearRoots(
    context: ExecutionContext,
    start: Int,
    size: Int,
) {
    context.vstack.unsafeElements().fill(0L, start, start + size)
}

fun closeFrame(
    context: ExecutionContext,
    saved: Long,
) {
    context.vstack.activateFrameAtDepth((saved ushr 32).toInt(), saved.toInt())
}

@OptIn(UnsafeHostApi::class)
fun frame(
    context: ExecutionContext,
): LongArray = context.vstack.unsafeElements()

fun saveStack(
    context: ExecutionContext,
): Long {
    val vstack = context.vstack
    return (vstack.fp.toLong() shl 32) or (vstack.sp.toLong() and 0xFFFF_FFFFL)
}
