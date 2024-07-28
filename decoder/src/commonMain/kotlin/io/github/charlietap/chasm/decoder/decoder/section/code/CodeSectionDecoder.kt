package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.CodeSection

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
