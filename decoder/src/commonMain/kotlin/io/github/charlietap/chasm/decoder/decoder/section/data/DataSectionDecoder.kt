package io.github.charlietap.chasm.decoder.decoder.section.data

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.DataSection

internal fun DataSectionDecoder(
    context: DecoderContext,
) = DataSectionDecoder(
    context = context,
    segmentDecoder = ::DataSegmentDecoder,
    vectorDecoder = ::VectorDecoder,
)

internal inline fun DataSectionDecoder(
    context: DecoderContext,
    noinline segmentDecoder: Decoder<DataSegment>,
    crossinline vectorDecoder: VectorDecoder<DataSegment>,
): Result<DataSection, WasmDecodeError> = binding {

    val segments = vectorDecoder(context, segmentDecoder).bind().vector.mapIndexed { idx, segment ->
        segment.copy(idx = Index.DataIndex(idx.toUInt()))
    }

    DataSection(segments)
}
