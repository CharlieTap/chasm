package io.github.charlietap.chasm.runtime.component.canonical

class CanonicalValueTupleLayout(
    val layouts: IntArray,
    val offsets32: UIntArray,
    val size32: UInt,
    val alignment32: UInt,
    val flatCount: Int,
) {
    companion object {
        val Empty = CanonicalValueTupleLayout(
            layouts = intArrayOf(),
            offsets32 = uintArrayOf(),
            size32 = 0u,
            alignment32 = 1u,
            flatCount = 0,
        )
    }
}
