package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.SubDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.ElementSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryElementSectionDecoder(
    private val elementSegmentDecoder: ElementSegmentDecoder = ::BinaryElementSegmentDecoder,
    private val vectorDecoder: VectorDecoder<ElementSegment> = ::BinaryVectorDecoder,
) : ElementSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<ElementSection, WasmDecodeError> = binding {

        var idx = 0u
        val subDecoder: SubDecoder<ElementSegment> = { _ ->
            elementSegmentDecoder(reader, Index.ElementIndex(idx)).also {
                idx++
            }
        }

        val segments = vectorDecoder(reader, subDecoder).bind()

        ElementSection(segments.vector)
    }
}
