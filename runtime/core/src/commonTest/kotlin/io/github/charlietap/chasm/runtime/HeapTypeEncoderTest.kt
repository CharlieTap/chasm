package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.runtime.encoder.HeapTypeDecoder
import io.github.charlietap.chasm.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.BottomType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HeapTypeEncoderTest {

    @Test
    fun `encode and decode AbstractHeapType Func`() {
        val original = AbstractHeapType.Func
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType NoFunc`() {
        val original = AbstractHeapType.NoFunc
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Extern`() {
        val original = AbstractHeapType.Extern
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType NoExtern`() {
        val original = AbstractHeapType.NoExtern
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Exception`() {
        val original = AbstractHeapType.Exception
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType NoException`() {
        val original = AbstractHeapType.NoException
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Any`() {
        val original = AbstractHeapType.Any
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Eq`() {
        val original = AbstractHeapType.Eq
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Struct`() {
        val original = AbstractHeapType.Struct
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Array`() {
        val original = AbstractHeapType.Array
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType I31`() {
        val original = AbstractHeapType.I31
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType None`() {
        val original = AbstractHeapType.None
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode AbstractHeapType Bottom`() {
        val original = AbstractHeapType.Bottom(BottomType)
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode and decode ConcreteHeapType TypeIndex`() {
        val original = ConcreteHeapType.TypeIndex(117)
        val encoded = HeapTypeEncoder(original)
        val decoded = HeapTypeDecoder(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode ConcreteHeapType should throw IllegalArgumentException`() {
        val concreteType: HeapType = ConcreteHeapType.RecursiveTypeIndex(0)
        assertFailsWith<IllegalArgumentException> {
            HeapTypeEncoder(concreteType)
        }
    }
}
