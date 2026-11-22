package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.f64ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

class BoxedValuesTest {

    @Test
    fun `numbers are lowered into the word lane and references into the reference lane`() {

        val host = Any()
        val words = LongArray(6)
        val references = arrayOfNulls<Any>(6)
        val values = listOf(
            NumberValue.I32(-1),
            NumberValue.I64(117L),
            NumberValue.F32(1.5f),
            NumberValue.F64(2.5),
            ReferenceValue.Extern(host),
            ReferenceValue.Null(AbstractHeapType.Extern),
        )

        val rejection = BoxedValues().lower(values, words, references)

        assertNull(rejection)
        assertContentEquals(
            longArrayOf(-1L, 117L, 1.5f.toRawBits().toLong(), 2.5.toRawBits(), 0L, 0L),
            words,
        )
        assertSame(host, references[4])
        assertNull(references[5])
    }

    @Test
    fun `a v128 cannot be lowered across the interpreter's embedding boundary`() {

        val rejection = BoxedValues().lower(listOf(VectorValue.V128(ByteArray(16))), LongArray(1), arrayOfNulls(1))

        assertEquals("argument 0 is a v128, which cannot cross the interpreter's embedding boundary", rejection)
    }

    @Test
    fun `results are raised from the lane their type names`() {

        val host = Any()
        val types = listOf(
            i32ValueType(),
            i64ValueType(),
            f32ValueType(),
            f64ValueType(),
            referenceValueType(refNullReferenceType(AbstractHeapType.Extern)),
            referenceValueType(refNullReferenceType(AbstractHeapType.Any)),
        )
        val words = longArrayOf(-1L, 117L, 1.5f.toRawBits().toLong(), 2.5.toRawBits(), 0L, 0L)
        val references = arrayOf<Any?>(null, null, null, null, host, I31Reference(7))

        val actual = BoxedValues().raise(types, words, references)

        assertEquals(NumberValue.I32(-1), actual[0])
        assertEquals(NumberValue.I64(117L), actual[1])
        assertEquals(NumberValue.F32(1.5f), actual[2])
        assertEquals(NumberValue.F64(2.5), actual[3])
        assertSame(host, assertIs<ReferenceValue.Extern>(actual[4]).value)
        assertEquals(I31Reference(7), assertIs<ReferenceValue.I31>(actual[5]).reference)
    }

    @Test
    fun `a null result is raised as the bottom of its heap type`() {

        val types = listOf(
            referenceValueType(refNullReferenceType(AbstractHeapType.Func)),
            referenceValueType(refNullReferenceType(AbstractHeapType.Extern)),
            referenceValueType(refNullReferenceType(AbstractHeapType.Exception)),
            referenceValueType(refNullReferenceType(AbstractHeapType.Struct)),
        )

        val actual = BoxedValues().raise(types, LongArray(4), arrayOfNulls(4))

        val heapTypes = actual.map { value -> assertIs<ReferenceValue.Null>(value).heapType }
        assertEquals(
            listOf(AbstractHeapType.NoFunc, AbstractHeapType.NoExtern, AbstractHeapType.NoException, AbstractHeapType.None),
            heapTypes,
        )
    }

    @Test
    fun `an extern result holding a guest reference stays an extern`() {

        val types = listOf(referenceValueType(refNullReferenceType(AbstractHeapType.Extern)))

        val actual = BoxedValues().raise(types, LongArray(1), arrayOf(I31Reference(3)))

        assertEquals(I31Reference(3), assertIs<ReferenceValue.Extern>(actual.single()).value)
    }
}
