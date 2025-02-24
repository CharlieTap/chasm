package io.github.charlietap.chasm.decoder.decoder.type.heap

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class HeapTypeDecoderTest {

    @Test
    fun `can decode an encoded abstract heap type`() {

        val abstractHeapTypes = ABSTRACT_HEAP_TYPE_RANGE.map(UInt::toUByte)

        val abstractHeapTypeDecoder: Decoder<AbstractHeapType> = { _ ->
            Ok(AbstractHeapType.Extern)
        }

        abstractHeapTypes.forEach { abstractHeapTypeByte ->

            val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
                Ok(abstractHeapTypeByte)
            }
            val peekReader = FakeWasmBinaryReader(
                fakeUByteReader = fakeUByteReader,
            )

            val reader = FakeWasmBinaryReader(
                fakeUByteReader = fakeUByteReader,
                fakePeekReader = { peekReader },
            )
            val context = decoderContext(reader)

            val actual = HeapTypeDecoder(context, abstractHeapTypeDecoder)
            assertEquals(Ok(AbstractHeapType.Extern), actual)
        }
    }

    @Test
    fun `can decode an encoded concrete heap type`() {

        val concreteHeapTypes = setOf(
            129u,
        ).map(UInt::toUByte)

        val abstractHeapTypeDecoder: Decoder<AbstractHeapType> = { _ ->
            fail("AbstractHeapTypeDecoder should not be called in this scenario")
        }

        concreteHeapTypes.forEach { concreteHeapTypeByte ->

            val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
                Ok(concreteHeapTypeByte)
            }

            val expectedTypeIndex = 117u

            val peekReader = FakeWasmBinaryReader(
                fakeUByteReader = fakeUByteReader,
            )
            val reader = FakeWasmBinaryReader(
                fakeUByteReader = fakeUByteReader,
                fakeS33Reader = { Ok(expectedTypeIndex) },
                fakePeekReader = { peekReader },
            )
            val context = decoderContext(reader)

            val expected = ConcreteHeapType.TypeIndex(expectedTypeIndex.toInt())

            val actual = HeapTypeDecoder(context, abstractHeapTypeDecoder)
            assertEquals(Ok(expected), actual)
        }
    }
}
