package io.github.charlietap.chasm.runtime.diagnostic

/**
 * Per-frame arrays are aligned and innermost first: the first IP is the
 * faulting instruction, later IPs are call sites. Nothing here retains the
 * store, stack or heap.
 */
class TrapSnapshot(
    val functionAddresses: IntArray,
    val ips: IntArray,
    val entryIps: IntArray,
    val endIps: IntArray,
    val omittedFrames: Int,
    /** Index of the first frame after the omitted ones. */
    val omissionIndex: Int,
    /** False when the walk stopped at an inconsistent activation header. */
    val complete: Boolean,
    /** Starting at the innermost frame's FP. */
    val innermostFrameSlots: LongArray,
    /** Of the innermost function's module instance. */
    val memorySizes: LongArray,
) {
    val frameCount: Int
        get() = functionAddresses.size
}
