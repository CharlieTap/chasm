package io.github.charlietap.chasm.fixture.runtime

import io.github.charlietap.chasm.runtime.Heap

fun heap(
    sizeInBytes: Long = 0,
    arrayReferencePool: ArrayDeque<Int> = ArrayDeque(),
    structReferencePool: ArrayDeque<Int> = ArrayDeque(),
) = Heap(
    sizeInBytes = sizeInBytes,
    arrayReferencePool = arrayReferencePool,
    structReferencePool = structReferencePool,
)
