package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.ImportSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryImportSectionDecoder(
    private val importDecoder: ImportDecoder = ::BinaryImportDecoder,
    private val vectorDecoder: VectorDecoder<Import> = ::BinaryVectorDecoder,
) : ImportSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<ImportSection, WasmDecodeError> = binding {

        val imports = vectorDecoder(reader, importDecoder).bind()

        ImportSection(imports.vector)
    }
}
