package io.github.charlietap.chasm.decoder.decoder.type.tag

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.ast.type.exceptionAttribute
import kotlin.test.Test
import kotlin.test.assertEquals

class AttributeDecoderTest {

    @Test
    fun `can decode an encoded attribute`() {

        val reader = FakeUByteReader { Ok(0x00u) }
        val context = decoderContext(reader)

        val expected = Ok(exceptionAttribute())

        val actual = AttributeDecoder(
            context,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns unknown attribute error when non attribute byte is returned`() {

        val reader = FakeUByteReader { Ok(0xFFu) }
        val context = decoderContext(reader)

        val expected = Err(SectionDecodeError.UnknownTagAttribute(0xFFu))

        val actual = AttributeDecoder(
            context,
        )

        assertEquals(expected, actual)
    }
}
