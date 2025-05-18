package io.github.charlietap.chasm.runtime

data class Heap(
    var sizeInBytes: Long = 0,
    val arrayReferencePool: ArrayDeque<Int> = ArrayDeque(),
    val structReferencePool: ArrayDeque<Int> = ArrayDeque(),
)
