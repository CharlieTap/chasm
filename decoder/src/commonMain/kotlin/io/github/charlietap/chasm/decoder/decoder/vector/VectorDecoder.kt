package io.github.charlietap.chasm.decoder.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.context.scope.ScopedDecoder
import io.github.charlietap.chasm.decoder.context.scope.VectorScope
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias VectorDecoder<T> = (ModuleDecoderContext, Decoder<T>) -> Result<Vector<T>, WasmDecodeError>

internal typealias CodeBodyVectorDecoder<T> =
    (CodeBodyDecoderContext, CodeBodyDecoder<T>) -> Result<Vector<T>, WasmDecodeError>

internal fun <T> CodeBodyVectorDecoder(
    context: CodeBodyDecoderContext,
    subDecoder: CodeBodyDecoder<T>,
): Result<Vector<T>, WasmDecodeError> {
    val previousIndex = context.index
    return try {
        binding {
            val vectorLength = context.reader.uint()
            val vector = List(vectorLength.toInt()) { index ->
                context.index = index
                subDecoder(context).bind()
            }
            Vector(vector)
        }
    } finally {
        context.index = previousIndex
    }
}

internal fun <T> VectorDecoder(
    context: ModuleDecoderContext,
    subDecoder: Decoder<T>,
): Result<Vector<T>, WasmDecodeError> =
    VectorDecoder(
        context = context,
        subDecoder = subDecoder,
        scope = ::VectorScope,
    )

internal inline fun <T> VectorDecoder(
    context: ModuleDecoderContext,
    crossinline subDecoder: Decoder<T>,
    crossinline scope: ScopedDecoder<Int, Vector<T>>,
): Result<Vector<T>, WasmDecodeError> = scope(
    context,
    0,
    { scopedContext ->
        binding {
            val vecLength = scopedContext.reader.uint()
            val vector = List(vecLength.toInt()) { index ->
                scopedContext.index = index
                subDecoder(scopedContext).bind()
            }
            Vector(vector)
        }
    },
)
