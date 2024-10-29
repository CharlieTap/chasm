package io.github.charlietap.chasm.decoder.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.context.scope.VectorScope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias VectorDecoder<T> = (DecoderContext, Decoder<T>) -> Result<Vector<T>, WasmDecodeError>

internal fun <T> VectorDecoder(
    context: DecoderContext,
    subDecoder: Decoder<T>,
): Result<Vector<T>, WasmDecodeError> =
    VectorDecoder(
        context = context,
        subDecoder = subDecoder,
        scope = ::VectorScope,
    )

internal inline fun <T> VectorDecoder(
    context: DecoderContext,
    crossinline subDecoder: Decoder<T>,
    crossinline scope: Scope<Int>,
): Result<Vector<T>, WasmDecodeError> = binding {

    val scopedContext = scope(context, 0).bind()
    val vecLength = context.reader.uint().bind()

    val vector = List(vecLength.toInt()) { index ->
        scopedContext.index = index
        subDecoder(scopedContext).bind()
    }

    Vector(vector)
}
