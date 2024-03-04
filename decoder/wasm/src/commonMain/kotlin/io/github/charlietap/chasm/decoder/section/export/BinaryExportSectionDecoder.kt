package io.github.charlietap.chasm.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.ExportSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryExportSectionDecoder(
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
