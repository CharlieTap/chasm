package io.github.charlietap.chasm.memory

// Backing capacity may exceed logical memory size. Check the whole access before
// touching storage, including stores that must not partially write on failure.
@PublishedApi
internal inline fun BoundsChecker(address: Int, byteCount: Int, upperBound: Int) {
    if (address < 0 || byteCount < 0 || address > upperBound - byteCount) {
        throw IndexOutOfBoundsException()
    }
}
