package io.github.charlietap.chasm.decoder.wasm.decoder.type.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryGlobalTypeDecoderTest {

    @Test
    fun `can decode an encoded global type with const mutability`() {

        val valueType = ValueType.Number(NumberType.I32)
        val valueTypeDecoder: ValueTypeDecoder = {
            Ok(valueType)
        }

        val reader = FakeUByteReader {
            Ok(0u.toUByte())
        }

        val expected = Ok(GlobalType(valueType, GlobalType.Mutability.Const))

        val actual = BinaryGlobalTypeDecoder(
            reader,
            valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded global type with var mutability`() {

        val valueType = ValueType.Number(NumberType.I32)
        val valueTypeDecoder: ValueTypeDecoder = {
            Ok(valueType)
        }

        val reader = FakeUByteReader {
            Ok(1u.toUByte())
        }

        val expected = Ok(GlobalType(valueType, GlobalType.Mutability.Var))

        val actual = BinaryGlobalTypeDecoder(
            reader,
            valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryGlobalTypeDecoder(reader)

        assertEquals(expected, actual)
    }
}
