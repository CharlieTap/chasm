package io.github.charlietap.chasm.decoder.wasm.decoder.type.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.ABSTRACT_HEAP_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.AbstractHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryReferenceTypeDecoderTest {

    @Test
    fun `can decode an encoded reference type`() {

        val referenceTypeMap = mapOf(
            REFERENCE_TYPE_REF to Ok(ReferenceType.Ref(AbstractHeapType.Func)),
            REFERENCE_TYPE_REF_NULL to Ok(ReferenceType.RefNull(AbstractHeapType.Func)),
        )

        val abstractHeapTypeMap = ABSTRACT_HEAP_TYPE_RANGE.map(UInt::toUByte)
            .associateWith { Ok(ReferenceType.RefNull(AbstractHeapType.Func)) }

        val referenceAndHeapTypeMap = referenceTypeMap + abstractHeapTypeMap

        val reader = FakeWasmBinaryReader()

        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(AbstractHeapType.Func)
        }

        val abstractHeapTypeDecoder: AbstractHeapTypeDecoder = { _ ->
            Ok(AbstractHeapType.Func)
        }

        referenceAndHeapTypeMap.forEach { (input, expected) ->
            val actual = BinaryReferenceTypeDecoder(reader, input, heapTypeDecoder, abstractHeapTypeDecoder)
            assertEquals(expected, actual)
        }
    }
}
