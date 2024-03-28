package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.SubDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ElementSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryElementSectionDecoder(
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
