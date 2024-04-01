package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.packedType
import io.github.charlietap.chasm.fixture.type.valueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryStorageTypeDecoderTest {

    @Test
    fun `can decode an encoded value type`() {

        val valueTypes = NUMBER_TYPE_RANGE.asSequence() +
            VECTOR_TYPE_RANGE.asSequence() +
            REFERENCE_TYPE_RANGE.asSequence()

        val iter = valueTypes.iterator()
        val peekReader = FakeUByteReader {
            Ok(iter.next().toUByte())
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })

        val packedTypeDecoder: PackedTypeDecoder = { _ ->
            fail("PackedTypeDecoder should not be called in this scenario")
        }

        val expectedValueType = valueType()
        val valueTypeDecoder: ValueTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(expectedValueType)
        }

        val expectedStorageType = StorageType.Value(expectedValueType)

        val actual = BinaryStorageTypeDecoder(reader, packedTypeDecoder, valueTypeDecoder)

        assertEquals(Ok(expectedStorageType), actual)
    }

    @Test
    fun `can decode an encoded packed type`() {

        val packedTypes = PACKED_TYPE_RANGE.asSequence()

        val iter = packedTypes.iterator()
        val peekReader = FakeUByteReader {
            Ok(iter.next().toUByte())
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })

        val expectedPackedType = packedType()
        val packedTypeDecoder: PackedTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(expectedPackedType)
        }

        val valueTypeDecoder: ValueTypeDecoder = { _ ->
            fail("ValueTypeDecoder should not be called in this scenario")
        }

        val expectedStorageType = StorageType.Packed(expectedPackedType)

        val actual = BinaryStorageTypeDecoder(reader, packedTypeDecoder, valueTypeDecoder)

        assertEquals(Ok(expectedStorageType), actual)
    }

    @Test
    fun `throws an InvalidStorageType error when byte is unrecognised`() {

        val byte: UByte = 0x00u

        val peekReader = FakeUByteReader { Ok(byte) }

        val actual = BinaryStorageTypeDecoder(FakeWasmBinaryReader(fakePeekReader = { peekReader }))
        assertEquals(Err(TypeDecodeError.InvalidStorageType(byte)), actual)
    }
}
