package io.github.charlietap.chasm.decoder.decoder.section.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.ElementSection

internal fun ElementSectionDecoder(
    context: DecoderContext,
): Result<ElementSection, WasmDecodeError> = ElementSectionDecoder(
    context = context,
    elementSegmentDecoder = ::ElementSegmentDecoder,
    vectorDecoder = ::VectorDecoder,
)

internal fun ElementSectionDecoder(
    context: DecoderContext,
    elementSegmentDecoder: Decoder<ElementSegment>,
    vectorDecoder: VectorDecoder<ElementSegment>,
): Result<ElementSection, WasmDecodeError> = binding {

    val segments = vectorDecoder(context, elementSegmentDecoder).bind().vector.mapIndexed { idx, segment ->
        segment.copy(
            idx = Index.ElementIndex(idx.toUInt()),
        )
    }

    ElementSection(segments)
}
