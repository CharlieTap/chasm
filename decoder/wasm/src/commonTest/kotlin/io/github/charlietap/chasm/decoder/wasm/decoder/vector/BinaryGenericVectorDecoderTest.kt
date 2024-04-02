package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryGenericVectorDecoderTest {

    @Test
    fun `can decode an encoded wasm vector`() {

        val expectedNames = listOf(
            NameValue("one"),
            NameValue("two"),
        )
        val expected = Ok(Vector(expectedNames))

        val reader = FakeUIntReader { Ok(2u) }

        val names = sequenceOf(NameValue("one"), NameValue("two")).iterator()
        val subDecoder: NameValueDecoder = {
            Ok(names.next())
        }

        val actual = BinaryVectorDecoder(reader, subDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val subDecoder: NameValueDecoder = {
            Ok(NameValue(""))
        }

        val actual = BinaryVectorDecoder(reader, subDecoder)

        assertEquals(expected, actual)
    }
}
