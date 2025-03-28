package io.github.charlietap.chasm.decoder.decoder.type.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.f64ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeDecoderTest {

    @Test
    fun `can decode an encoded function type`() {

        val context = decoderContext()

        val params = listOf(
            i32ValueType(),
            i64ValueType(),
            f32ValueType(),
        )

        val results = listOf(
            f64ValueType(),
            ValueType.Vector(VectorType.V128),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)),
        )

        val resultSequence = sequenceOf(params, results).iterator()
        val resultDecoder: Decoder<ResultType> = {
            Ok(resultType(resultSequence.next()))
        }

        val expectedParams = resultType(params)
        val expectedResults = resultType(results)
        val expected = Ok(FunctionType(expectedParams, expectedResults))

        val actual = FunctionTypeDecoder(context, resultDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = FunctionTypeDecoder(context)

        assertEquals(expected, actual)
    }
}
