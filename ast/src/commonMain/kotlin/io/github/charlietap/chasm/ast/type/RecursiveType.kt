package io.github.charlietap.chasm.ast.type

data class RecursiveType(
    val subTypes: List<SubType>,
    var state: Byte,
) : Type {
    companion object {
        // Represents the initial state of recursive types immediately after decoding.
        const val STATE_SYNTAX: Byte = 0

        // Indicates an intermediate state where either:
        // 1. Types have been substituted during validation:
        //    - Some type indices pointing to recursive types within the same group have been substituted with recursive type indices.
        //    - However, type indices pointing to other recursive groups remain unmodified.
        //    - This state is required to support the progressive nature of validation.
        // 2. Types have been substituted during TypeAllocation as part of instantiation (just before rolling allocation):
        //    - Type indices related to recursive types outside the current group have been replaced with concrete defined type references.
        //    - This is the opposite of the validation scenario
        const val STATE_SUBSTITUTED: Byte = 1

        // Represents the final state where:
        // - All type indices pointing to recursive types in the same group are replaced with recursive type indices.
        // - Type indices pointing to types in other recursive groups are replaced with concrete defined type references.
        const val STATE_CLOSED: Byte = 2
    }
}
