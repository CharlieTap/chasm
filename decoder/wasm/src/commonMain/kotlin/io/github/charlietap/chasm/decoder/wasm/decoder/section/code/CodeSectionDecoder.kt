package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.CodeSection

internal fun CodeSectionDecoder(
    context: DecoderContext,
): Result<CodeSection, WasmDecodeError> =
    CodeSectionDecoder(
        context = context,
        codeEntryDecoder = ::CodeEntryDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun CodeSectionDecoder(
    context: DecoderContext,
    codeEntryDecoder: Decoder<CodeEntry>,
    vectorDecoder: VectorDecoder<CodeEntry>,
): Result<CodeSection, WasmDecodeError> = binding {

    val indices = vectorDecoder(context, codeEntryDecoder).bind()
    val bodies = indices.vector.mapIndexed { index, codeEntry ->
        FunctionBody(Index.FunctionIndex(index.toUInt()), codeEntry.locals, codeEntry.body)
    }

    CodeSection(bodies)
}
