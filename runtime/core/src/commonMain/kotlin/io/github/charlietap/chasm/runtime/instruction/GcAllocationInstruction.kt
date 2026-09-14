package io.github.charlietap.chasm.runtime.instruction

/** Allocation preflight must expose the complete frame to the guest collector. */
sealed interface GcAllocationInstruction : LinkedInstruction {
    /** The compiler finalizes this before publishing the function. */
    var frameSlots: Int
}
