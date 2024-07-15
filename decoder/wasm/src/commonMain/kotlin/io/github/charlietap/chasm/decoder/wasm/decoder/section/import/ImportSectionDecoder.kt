package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.ImportSection

internal fun ImportSectionDecoder(
    context: DecoderContext,
): Result<ImportSection, WasmDecodeError> =
    ImportSectionDecoder(
        context = context,
        importDecoder = ::ImportDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun ImportSectionDecoder(
    context: DecoderContext,
    importDecoder: Decoder<Import>,
    vectorDecoder: VectorDecoder<Import>,
): Result<ImportSection, WasmDecodeError> = binding {

    val imports = vectorDecoder(context, importDecoder).bind()

    ImportSection(imports.vector)
}
