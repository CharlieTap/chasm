package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
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
        val context = decoderContext(reader)

        val names = sequenceOf(NameValue("one"), NameValue("two")).iterator()
        val subDecoder: Decoder<NameValue> = {
            Ok(names.next())
        }

        val actual = VectorDecoder(context, subDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val subDecoder: Decoder<NameValue> = {
            Ok(NameValue(""))
        }

        val actual = VectorDecoder(context, subDecoder)

        assertEquals(expected, actual)
    }
}
