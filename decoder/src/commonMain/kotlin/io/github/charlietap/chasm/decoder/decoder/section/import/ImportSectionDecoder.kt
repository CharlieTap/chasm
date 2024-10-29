package io.github.charlietap.chasm.decoder.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.ImportSection

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

    val imports = vectorDecoder(context, importDecoder).bind().vector

    context.imports = imports
    ImportSection(imports)
}
