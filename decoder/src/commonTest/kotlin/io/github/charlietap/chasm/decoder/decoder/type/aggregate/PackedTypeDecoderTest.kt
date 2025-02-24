package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.type.PackedType
import kotlin.test.Test
import kotlin.test.assertEquals

class PackedTypeDecoderTest {

    @Test
    fun `can decode an encoded abstract heap type`() {

        var byte: UByte = 0u
        val reader = FakeUByteReader { Ok(byte) }
        val context = decoderContext(reader)

        val packedTypes = mapOf(
            PACKED_TYPE_I8 to PackedType.I8,
            PACKED_TYPE_I16 to PackedType.I16,
        )

        packedTypes.forEach { (packedTypeByte, packedType) ->

            byte = packedTypeByte

            val actual = PackedTypeDecoder(context)
            assertEquals(Ok(packedType), actual)
        }
    }

    @Test
    fun `throws an InvalidPackedType error when byte is unrecognised`() {

        val byte: UByte = 0x00u
        val reader = FakeUByteReader { Ok(byte) }
        val context = decoderContext(reader)

        val actual = PackedTypeDecoder(context)
        assertEquals(Err(TypeDecodeError.InvalidPackedType(byte)), actual)
    }
}
