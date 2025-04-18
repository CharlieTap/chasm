package io.github.charlietap.chasm.decoder.decoder.type.recursive

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.subType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
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

        val expected = recursiveType(
            subTypes = subTypes,
            state = RecursiveType.State.SYNTAX,
        )

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

        val expected = recursiveType(
            subTypes = listOf(subType),
            state = RecursiveType.State.SYNTAX,
        )

        val actual = RecursiveTypeDecoder(
            context = context,
            subTypeDecoder = subTypeDecoder,
            vectorDecoder = vectorDecoder,
        )

        assertEquals(Ok(expected), actual)
    }
}
