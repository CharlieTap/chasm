package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.executor.runtime.encoder.ReferenceValueDecoder
import io.github.charlietap.chasm.executor.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReferenceValueEncoderTest {

    @Test
    fun `encode and decode ReferenceValue Null`() {
        val original = ReferenceValue.Null(AbstractHeapType.Any)
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Null with HeapTypeFunc`() {
        val original = ReferenceValue.Null(AbstractHeapType.Func)
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode ReferenceValue Null with HeapTypeExtern`() {
        val referenceValue = ReferenceValue.Null(AbstractHeapType.Extern)
        val encoded = ReferenceValueEncoder(referenceValue)

        val expected = (HeapTypeEncoder(AbstractHeapType.Extern).toLong() shl 8) or 1L

        assertEquals(expected, encoded, "Encoding of ReferenceValue.Null with HeapTypeExtern is incorrect")
    }

    @Test
    fun `decode ReferenceValue Null with HeapTypeExtern`() {
        val expectedEncoded = (HeapTypeEncoder(AbstractHeapType.Extern).toLong() shl 8) or 1L

        val decoded = ReferenceValueDecoder(expectedEncoded)
        val expectedDecoded = ReferenceValue.Null(AbstractHeapType.Extern)

        assertEquals(expectedDecoded, decoded, "Decoding of ReferenceValue.Null with HeapTypeExtern is incorrect")
    }

    @Test
    fun `encode and decode ReferenceValue Null with HeapTypeExtern`() {
        val original = ReferenceValue.Null(AbstractHeapType.Extern)
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Null with ConcreteTypeIndex`() {
        val original = ReferenceValue.Null(ConcreteHeapType.TypeIndex(117))
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue I31`() {
        val original = ReferenceValue.I31(123u)
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Struct`() {
        val original = ReferenceValue.Struct(Address.Struct(456))
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Array`() {
        val original = ReferenceValue.Array(Address.Array(789))
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Function`() {
        val original = ReferenceValue.Function(Address.Function(101112))
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Host`() {
        val original = ReferenceValue.Host(Address.Host(131415))
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Exception`() {
        val original = ReferenceValue.Exception(Address.Exception(161718))
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ReferenceValue Extern`() {
        val inner = ReferenceValue.Struct(Address.Struct(192021))
        val original = ReferenceValue.Extern(inner)
        val encoded = ReferenceValueEncoder(original)
        val decoded = ReferenceValueDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `decode with unknown type should throw InvocationException`() {
        val invalidEncoded = 0xFF0000000000000L
        assertFailsWith<InvocationException> {
            ReferenceValueDecoder(invalidEncoded)
        }
    }
}
