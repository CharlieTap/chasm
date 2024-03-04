package io.github.charlietap.chasm.ext

import io.github.charlietap.chasm.reader.WasmBinaryReader

fun <T> WasmBinaryReader.trackBytes(
    block: WasmBinaryReader.() -> T,
): Pair<T, UInt> {
    val position = position()
    return block().let { result ->
        result to position() - position
    }
}
