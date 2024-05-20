package io.github.charlietap.chasm.decoder.wasm.decoder.type.result

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryResultTypeDecoderTest {

    @Test
    fun `can decode an encoded result type`() {

        val subDecoder: ValueTypeDecoder = {
            Ok(i32ValueType())
        }

        val valueTypes = listOf(
            i32ValueType(),
            i64ValueType(),
        )
        val vectorDecoder: VectorDecoder<ValueType> = { _, sub ->
            assertEquals(subDecoder, sub)
            Ok(Vector(valueTypes))
        }

        val expected = Ok(resultType(valueTypes))

        val actual = BinaryResultTypeDecoder(FakeWasmBinaryReader(), vectorDecoder, subDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryResultTypeDecoder(reader)

        assertEquals(expected, actual)
    }
}
