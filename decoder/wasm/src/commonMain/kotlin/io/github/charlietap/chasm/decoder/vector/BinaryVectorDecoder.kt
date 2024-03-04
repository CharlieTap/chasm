package io.github.charlietap.chasm.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun <T> BinaryVectorDecoder(
    reader: WasmBinaryReader,
    subDecoder: SubDecoder<T>,
): Result<Vector<T>, WasmDecodeError> = binding {

    val vecLength = reader.uint().bind()

    val vector = (1u..vecLength).map {
        subDecoder(reader).bind()
    }

    Vector(vector)
}
