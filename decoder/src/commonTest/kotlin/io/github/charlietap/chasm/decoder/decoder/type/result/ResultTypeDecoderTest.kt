package io.github.charlietap.chasm.decoder.decoder.type.result

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTypeDecoderTest {

    @Test
    fun `can decode an encoded result type`() {

        val context = decoderContext()

        val subDecoder: Decoder<ValueType> = {
            Ok(i32ValueType())
        }

        val valueTypes = listOf(
            i32ValueType(),
            i64ValueType(),
        )
        val vectorDecoder: VectorDecoder<ValueType> = { _, _ ->
            Ok(Vector(valueTypes))
        }

        val expected = Ok(resultType(valueTypes))

        val actual = ResultTypeDecoder(context, vectorDecoder, subDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = ResultTypeDecoder(context)

        assertEquals(expected, actual)
    }
}
