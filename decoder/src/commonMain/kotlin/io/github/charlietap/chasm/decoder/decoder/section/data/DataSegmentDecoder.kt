package io.github.charlietap.chasm.decoder.decoder.section.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.BinaryByteVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun DataSegmentDecoder(
    context: DecoderContext,
): Result<DataSegment, WasmDecodeError> =
    DataSegmentDecoder(
        context = context,
        expressionDecoder = ::ExpressionDecoder,
        memoryIndexDecoder = ::MemoryIndexDecoder,
        byteVectorDecoder = ::BinaryByteVectorDecoder,
    )

internal fun DataSegmentDecoder(
    context: DecoderContext,
    expressionDecoder: Decoder<Expression>,
    memoryIndexDecoder: Decoder<Index.MemoryIndex>,
    byteVectorDecoder: ByteVectorDecoder,
): Result<DataSegment, WasmDecodeError> = binding {

    val dataIndexPlaceholder = Index.DataIndex(0u)

    when (val segment = context.reader.uint().bind()) {
        SEGMENT_ACTIVE_NO_MEM -> {
            val expression = expressionDecoder(context).bind()
            val data = byteVectorDecoder(context.reader).bind()
            val mode = DataSegment.Mode.Active(Index.MemoryIndex(0u), expression)

            DataSegment(dataIndexPlaceholder, data.bytes, mode)
        }
        SEGMENT_PASSIVE -> {
            val data = byteVectorDecoder(context.reader).bind()
            val mode = DataSegment.Mode.Passive

            DataSegment(dataIndexPlaceholder, data.bytes, mode)
        }
        SEGMENT_ACTIVE -> {
            val memIndex = memoryIndexDecoder(context).bind()
            val expression = expressionDecoder(context).bind()
            val data = byteVectorDecoder(context.reader).bind()
            val mode = DataSegment.Mode.Active(memIndex, expression)

            DataSegment(dataIndexPlaceholder, data.bytes, mode)
        }
        else -> Err(SectionDecodeError.UnknownDataSegment(segment)).bind<DataSegment>()
    }
}

internal const val SEGMENT_ACTIVE_NO_MEM: UInt = 0u
internal const val SEGMENT_PASSIVE: UInt = 1u
internal const val SEGMENT_ACTIVE: UInt = 2u
