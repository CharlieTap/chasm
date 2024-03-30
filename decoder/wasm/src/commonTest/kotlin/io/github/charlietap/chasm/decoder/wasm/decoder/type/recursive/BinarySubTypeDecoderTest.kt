package io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.composite.CompositeTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.compositeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinarySubTypeDecoderTest {

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

        val typeIndexDecoder = typeIndexDecoder()

        val typeIndices: List<Index.TypeIndex> = emptyList()
        val vectorDecoder: VectorDecoder<Index.TypeIndex> = { _reader, _subdecoder ->
            assertEquals(reader, _reader)
            assertEquals(typeIndexDecoder, _subdecoder)
            Ok(Vector(typeIndices))
        }

        val compositeType = compositeType()
        val compositeTypeDecoder: CompositeTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(compositeType)
        }

        val expected = SubType.Open(typeIndices, compositeType)

        val actual = BinarySubTypeDecoder(
            reader = reader,
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

        val typeIndexDecoder = typeIndexDecoder()

        val typeIndices: List<Index.TypeIndex> = emptyList()
        val vectorDecoder: VectorDecoder<Index.TypeIndex> = { _reader, _subdecoder ->
            assertEquals(reader, _reader)
            assertEquals(typeIndexDecoder, _subdecoder)
            Ok(Vector(typeIndices))
        }

        val compositeType = compositeType()
        val compositeTypeDecoder: CompositeTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(compositeType)
        }

        val expected = SubType.Final(typeIndices, compositeType)

        val actual = BinarySubTypeDecoder(
            reader = reader,
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

        val compositeType = compositeType()
        val compositeTypeDecoder: CompositeTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(compositeType)
        }

        val expected = SubType.Final(emptyList(), compositeType)

        val actual = BinarySubTypeDecoder(
            reader = reader,
            typeIndexDecoder = typeIndexDecoder(),
            vectorDecoder = vectorDecoder(),
            compositeTypeDecoder = compositeTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    companion object {
        private fun typeIndexDecoder(): TypeIndexDecoder = { _ ->
            fail("TypeIndexDecoder should not be called in this scenario")
        }

        private fun vectorDecoder(): VectorDecoder<Index.TypeIndex> = { _, _ ->
            fail("VectorDecoder should not be called in this scenario")
        }
    }
}
