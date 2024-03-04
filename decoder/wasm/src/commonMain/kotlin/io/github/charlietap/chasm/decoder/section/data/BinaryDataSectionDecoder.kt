package io.github.charlietap.chasm.decoder.section.data

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.SubDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.DataSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryDataSectionDecoder(
    private val segmentDecoder: DataSegmentDecoder = ::BinaryDataSegmentDecoder,
    private val vectorDecoder: VectorDecoder<DataSegment> = ::BinaryVectorDecoder,
) : DataSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<DataSection, WasmDecodeError> = binding {

        var idx = 0u
        val subDecoder: SubDecoder<DataSegment> = { _ ->
            segmentDecoder(reader, Index.DataIndex(idx)).also {
                idx++
            }
        }

        val segments = vectorDecoder(reader, subDecoder).bind()

        DataSection(segments.vector)
    }
}
