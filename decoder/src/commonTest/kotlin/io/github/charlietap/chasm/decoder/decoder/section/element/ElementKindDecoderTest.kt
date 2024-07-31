package io.github.charlietap.chasm.decoder.decoder.section.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeByteReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementKindDecoderTest {

    @Test
    fun `can decode an element kind`() {

        val reader = FakeByteReader {
            Ok(ElementKind.FuncRef.byte)
        }
        val context = decoderContext(reader)

        val expected = Ok(ElementKind.FuncRef)

        val actual = ElementKindDecoder(
            context = context,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `return unknown element kind error when byte isn't known`() {

        val unknown = Byte.MAX_VALUE

        val reader = FakeByteReader {
            Ok(unknown)
        }
        val context = decoderContext(reader)

        val expected = Err(SectionDecodeError.UnknownElementKind(unknown))

        val actual = ElementKindDecoder(
            context = context,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = ElementKindDecoder(context)

        assertEquals(expected, actual)
    }
}
