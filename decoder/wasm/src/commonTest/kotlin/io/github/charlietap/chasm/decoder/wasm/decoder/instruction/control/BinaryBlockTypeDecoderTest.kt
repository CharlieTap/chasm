package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.BlockType
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.toSignedLeb128
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryBlockTypeDecoderTest {

    @Test
    fun `can decode an empty block type`() {

        val peekReader = FakeUByteReader {
            Ok(BLOCK_TYPE_EMPTY)
        }

        var consumedByte = false
        val emptyByteReader: () -> Result<Byte, WasmDecodeError> = {
            consumedByte = true
            Ok(0x00)
        }
        val reader = FakeWasmBinaryReader(fakeByteReader = emptyByteReader, fakePeekReader = { peekReader })

        val expected = Ok(BlockType.Empty)

        val actual = BinaryBlockTypeDecoder(reader)

        assertEquals(expected, actual)
        assertEquals(true, consumedByte)
    }

    @Test
    fun `can decode value type block type`() {

        val valueTypes = NUMBER_TYPE_RANGE.asSequence() +
            VECTOR_TYPE_RANGE.asSequence() +
            REFERENCE_TYPE_RANGE.asSequence()

        val iter = valueTypes.iterator()
        val peekReader = FakeUByteReader {
            Ok(iter.next().toUByte())
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })

        val valueTypeDecoder: ValueTypeDecoder = { _ ->
            Ok(ValueType.Number(NumberType.I32))
        }

        val expected = Ok(BlockType.ValType(ValueType.Number(NumberType.I32)))

        valueTypes.forEach { _ ->
            val actual = BinaryBlockTypeDecoder(reader, valueTypeDecoder)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `can decode signed type index block type`() {

        val expectedInt = 117u
        val longReader = {
            Ok(expectedInt.toLong())
        }

        val peekReader = FakeUByteReader {
            Ok(expectedInt.toInt().toSignedLeb128().first().toUByte())
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader }, fakeLongReader = longReader)

        val expected = Ok(BlockType.SignedTypeIndex(Index.TypeIndex(expectedInt)))

        val actual = BinaryBlockTypeDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = BinaryBlockTypeDecoder(reader)

        assertEquals(err, actual)
    }
}
