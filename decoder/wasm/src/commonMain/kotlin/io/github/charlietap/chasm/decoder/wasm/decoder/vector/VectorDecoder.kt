package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal typealias VectorDecoder<T> = (DecoderContext, Decoder<T>) -> Result<Vector<T>, WasmDecodeError>

internal fun <T> VectorDecoder(
    context: DecoderContext,
    subDecoder: Decoder<T>,
): Result<Vector<T>, WasmDecodeError> = binding {

    val vecLength = context.reader.uint().bind()

    val vector = List(vecLength.toInt()) {
        subDecoder(context).bind()
    }

    Vector(vector)
}
