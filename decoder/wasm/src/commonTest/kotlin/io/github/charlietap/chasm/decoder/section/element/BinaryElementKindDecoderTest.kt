package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeByteReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryElementKindDecoderTest {

    @Test
    fun `can decode an element kind`() {

        val reader = FakeByteReader {
            Ok(ElementKind.FuncRef.byte)
        }

        val expected = Ok(ElementKind.FuncRef)

        val actual = BinaryElementKindDecoder(
            reader = reader,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `return unknown element kind error when byte isn't known`() {

        val unknown = Byte.MAX_VALUE

        val reader = FakeByteReader {
            Ok(unknown)
        }

        val expected = Err(SectionDecodeError.UnknownElementKind(unknown))

        val actual = BinaryElementKindDecoder(
            reader = reader,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryElementKindDecoder(reader)

        assertEquals(expected, actual)
    }
}
