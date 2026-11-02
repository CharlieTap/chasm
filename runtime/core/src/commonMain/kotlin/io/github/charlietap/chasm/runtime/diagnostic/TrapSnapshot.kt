package io.github.charlietap.chasm.runtime.diagnostic

/**
 * Per-frame arrays are aligned and innermost first: the first IP is the
 * faulting instruction, later IPs are call sites. A frame of compiled code
 * has [COMPILED_FRAME] as its entry and end IP, and the compiled instruction
 * position, or -1, as its IP. Nothing here retains the store, stack or heap.
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
    // The access compiled code recorded when it trapped, by the innermost function's memory index.
    val memoryAccess: TrapMemoryAccess? = null,
) {
    val frameCount: Int
        get() = functionAddresses.size

    companion object {
        const val COMPILED_FRAME = -2
    }
}

/**
 * [memoryIndex] is found by the memory's identity, or -1; a module that
 * imports one memory twice needs its instruction to tell. [memorySize] is
 * the size of the memory accessed when the trap happened.
 */
class TrapMemoryAccess(
    val kind: FailedMemoryAccess.Kind,
    val memoryIndex: Int,
    val address: Long,
    val width: Long,
    val source: Boolean,
    val memorySize: Long,
)
