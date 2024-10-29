package io.github.charlietap.chasm.decoder.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.ExportSection

internal fun ExportSectionDecoder(
    context: DecoderContext,
): Result<ExportSection, WasmDecodeError> =
    ExportSectionDecoder(
        context = context,
        exportDecoder = ::ExportDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal inline fun ExportSectionDecoder(
    context: DecoderContext,
    noinline exportDecoder: Decoder<Export>,
    crossinline vectorDecoder: VectorDecoder<Export>,
): Result<ExportSection, WasmDecodeError> = binding {

    val exports = vectorDecoder(context, exportDecoder).bind()

    ExportSection(exports.vector)
}
