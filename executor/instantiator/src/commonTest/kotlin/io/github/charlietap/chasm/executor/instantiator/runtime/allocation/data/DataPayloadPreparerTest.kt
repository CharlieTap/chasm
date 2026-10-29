package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.data

import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataReader
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataPayloadPreparer
import io.github.charlietap.chasm.fixture.executor.instantiator.constantExpression
import io.github.charlietap.chasm.fixture.executor.instantiator.moduleDeclarations
import io.github.charlietap.chasm.runtime.error.InstantiationError
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DataPayloadPreparerTest {

    private val source = ByteArray(64) { index -> index.toByte() }
    private val active = DataSegmentDeclaration.Mode.Active(Index.MemoryIndex(0u), constantExpression())

    @Test
    fun `a reader that knows its length leaves active payloads to be read into memory and stays open`() {
        val reader = FakeReader(source, sized = true)
        val module = moduleDeclarations(
            dataSegments = listOf(
                DataSegmentDeclaration(DataPayload.External(0, 8), active),
                DataSegmentDeclaration(DataPayload.External(8, 4), DataSegmentDeclaration.Mode.Passive),
                DataSegmentDeclaration(DataPayload.Inline(byteArrayOf(9)), active),
            ),
            dataSource = { reader },
        )

        val data = DataPayloadPreparer(module).getOrThrow { IllegalStateException(it.toString()) }

        assertNull(data.payloads[0])
        assertContentEquals(source.copyOfRange(8, 12), data.payloads[1])
        assertContentEquals(byteArrayOf(9), data.payloads[2])
        assertSame(reader, data.reader)
        assertEquals(listOf(8L), reader.reads)
        assertEquals(false, reader.closed)

        data.close()
        assertTrue(reader.closed)
        assertNull(data.reader)
        assertTrue(data.payloads.all { payload -> payload == null })
    }

    @Test
    fun `a payload outside a reader's length fails before anything is read`() {
        val reader = FakeReader(source, sized = true)
        val module = moduleDeclarations(
            dataSegments = listOf(
                DataSegmentDeclaration(DataPayload.External(0, 4), DataSegmentDeclaration.Mode.Passive),
                DataSegmentDeclaration(DataPayload.External(60, 8), active),
            ),
            dataSource = { reader },
        )

        val result = DataPayloadPreparer(module)

        assertIs<InstantiationError.DataUnavailable>(result.getError())
        assertEquals(emptyList(), reader.reads)
        assertTrue(reader.closed)
    }

    @Test
    fun `a reader without a length reads every payload in order and is closed`() {
        val reader = FakeReader(source, sized = false)
        val module = moduleDeclarations(
            dataSegments = listOf(
                DataSegmentDeclaration(DataPayload.External(0, 8), active),
                DataSegmentDeclaration(DataPayload.External(8, 4), DataSegmentDeclaration.Mode.Passive),
            ),
            dataSource = { reader },
        )

        val data = DataPayloadPreparer(module).getOrThrow { IllegalStateException(it.toString()) }

        assertContentEquals(source.copyOfRange(0, 8), data.payloads[0])
        assertContentEquals(source.copyOfRange(8, 12), data.payloads[1])
        assertNull(data.reader)
        assertEquals(listOf(0L, 8L), reader.reads)
        assertTrue(reader.closed)
    }

    @Test
    fun `external payloads without a data source fail`() {
        val module = moduleDeclarations(dataSegments = listOf(DataSegmentDeclaration(DataPayload.External(0, 8), active)))

        assertIs<InstantiationError.DataUnavailable>(DataPayloadPreparer(module).getError())
    }

    @Test
    fun `a reader that throws an error is closed and the error propagates`() {
        val failure = AssertionError("reader failed")
        var closed = false
        val reader = object : DataReader {
            override fun read(
                offset: Long,
                size: Int,
            ): ByteArray = throw failure

            override fun close() {
                closed = true
            }
        }
        val module = moduleDeclarations(
            dataSegments = listOf(DataSegmentDeclaration(DataPayload.External(0, 8), DataSegmentDeclaration.Mode.Passive)),
            dataSource = { reader },
        )

        val thrown = assertFailsWith<AssertionError> { DataPayloadPreparer(module) }

        assertSame(failure, thrown)
        assertTrue(closed)
    }

    private class FakeReader(
        private val bytes: ByteArray,
        sized: Boolean,
    ) : DataReader {

        val reads = ArrayList<Long>()
        var closed = false

        override val length: Long? = if (sized) bytes.size.toLong() else null

        override fun read(
            offset: Long,
            size: Int,
        ): ByteArray {
            reads += offset
            return bytes.copyOfRange(offset.toInt(), offset.toInt() + size)
        }

        override fun close() {
            closed = true
        }
    }
}
