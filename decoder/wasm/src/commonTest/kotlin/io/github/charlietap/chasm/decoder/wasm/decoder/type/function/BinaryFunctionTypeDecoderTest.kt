package io.github.charlietap.chasm.decoder.wasm.decoder.type.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.result.ResultTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryFunctionTypeDecoderTest {

    @Test
    fun `can decode an encoded function type`() {

        val params = listOf(
            ValueType.Number(NumberType.I32),
            ValueType.Number(NumberType.I64),
            ValueType.Number(NumberType.F32),
        )

        val results = listOf(
            ValueType.Number(NumberType.F64),
            ValueType.Vector(VectorType.V128),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)),
        )

        val reader = FakeUByteReader {
            Ok(0x60.toUByte())
        }

        val resultSequence = sequenceOf(params, results).iterator()
        val resultDecoder: ResultTypeDecoder = {
            Ok(ResultType(resultSequence.next()))
        }

        val expectedParams = ResultType(params)
        val expectedResults = ResultType(results)
        val expected = Ok(FunctionType(expectedParams, expectedResults))

        val actual = BinaryFunctionTypeDecoder(reader, resultDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when the encoded function type byte doesnt match`() {

        val reader = FakeUByteReader {
            Ok(0x00.toUByte())
        }

        val actual = BinaryFunctionTypeDecoder(reader)
        assertEquals(Err(TypeDecodeError.InvalidFunctionType(0x00.toUByte())), actual)
    }

    @Test
    fun `returns an error when the encoded result type is invalid`() {

        val reader = FakeUByteReader {
            Ok(0x60.toUByte())
        }

        val expected = Err(TypeDecodeError.InvalidValueType(0x01.toUByte()))
        val resultTypeDecoder: ResultTypeDecoder = {
            expected
        }

        val actual = BinaryFunctionTypeDecoder(reader, resultTypeDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryFunctionTypeDecoder(reader)

        assertEquals(expected, actual)
    }
}
