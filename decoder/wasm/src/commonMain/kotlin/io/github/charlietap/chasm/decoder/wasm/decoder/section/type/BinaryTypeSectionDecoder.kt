package io.github.charlietap.chasm.decoder.wasm.decoder.section.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive.BinaryRecursiveTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive.RecursiveTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.TypeSection

internal class BinaryTypeSectionDecoder(
    private val vectorLengthDecoder: VectorLengthDecoder = ::BinaryVectorLengthDecoder,
    private val recursiveTypeDecoder: RecursiveTypeDecoder = ::BinaryRecursiveTypeDecoder,
) : TypeSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<TypeSection, WasmDecodeError> = binding {

        val vectorLength = vectorLengthDecoder(reader).bind()

        val types = (0u..<vectorLength.length).map { idx ->
            val recursiveType = recursiveTypeDecoder(reader).bind()
            Type(Index.TypeIndex(idx), recursiveType)
        }

        TypeSection(types)
    }
}
