package io.github.charlietap.chasm.decoder.wasm.decoder.section.data

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.ByteVector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryDataSegmentDecoderTest {

    @Test
    fun `can decode an active with no memory index data segment`() {

        val segmentId = SEGMENT_ACTIVE_NO_MEM

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val dataIndex = Index.DataIndex(117u)
        val data = ubyteArrayOf()
        val expression = Expression(emptyList())
        val mode = DataSegment.Mode.Active(Index.MemoryIndex(0u), expression)

        val expected = Ok(DataSegment(dataIndex, data, mode))

        val byteVectorDecoder: ByteVectorDecoder = { _ ->
            Ok(ByteVector(data, 0u))
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(expression)
        }

        val memoryIndexDecoder: MemoryIndexDecoder = { _ ->
            fail("memory index decoder should not be called whilst decoding data segment active no memory")
        }

        val actual = BinaryDataSegmentDecoder(
            reader = reader,
            dataIndex = dataIndex,
            expressionDecoder = expressionDecoder,
            memoryIndexDecoder = memoryIndexDecoder,
            byteVectorDecoder = byteVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a passive data segment`() {

        val segmentId = SEGMENT_PASSIVE

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val dataIndex = Index.DataIndex(117u)
        val data = ubyteArrayOf()
        val mode = DataSegment.Mode.Passive

        val expected = Ok(DataSegment(dataIndex, data, mode))

        val byteVectorDecoder: ByteVectorDecoder = { _ ->
            Ok(ByteVector(data, 0u))
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            fail("expression decoder should not be called whilst decoding data segment passive")
        }

        val memoryIndexDecoder: MemoryIndexDecoder = { _ ->
            fail("memory index decoder should not be called whilst decoding data segment active no memory")
        }

        val actual = BinaryDataSegmentDecoder(
            reader = reader,
            dataIndex = dataIndex,
            expressionDecoder = expressionDecoder,
            memoryIndexDecoder = memoryIndexDecoder,
            byteVectorDecoder = byteVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an active data segment`() {

        val segmentId = SEGMENT_ACTIVE

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val dataIndex = Index.DataIndex(117u)
        val memIndex = Index.MemoryIndex(118u)
        val data = ubyteArrayOf()
        val expression = Expression(emptyList())
        val mode = DataSegment.Mode.Active(memIndex, expression)

        val expected = Ok(DataSegment(dataIndex, data, mode))

        val byteVectorDecoder: ByteVectorDecoder = { _ ->
            Ok(ByteVector(data, 0u))
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(expression)
        }

        val memoryIndexDecoder: MemoryIndexDecoder = { _ ->
            Ok(memIndex)
        }

        val actual = BinaryDataSegmentDecoder(
            reader = reader,
            dataIndex = dataIndex,
            expressionDecoder = expressionDecoder,
            memoryIndexDecoder = memoryIndexDecoder,
            byteVectorDecoder = byteVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryDataSegmentDecoder(reader, Index.DataIndex(117u))

        assertEquals(expected, actual)
    }
}
