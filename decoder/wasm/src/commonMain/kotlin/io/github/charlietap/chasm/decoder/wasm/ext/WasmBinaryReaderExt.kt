package io.github.charlietap.chasm.decoder.wasm.ext

import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun <T> WasmBinaryReader.trackBytes(
    block: WasmBinaryReader.() -> T,
): Pair<T, UInt> {
    val position = position()
    return block().let { result ->
        result to position() - position
    }
}
