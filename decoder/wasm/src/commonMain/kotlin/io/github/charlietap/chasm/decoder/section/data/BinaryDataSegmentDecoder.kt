package io.github.charlietap.chasm.decoder.section.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryMemoryIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryByteVectorDecoder
import io.github.charlietap.chasm.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryDataSegmentDecoder(
    reader: WasmBinaryReader,
    dataIndex: Index.DataIndex,
): Result<DataSegment, WasmDecodeError> =
    BinaryDataSegmentDecoder(
        reader = reader,
        dataIndex = dataIndex,
        expressionDecoder = ::BinaryExpressionDecoder,
        memoryIndexDecoder = ::BinaryMemoryIndexDecoder,
        byteVectorDecoder = ::BinaryByteVectorDecoder,
    )

fun BinaryDataSegmentDecoder(
    reader: WasmBinaryReader,
    dataIndex: Index.DataIndex,
    expressionDecoder: ExpressionDecoder,
    memoryIndexDecoder: MemoryIndexDecoder,
    byteVectorDecoder: ByteVectorDecoder,
): Result<DataSegment, WasmDecodeError> = binding {

    when (val segment = reader.uint().bind()) {
        SEGMENT_ACTIVE_NO_MEM -> {
            val expression = expressionDecoder(reader).bind()
            val data = byteVectorDecoder(reader).bind()
            val mode = DataSegment.Mode.Active(Index.MemoryIndex(0u), expression)

            DataSegment(dataIndex, data.bytes, mode)
        }
        SEGMENT_PASSIVE -> {
            val data = byteVectorDecoder(reader).bind()
            val mode = DataSegment.Mode.Passive

            DataSegment(dataIndex, data.bytes, mode)
        }
        SEGMENT_ACTIVE -> {
            val memIndex = memoryIndexDecoder(reader).bind()
            val expression = expressionDecoder(reader).bind()
            val data = byteVectorDecoder(reader).bind()
            val mode = DataSegment.Mode.Active(memIndex, expression)

            DataSegment(dataIndex, data.bytes, mode)
        }
        else -> Err(SectionDecodeError.UnknownDataSegment(segment)).bind<DataSegment>()
    }
}

internal const val SEGMENT_ACTIVE_NO_MEM: UInt = 0u
internal const val SEGMENT_PASSIVE: UInt = 1u
internal const val SEGMENT_ACTIVE: UInt = 2u
