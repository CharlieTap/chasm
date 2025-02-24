package io.github.charlietap.chasm.decoder.decoder.type.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.ABSTRACT_HEAP_TYPE_RANGE
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ReferenceTypeDecoderTest {

    @Test
    fun `can decode an encoded non nullable reference type`() {

        val byte: UByte = REFERENCE_TYPE_REF

        val peekReader = FakeUByteReader { Ok(byte) }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeUByteReader = { Ok(0u) },
        )
        val context = decoderContext(reader)

        val heapTypeDecoder: Decoder<HeapType> = { _context ->
            assertEquals(context, _context)
            Ok(AbstractHeapType.Func)
        }

        val abstractHeapTypeDecoder: Decoder<AbstractHeapType> = { _ ->
            fail("AbstractHeapTypeDecoder should not be called in this scenario")
        }

        val expected = Ok(ReferenceType.Ref(AbstractHeapType.Func))

        val actual = ReferenceTypeDecoder(context, heapTypeDecoder, abstractHeapTypeDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded nullable reference type`() {

        val byte: UByte = REFERENCE_TYPE_REF_NULL

        val peekReader = FakeUByteReader { Ok(byte) }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeUByteReader = { Ok(0u) },
        )
        val context = decoderContext(reader)

        val heapTypeDecoder: Decoder<HeapType> = { _context ->
            assertEquals(context, _context)
            Ok(AbstractHeapType.Func)
        }

        val abstractHeapTypeDecoder: Decoder<AbstractHeapType> = { _ ->
            fail("AbstractHeapTypeDecoder should not be called in this scenario")
        }

        val expected = Ok(ReferenceType.RefNull(AbstractHeapType.Func))

        val actual = ReferenceTypeDecoder(context, heapTypeDecoder, abstractHeapTypeDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded abstract heap type directly as a reference type`() {

        val abstractHeapTypeMap = ABSTRACT_HEAP_TYPE_RANGE
            .map(UInt::toUByte)
            .associateWith { Ok(ReferenceType.RefNull(AbstractHeapType.Func)) }

        var byte: UByte = 0u
        val peekReader = FakeUByteReader { Ok(byte) }
        val reader = FakeWasmBinaryReader(
            fakePeekReader = { peekReader },
            fakeUByteReader = { Ok(0u) },
        )
        val context = decoderContext(reader)

        val heapTypeDecoder: Decoder<HeapType> = { _ ->
            fail("HeapTypeDecoder should not be called in this scenario")
        }

        val abstractHeapTypeDecoder: Decoder<AbstractHeapType> = { _ ->
            Ok(AbstractHeapType.Func)
        }

        abstractHeapTypeMap.forEach { (abstractHeapTypeByte, expected) ->

            byte = abstractHeapTypeByte

            val actual = ReferenceTypeDecoder(context, heapTypeDecoder, abstractHeapTypeDecoder)

            assertEquals(expected, actual)
        }
    }
}
