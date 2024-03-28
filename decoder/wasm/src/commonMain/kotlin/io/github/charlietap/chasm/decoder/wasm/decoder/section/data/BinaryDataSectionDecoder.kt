package io.github.charlietap.chasm.decoder.wasm.decoder.section.data

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.SubDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.DataSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryDataSectionDecoder(
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
