package io.github.charlietap.chasm.decoder.decoder.type.composite

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.structType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CompositeTypeDecoderTest {

    @Test
    fun `can decode an encoded composite array type`() {

        val reader = FakeUByteReader { Ok(ARRAY_COMPOSITE_TYPE) }
        val context = decoderContext(reader)

        val arrayType = arrayType()
        val arrayTypeDecoder: Decoder<ArrayType> = { _context ->
            assertEquals(context, _context)
            Ok(arrayType)
        }

        val expected = Ok(CompositeType.Array(arrayType))

        val actual = CompositeTypeDecoder(
            context,
            functionTypeDecoder(),
            structTypeDecoder(),
            arrayTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded composite function type`() {

        val reader = FakeUByteReader { Ok(FUNCTION_COMPOSITE_TYPE) }
        val context = decoderContext(reader)

        val functionType = functionType()
        val functionTypeDecoder: Decoder<FunctionType> = { _context ->
            assertEquals(context, _context)
            Ok(functionType)
        }

        val expected = Ok(CompositeType.Function(functionType))

        val actual = CompositeTypeDecoder(
            context,
            functionTypeDecoder,
            structTypeDecoder(),
            arrayTypeDecoder(),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded composite struct type`() {

        val reader = FakeUByteReader { Ok(STRUCT_COMPOSITE_TYPE) }
        val context = decoderContext(reader)

        val structType = structType()
        val structTypeDecoder: Decoder<StructType> = { _context ->
            assertEquals(context, _context)
            Ok(structType)
        }

        val expected = Ok(CompositeType.Struct(structType))

        val actual = CompositeTypeDecoder(
            context,
            functionTypeDecoder(),
            structTypeDecoder,
            arrayTypeDecoder(),
        )

        assertEquals(expected, actual)
    }

    companion object {
        private fun arrayTypeDecoder(): Decoder<ArrayType> = { _ ->
            fail("ArrayTypeDecoder should not be called in this scenario")
        }

        private fun functionTypeDecoder(): Decoder<FunctionType> = { _ ->
            fail("FunctionTypeDecoder should not be called in this scenario")
        }

        private fun structTypeDecoder(): Decoder<StructType> = { _ ->
            fail("StructTypeDecoder should not be called in this scenario")
        }
    }
}
