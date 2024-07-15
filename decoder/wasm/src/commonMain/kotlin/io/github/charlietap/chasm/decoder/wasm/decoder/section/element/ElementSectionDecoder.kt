package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.ElementSection

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
