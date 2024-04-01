package io.github.charlietap.chasm.decoder.wasm.decoder.type.composite

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate.ArrayTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate.StructTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.function.FunctionTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.structType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryCompositeTypeDecoderTest {

    @Test
    fun `can decode an encoded composite array type`() {

        val reader = FakeUByteReader { Ok(ARRAY_COMPOSITE_TYPE) }

        val arrayType = arrayType()
        val arrayTypeDecoder: ArrayTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(arrayType)
        }

        val expected = Ok(CompositeType.Array(arrayType))

        val actual = BinaryCompositeTypeDecoder(
            reader,
            functionTypeDecoder(),
            structTypeDecoder(),
            arrayTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded composite function type`() {

        val reader = FakeUByteReader { Ok(FUNCTION_COMPOSITE_TYPE) }

        val functionType = functionType()
        val functionTypeDecoder: FunctionTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(functionType)
        }

        val expected = Ok(CompositeType.Function(functionType))

        val actual = BinaryCompositeTypeDecoder(
            reader,
            functionTypeDecoder,
            structTypeDecoder(),
            arrayTypeDecoder(),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded composite struct type`() {

        val reader = FakeUByteReader { Ok(STRUCT_COMPOSITE_TYPE) }

        val structType = structType()
        val structTypeDecoder: StructTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(structType)
        }

        val expected = Ok(CompositeType.Struct(structType))

        val actual = BinaryCompositeTypeDecoder(
            reader,
            functionTypeDecoder(),
            structTypeDecoder,
            arrayTypeDecoder(),
        )

        assertEquals(expected, actual)
    }

    companion object {
        private fun arrayTypeDecoder(): ArrayTypeDecoder = { _ ->
            fail("ArrayTypeDecoder should not be called in this scenario")
        }

        private fun functionTypeDecoder(): FunctionTypeDecoder = { _ ->
            fail("FunctionTypeDecoder should not be called in this scenario")
        }

        private fun structTypeDecoder(): StructTypeDecoder = { _ ->
            fail("StructTypeDecoder should not be called in this scenario")
        }
    }
}
