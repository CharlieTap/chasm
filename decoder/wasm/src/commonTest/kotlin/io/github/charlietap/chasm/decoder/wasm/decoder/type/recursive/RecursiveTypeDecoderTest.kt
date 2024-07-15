package io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.subType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class RecursiveTypeDecoderTest {

    @Test
    fun `can decode an encoded recursive type with multiple subtypes`() {

        val prefixByte = MULTIPLE_SUBTYPES_RECURSIVE_TYPE

        val peekReader = FakeUByteReader {
            Ok(prefixByte)
        }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeUByteReader = { Ok(prefixByte) },
        )
        val context = decoderContext(reader)

        val subTypeDecoder: Decoder<SubType> = { _ ->
            fail("SubTypeDecoder should not be directly called in this scenario")
        }

        val subTypes: List<SubType> = emptyList()
        val vectorDecoder: VectorDecoder<SubType> = { _context, _decoder ->
            assertEquals(context, _context)
            assertEquals(subTypeDecoder, _decoder)
            Ok(Vector(subTypes))
        }

        val expected = RecursiveType(subTypes)

        val actual = RecursiveTypeDecoder(
            context = context,
            subTypeDecoder = subTypeDecoder,
            vectorDecoder = vectorDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded non prefixed recursive type`() {

        val peekReader = FakeUByteReader {
            Ok(0x00u)
        }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
        )
        val context = decoderContext(reader)

        val subType = subType()
        val subTypeDecoder: Decoder<SubType> = { _context ->
            assertEquals(context, _context)
            Ok(subType)
        }

        val vectorDecoder: VectorDecoder<SubType> = { _, _ ->
            fail("VectorDecoder should not be called in this scenario")
        }

        val expected = RecursiveType(listOf(subType))

        val actual = RecursiveTypeDecoder(
            context = context,
            subTypeDecoder = subTypeDecoder,
            vectorDecoder = vectorDecoder,
        )

        assertEquals(Ok(expected), actual)
    }
}
