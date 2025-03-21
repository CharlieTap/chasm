package io.github.charlietap.chasm.type

import kotlin.jvm.JvmInline

data class RecursiveType(
    var subTypes: List<SubType>,
    var state: State,
) : Type {

    // Represents the state of a RecursiveType. Each state enforces different invariants
    // about which ConcreteHeapTypes are allowed within the recursive type.
    @JvmInline
    value class State(val value: Byte) {
        companion object {
            // Initial state immediately after decoding.
            // Only ConcreteHeapType.TypeIndex instances exist in this state.
            val SYNTAX = State(0)

            // Type indices pointing to recursive types within the same group
            // have been replaced with ConcreteHeapType.RecursiveTypeIndex.
            val INTERNAL_SUBSTITUTED = State(1)

            // Type indices referring to recursive types outside the group
            // have been replaced with ConcreteHeapType.Defined.
            val EXTERNAL_SUBSTITUTED = State(2)

            // Final form for cross-module usage:
            // - Type indices for recursive types in the same group are replaced with ConcreteHeapType.RecursiveTypeIndex.
            // - Type indices for external recursive types are replaced with ConcreteHeapType.Defined.
            // No module-contextual ConcreteHeapType.TypeIndex remain.
            val CLOSED = State(3)

            // All ConcreteHeapTypes in the RecursiveType are now ConcreteHeapType.Defined.
            val UNROLLED = State(4)
        }
    }
}
