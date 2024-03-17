package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryExportSectionDecoder(
    private val importDecoder: ExportDecoder = ::BinaryExportDecoder,
    private val vectorDecoder: VectorDecoder<Export> = ::BinaryVectorDecoder,
) : ExportSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<ExportSection, WasmDecodeError> = binding {

        val exports = vectorDecoder(reader, importDecoder).bind()

        ExportSection(exports.vector)
    }
}
