package io.github.charlietap.chasm.decoder.decoder.section.data

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.ByteVector
import io.github.charlietap.chasm.decoder.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.dataSegment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DataSegmentDecoderTest {

    @Test
    fun `can decode an active with no memory index data segment`() {

        val segmentId = SEGMENT_ACTIVE_NO_MEM

        val reader = FakeUIntReader {
            Ok(segmentId)
        }
        val context = decoderContext(reader)

        val data = ubyteArrayOf()
        val expression = Expression(emptyList())
        val mode = DataSegment.Mode.Active(Index.MemoryIndex(0u), expression)

        val expected = Ok(dataSegment(dataIndex(0u), data, mode))

        val byteVectorDecoder: ByteVectorDecoder = { _ ->
            Ok(ByteVector(data, 0u))
        }

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(expression)
        }

        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = { _ ->
            fail("memory index decoder should not be called whilst decoding data segment active no memory")
        }

        val actual = DataSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val data = ubyteArrayOf()
        val mode = DataSegment.Mode.Passive

        val expected = Ok(dataSegment(dataIndex(0u), data, mode))

        val byteVectorDecoder: ByteVectorDecoder = { _ ->
            Ok(ByteVector(data, 0u))
        }

        val expressionDecoder: Decoder<Expression> = { _ ->
            fail("expression decoder should not be called whilst decoding data segment passive")
        }

        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = { _ ->
            fail("memory index decoder should not be called whilst decoding data segment active no memory")
        }

        val actual = DataSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val memIndex = Index.MemoryIndex(118u)
        val data = ubyteArrayOf()
        val expression = Expression(emptyList())
        val mode = DataSegment.Mode.Active(memIndex, expression)

        val expected = Ok(dataSegment(dataIndex(0u), data, mode))

        val byteVectorDecoder: ByteVectorDecoder = { _ ->
            Ok(ByteVector(data, 0u))
        }

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(expression)
        }

        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = { _ ->
            Ok(memIndex)
        }

        val actual = DataSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val actual = DataSegmentDecoder(context)

        assertEquals(expected, actual)
    }
}
