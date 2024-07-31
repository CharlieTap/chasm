package io.github.charlietap.chasm.decoder.decoder.type.heap

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractHeapTypeDecoderTest {

    @Test
    fun `can decode an encoded abstract heap type`() {

        val abstractHeapTypes = mapOf(
            HEAP_TYPE_ARRAY to AbstractHeapType.Array,
            HEAP_TYPE_STRUCT to AbstractHeapType.Struct,
            HEAP_TYPE_I31 to AbstractHeapType.I31,
            HEAP_TYPE_EQ to AbstractHeapType.Eq,
            HEAP_TYPE_ANY to AbstractHeapType.Any,
            HEAP_TYPE_EXTERN to AbstractHeapType.Extern,
            HEAP_TYPE_FUNC to AbstractHeapType.Func,
            HEAP_TYPE_NONE to AbstractHeapType.None,
            HEAP_TYPE_NO_EXTERN to AbstractHeapType.NoExtern,
            HEAP_TYPE_NO_FUNC to AbstractHeapType.NoFunc,
        )

        var byte: UByte = 0u
        val reader = FakeUByteReader { Ok(byte) }
        val context = decoderContext(reader)

        abstractHeapTypes.forEach { (abstractHeapTypeByte, expectedAbstractHeapType) ->

            byte = abstractHeapTypeByte

            val actual = AbstractHeapTypeDecoder(context)
            assertEquals(Ok(expectedAbstractHeapType), actual)
        }
    }

    @Test
    fun `throws an InvalidHeapType error when byte is unrecognised`() {

        val byte: UByte = 0x00u
        val reader = FakeUByteReader { Ok(byte) }
        val context = decoderContext(reader)

        val actual = AbstractHeapTypeDecoder(context)
        assertEquals(Err(TypeDecodeError.InvalidHeapType(byte)), actual)
    }
}
