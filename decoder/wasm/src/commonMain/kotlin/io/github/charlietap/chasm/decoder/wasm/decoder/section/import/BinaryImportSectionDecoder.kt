package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ImportSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryImportSectionDecoder(
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
