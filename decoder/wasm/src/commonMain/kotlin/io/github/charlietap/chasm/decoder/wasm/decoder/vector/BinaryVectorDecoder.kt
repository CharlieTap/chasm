package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun <T> BinaryVectorDecoder(
    reader: WasmBinaryReader,
    subDecoder: SubDecoder<T>,
): Result<Vector<T>, WasmDecodeError> = binding {

    val vecLength = reader.uint().bind()

    val vector = List(vecLength.toInt()) {
        subDecoder(reader).bind()
    }

    Vector(vector)
}
