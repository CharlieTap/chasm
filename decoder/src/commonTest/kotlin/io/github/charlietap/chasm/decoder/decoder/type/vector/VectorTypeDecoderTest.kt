package io.github.charlietap.chasm.decoder.decoder.type.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.type.VectorType
import kotlin.test.Test
import kotlin.test.assertEquals

class VectorTypeDecoderTest {

    @Test
    fun `can decode an encoded vector type`() {

        val reader = FakeUByteReader { Ok(VECTOR_TYPE_128) }
        val context = decoderContext(reader)

        val actual = VectorTypeDecoder(context)
        assertEquals(Ok(VectorType.V128), actual)
    }

    @Test
    fun `returns an error when the encoded byte doesnt match`() {

        val reader = FakeUByteReader { Ok(0u) }
        val context = decoderContext(reader)

        val actual = VectorTypeDecoder(context)
        assertEquals(Err(TypeDecodeError.InvalidVectorType(0x00.toUByte())), actual)
    }
}
