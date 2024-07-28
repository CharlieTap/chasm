package io.github.charlietap.chasm.decoder.ext

import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal fun <T> WasmBinaryReader.trackBytes(
    block: WasmBinaryReader.() -> T,
): Pair<T, UInt> {
    val position = position()
    return block().let { result ->
        result to position() - position
    }
}
