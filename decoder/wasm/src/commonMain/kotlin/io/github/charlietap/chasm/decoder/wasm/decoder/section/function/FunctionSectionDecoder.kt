package io.github.charlietap.chasm.decoder.wasm.decoder.section.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.FunctionSection

internal fun FunctionSectionDecoder(
    context: DecoderContext,
): Result<FunctionSection, WasmDecodeError> =
    FunctionSectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
    )

internal fun FunctionSectionDecoder(
    context: DecoderContext,
    vectorDecoder: VectorDecoder<Index.TypeIndex>,
    typeIndexDecoder: Decoder<Index.TypeIndex>,
): Result<FunctionSection, WasmDecodeError> = binding {

    val indices = vectorDecoder(context, typeIndexDecoder).bind()

    val builders = indices.vector.mapIndexed { index, typeIndex ->
        FunctionHeader(Index.FunctionIndex(index.toUInt()), typeIndex)
    }

    FunctionSection(builders)
}
