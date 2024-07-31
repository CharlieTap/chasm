package io.github.charlietap.chasm.decoder.decoder.type.recursive

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.compositeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class SubTypeDecoderTest {

    @Test
    fun `can decode an encoded open subtype`() {

        val prefixByte = OPEN_SUB_TYPE

        val peekReader = FakeUByteReader {
            Ok(prefixByte)
        }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeUByteReader = { Ok(prefixByte) },
        )
        val context = decoderContext(reader)

        val typeIndexDecoder = typeIndexDecoder()

        val typeIndices: List<Index.TypeIndex> = emptyList()
        val vectorDecoder: VectorDecoder<Index.TypeIndex> = { _context, _decoder ->
            assertEquals(context, _context)
            assertEquals(typeIndexDecoder, _decoder)
            Ok(Vector(typeIndices))
        }

        val compositeType = compositeType()
        val compositeTypeDecoder: Decoder<CompositeType> = { _context ->
            assertEquals(context, _context)
            Ok(compositeType)
        }

        val expectedHeapTypes = typeIndices.map(ConcreteHeapType::TypeIndex)
        val expected = SubType.Open(expectedHeapTypes, compositeType)

        val actual = SubTypeDecoder(
            context = context,
            typeIndexDecoder = typeIndexDecoder,
            vectorDecoder = vectorDecoder,
            compositeTypeDecoder = compositeTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded final subtype`() {

        val prefixByte = FINAL_SUB_TYPE

        val peekReader = FakeUByteReader {
            Ok(prefixByte)
        }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeUByteReader = { Ok(prefixByte) },
        )
        val context = decoderContext(reader)

        val typeIndexDecoder = typeIndexDecoder()

        val typeIndices: List<Index.TypeIndex> = emptyList()
        val vectorDecoder: VectorDecoder<Index.TypeIndex> = { _context, _decoder ->
            assertEquals(context, _context)
            assertEquals(typeIndexDecoder, _decoder)
            Ok(Vector(typeIndices))
        }

        val compositeType = compositeType()
        val compositeTypeDecoder: Decoder<CompositeType> = { _context ->
            assertEquals(context, _context)
            Ok(compositeType)
        }

        val expectedHeapTypes = typeIndices.map(ConcreteHeapType::TypeIndex)
        val expected = SubType.Final(expectedHeapTypes, compositeType)

        val actual = SubTypeDecoder(
            context = context,
            typeIndexDecoder = typeIndexDecoder,
            vectorDecoder = vectorDecoder,
            compositeTypeDecoder = compositeTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode a non prefixed encoded subtype`() {

        val peekReader = FakeUByteReader {
            Ok(0x00u)
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })
        val context = decoderContext(reader)

        val compositeType = compositeType()
        val compositeTypeDecoder: Decoder<CompositeType> = { _context ->
            assertEquals(context, _context)
            Ok(compositeType)
        }

        val expected = SubType.Final(emptyList(), compositeType)

        val actual = SubTypeDecoder(
            context = context,
            typeIndexDecoder = typeIndexDecoder(),
            vectorDecoder = vectorDecoder(),
            compositeTypeDecoder = compositeTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    companion object {
        private fun typeIndexDecoder(): Decoder<Index.TypeIndex> = { _ ->
            fail("TypeIndexDecoder should not be called in this scenario")
        }

        private fun vectorDecoder(): VectorDecoder<Index.TypeIndex> = { _, _ ->
            fail("VectorDecoder should not be called in this scenario")
        }
    }
}
