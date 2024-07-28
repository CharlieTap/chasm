package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.aggregate.PACKED_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.aggregate.StorageTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.packedType
import io.github.charlietap.chasm.fixture.type.valueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StorageTypeDecoderTest {

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
        val context = decoderContext(reader)

        val packedTypeDecoder: Decoder<PackedType> = { _ ->
            fail("PackedTypeDecoder should not be called in this scenario")
        }

        val expectedValueType = valueType()
        val valueTypeDecoder: Decoder<ValueType> = { _context ->
            assertEquals(context, _context)
            Ok(expectedValueType)
        }

        val expectedStorageType = StorageType.Value(expectedValueType)

        val actual = StorageTypeDecoder(context, packedTypeDecoder, valueTypeDecoder)

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
        val context = decoderContext(reader)

        val expectedPackedType = packedType()
        val packedTypeDecoder: Decoder<PackedType> = { _context ->
            assertEquals(context, _context)
            Ok(expectedPackedType)
        }

        val valueTypeDecoder: Decoder<ValueType> = { _ ->
            fail("ValueTypeDecoder should not be called in this scenario")
        }

        val expectedStorageType = StorageType.Packed(expectedPackedType)

        val actual = StorageTypeDecoder(context, packedTypeDecoder, valueTypeDecoder)

        assertEquals(Ok(expectedStorageType), actual)
    }

    @Test
    fun `throws an InvalidStorageType error when byte is unrecognised`() {

        val byte: UByte = 0x00u

        val peekReader = FakeUByteReader { Ok(byte) }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })
        val context = decoderContext(reader)

        val actual = StorageTypeDecoder(context)
        assertEquals(Err(TypeDecodeError.InvalidStorageType(byte)), actual)
    }
}
