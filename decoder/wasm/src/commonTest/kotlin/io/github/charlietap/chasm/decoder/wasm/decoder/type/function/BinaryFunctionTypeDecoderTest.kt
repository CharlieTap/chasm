package io.github.charlietap.chasm.decoder.wasm.decoder.type.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.result.ResultTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.resultType
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

        val resultSequence = sequenceOf(params, results).iterator()
        val resultDecoder: ResultTypeDecoder = {
            Ok(resultType(resultSequence.next()))
        }

        val expectedParams = resultType(params)
        val expectedResults = resultType(results)
        val expected = Ok(FunctionType(expectedParams, expectedResults))

        val actual = BinaryFunctionTypeDecoder(FakeWasmBinaryReader(), resultDecoder)
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
