package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection

internal fun ExportSectionDecoder(
    context: DecoderContext,
): Result<ExportSection, WasmDecodeError> =
    ExportSectionDecoder(
        context = context,
        exportDecoder = ::ExportDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun ExportSectionDecoder(
    context: DecoderContext,
    exportDecoder: Decoder<Export>,
    vectorDecoder: VectorDecoder<Export>,
): Result<ExportSection, WasmDecodeError> = binding {

    val exports = vectorDecoder(context, exportDecoder).bind()

    ExportSection(exports.vector)
}
