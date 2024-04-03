package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryHeapTypeDecoderTest {

    @Test
    fun `can decode an encoded abstract heap type`() {

        val abstractHeapTypes = ABSTRACT_HEAP_TYPE_RANGE.map(UInt::toUByte)

        val abstractHeapTypeDecoder: AbstractHeapTypeDecoder = { _ ->
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

            val actual = BinaryHeapTypeDecoder(reader, abstractHeapTypeDecoder)
            assertEquals(Ok(AbstractHeapType.Extern), actual)
        }
    }

    @Test
    fun `can decode an encoded concrete heap type`() {

        val concreteHeapTypes = setOf(
            129u,
        ).map(UInt::toUByte)

        val abstractHeapTypeDecoder: AbstractHeapTypeDecoder = { _ ->
            fail("AbstractHeapTypeDecoder should not be called in this scenario")
        }

        concreteHeapTypes.forEach { concreteHeapTypeByte ->

            val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
                Ok(concreteHeapTypeByte)
            }

            val expectedTypeIndex = Index.TypeIndex(117u)

            val peekReader = FakeWasmBinaryReader(
                fakeUByteReader = fakeUByteReader,
            )
            val reader = FakeWasmBinaryReader(
                fakeUByteReader = fakeUByteReader,
                fakeS33Reader = { Ok(expectedTypeIndex.idx) },
                fakePeekReader = { peekReader },
            )

            val expected = ConcreteHeapType.TypeIndex(expectedTypeIndex)

            val actual = BinaryHeapTypeDecoder(reader, abstractHeapTypeDecoder)
            assertEquals(Ok(expected), actual)
        }
    }
}
