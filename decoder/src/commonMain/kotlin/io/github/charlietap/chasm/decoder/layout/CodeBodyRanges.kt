package io.github.charlietap.chasm.decoder.layout

internal class CodeBodyRanges(
    val starts: IntArray,
    val ends: IntArray,
    val sizes: IntArray,
) {

    val size: Int
        get() = starts.size
}
