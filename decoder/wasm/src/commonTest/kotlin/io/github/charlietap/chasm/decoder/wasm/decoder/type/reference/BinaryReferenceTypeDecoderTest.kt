package io.github.charlietap.chasm.decoder.wasm.decoder.type.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryReferenceTypeDecoderTest {

    @Test
    fun `can decode an encoded reference type`() {

        val referenceTypeMap = mapOf(
            REFERENCE_TYPE_FUNC to Ok(ReferenceType.RefNull(HeapType.Func)),
            REFERENCE_TYPE_EXTERN to Ok(ReferenceType.RefNull(HeapType.Extern)),
            REFERENCE_TYPE_REF to Ok(ReferenceType.Ref(HeapType.Func)),
            REFERENCE_TYPE_REF_NULL to Ok(ReferenceType.RefNull(HeapType.Func)),
            0x11.toUByte() to Err(TypeDecodeError.InvalidReferenceType(0x11.toUByte())),
        )

        val reader = FakeWasmBinaryReader()

        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(HeapType.Func)
        }

        referenceTypeMap.forEach { (input, expected) ->
            val actual = BinaryReferenceTypeDecoder(reader, input, heapTypeDecoder)
            assertEquals(expected, actual)
        }
    }
}
