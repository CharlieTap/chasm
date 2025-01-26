package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.BottomType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.encoder.HeapTypeDecoder
import io.github.charlietap.chasm.executor.runtime.encoder.HeapTypeEncoder
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
        val original = ConcreteHeapType.TypeIndex(Index.TypeIndex(117u))
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
