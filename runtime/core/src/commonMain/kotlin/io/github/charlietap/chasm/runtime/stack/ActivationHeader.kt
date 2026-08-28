package io.github.charlietap.chasm.runtime.stack

private const val REFERENCE_TAG_BITS = 8
private const val CALLER_FRAME_DELTA_BITS = 25
private const val RETURN_IP_SHIFT = REFERENCE_TAG_BITS + CALLER_FRAME_DELTA_BITS
private const val CALLER_FRAME_DELTA_MASK = (1L shl CALLER_FRAME_DELTA_BITS) - 1L

/**
 * Packs the metadata needed to return from a Wasm activation into one stack
 * slot. The header lives at `FP + I`, immediately above the activation frame's
 * shared parameter/result interface.
 *
 * ```text
 *                        64-bit activation header
 *
 * ┌───────────────────────────────┬─────────────────────────┬────────┐
 * │           return IP           │      dynamic link       │ GC tag │
 * │            31 bits            │         25 bits         │ 8 bits │
 * │          bits 63…33           │ caller FP Δ · bits 32…8 │  zero  │
 * └───────────────────────────────┴─────────────────────────┴────────┘
 * ```
 *
 * The dynamic link is stored as the non-negative displacement from the
 * callee's FP back to the caller's FP. Decoding the header yields:
 *
 * ```text
 * caller FP  = callee FP - caller frame delta
 * return IP  = header bits 63…33
 * ```
 *
 * The return field ordinarily names the instruction after the call. The return
 * path may also interpret a compiler-defined marker in that field before
 * resuming execution.
 *
 * The low byte remains zero so an activation header cannot be mistaken for a
 * tagged reference while the unified stack is scanned. Call-site linking
 * validates that both encoded values fit their fields; this packing operation
 * is deliberately unchecked.
 */
fun activationHeader(
    returnIp: Int,
    callerFrameDelta: Int,
): Long =
    (returnIp.toLong() shl RETURN_IP_SHIFT) or
        (callerFrameDelta.toLong() shl REFERENCE_TAG_BITS)

/** Returns the dynamic-link displacement from the callee's FP to its caller's FP. */
@Suppress("NOTHING_TO_INLINE")
internal inline fun activationCallerFrameDelta(header: Long): Int =
    ((header ushr REFERENCE_TAG_BITS) and CALLER_FRAME_DELTA_MASK).toInt()

/** Returns the encoded return-IP field. */
@Suppress("NOTHING_TO_INLINE")
internal inline fun activationReturnIp(header: Long): Int = (header ushr RETURN_IP_SHIFT).toInt()
