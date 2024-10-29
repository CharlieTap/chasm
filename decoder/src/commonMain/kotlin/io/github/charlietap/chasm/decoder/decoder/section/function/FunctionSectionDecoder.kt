package io.github.charlietap.chasm.decoder.decoder.section.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.FunctionSection

internal fun FunctionSectionDecoder(
    context: DecoderContext,
): Result<FunctionSection, WasmDecodeError> =
    FunctionSectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
    )

internal inline fun FunctionSectionDecoder(
    context: DecoderContext,
    crossinline vectorDecoder: VectorDecoder<Index.TypeIndex>,
    noinline typeIndexDecoder: Decoder<Index.TypeIndex>,
): Result<FunctionSection, WasmDecodeError> = binding {

    val indices = vectorDecoder(context, typeIndexDecoder).bind()

    val builders = indices.vector.mapIndexed { index, typeIndex ->
        FunctionHeader(Index.FunctionIndex(index.toUInt()), typeIndex)
    }

    FunctionSection(builders)
}
