package io.github.charlietap.chasm.runtime.compiled.gc

internal fun checkIndex(
    index: Int,
    size: Int,
) {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException("index $index, size $size")
    }
}

internal fun checkRange(
    offset: Int,
    count: Int,
    size: Int,
) {
    if (offset < 0 || count < 0 || offset.toLong() + count > size) {
        throw IndexOutOfBoundsException("range $offset+$count, size $size")
    }
}
