package io.github.charlietap.chasm.decoder.section.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.FunctionSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryFunctionSectionDecoder(
    private val vectorDecoder: VectorDecoder<Index.TypeIndex> = ::BinaryVectorDecoder,
    private val subDecoder: TypeIndexDecoder = ::BinaryTypeIndexDecoder,
) : FunctionSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<FunctionSection, WasmDecodeError> = binding {

        val indices = vectorDecoder(reader, subDecoder).bind()

        val builders = indices.vector.mapIndexed { index, typeIndex ->
            FunctionHeader(Index.FunctionIndex(index.toUInt()), typeIndex)
        }

        FunctionSection(builders)
    }
}
