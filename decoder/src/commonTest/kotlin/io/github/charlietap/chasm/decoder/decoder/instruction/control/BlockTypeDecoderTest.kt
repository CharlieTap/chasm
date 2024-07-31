package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.BlockType
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockTypeDecoderTest {

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
        val reader = FakeWasmBinaryReader(
            fakeByteReader = emptyByteReader,
            fakePeekReader = { peekReader },
        )
        val context = decoderContext(reader)

        val expected = Ok(BlockType.Empty)

        val actual = BlockTypeDecoder(context)

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
        val context = decoderContext(reader)

        val valueTypeDecoder: Decoder<ValueType> = { _ ->
            Ok(i32ValueType())
        }

        val expected = Ok(BlockType.ValType(i32ValueType()))

        valueTypes.forEach { _ ->
            val actual = BlockTypeDecoder(context, valueTypeDecoder)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `can decode signed type index block type`() {

        val expectedInt = 117u

        val peekReader = FakeUByteReader {
            Ok(0x00u)
        }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeS33Reader = { Ok(expectedInt) },
        )
        val context = decoderContext(reader)

        val expected = Ok(BlockType.SignedTypeIndex(Index.TypeIndex(expectedInt)))

        val actual = BlockTypeDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = BlockTypeDecoder(context)

        assertEquals(err, actual)
    }
}
