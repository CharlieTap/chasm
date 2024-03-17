package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.typeIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryHeapTypeDecoderTest {

    @Test
    fun `can decode an encoded extern heap type`() {

        val opcode = HEAP_TYPE_EXTERN
        var timesCalled = 0
        val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
            timesCalled++
            Ok(opcode)
        }
        val fakeLongReader: () -> Result<Long, WasmDecodeError> = {
            Ok(117)
        }

        val peekReader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
        )

        val reader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
            fakeLongReader = fakeLongReader,
            fakePeekReader = { peekReader },
        )

        val actual = BinaryHeapTypeDecoder(reader)
        assertEquals(Ok(HeapType.Extern), actual)
        assertEquals(2, timesCalled)
    }

    @Test
    fun `can decode an encoded func heap type`() {

        val opcode = HEAP_TYPE_FUNC
        var timesCalled = 0
        val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
            timesCalled++
            Ok(opcode)
        }
        val fakeLongReader: () -> Result<Long, WasmDecodeError> = {
            Ok(117)
        }

        val peekReader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
        )

        val reader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
            fakeLongReader = fakeLongReader,
            fakePeekReader = { peekReader },
        )

        val actual = BinaryHeapTypeDecoder(reader)
        assertEquals(Ok(HeapType.Func), actual)
        assertEquals(2, timesCalled)
    }

    @Test
    fun `can decode an encoded type index heap type`() {

        val opcode: UByte = 0x00u
        val typeIndex = typeIndex(117u)
        val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }
        val fakeLongReader: () -> Result<Long, WasmDecodeError> = {
            Ok(typeIndex.idx.toLong())
        }

        val peekReader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
        )

        val reader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
            fakeLongReader = fakeLongReader,
            fakePeekReader = { peekReader },
        )

        val actual = BinaryHeapTypeDecoder(reader)
        assertEquals(Ok(HeapType.TypeIndex(typeIndex)), actual)
    }
}
