package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.allocateArray
import io.github.charlietap.chasm.embedding.compiled.arrayLength
import io.github.charlietap.chasm.embedding.compiled.arrayType
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.readArrayReference
import io.github.charlietap.chasm.embedding.compiled.readStructField
import io.github.charlietap.chasm.embedding.compiled.readStructReference
import io.github.charlietap.chasm.embedding.compiled.unsafeWriteStructReference
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.F32
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.embedding.compiled.writeStructField
import io.github.charlietap.chasm.embedding.compiled.writeStructReference
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class StructAccessorsTest {

    private val store = store()

    private val instance = instantiate(store)

    @Test
    fun floatFieldsKeepTheirRawBits() {

        val kinds = kinds()
        val negative = (-1.5f).toRawBits()
        val nan = 0x7FC0_0001

        writeStructField(kinds, F32_FIELD, negative.toLong())
        assertEquals(negative.toLong(), readStructField(kinds, F32_FIELD))
        assertEquals(F32(-1.5f), guestCall("read_f32", kinds))

        writeStructField(kinds, F32_FIELD, nan.toLong())
        assertEquals(nan.toLong(), readStructField(kinds, F32_FIELD))
    }

    @Test
    fun negativeIntegersReadBackSignExtended() {

        val kinds = kinds()

        writeStructField(kinds, I32_FIELD, -7L)

        assertEquals(-7L, readStructField(kinds, I32_FIELD))
    }

    @Test
    fun packedFieldsTruncateOnWriteAndZeroExtendOnRead() {

        val kinds = kinds()

        writeStructField(kinds, I8_FIELD, 0x1FFL)
        writeStructField(kinds, I16_FIELD, 0x1FFFFL)

        assertEquals(0xFFL, readStructField(kinds, I8_FIELD))
        assertEquals(0xFFFFL, readStructField(kinds, I16_FIELD))
        assertEquals(I32(-1), guestCall("read_i8_s", kinds))
        assertEquals(I32(0xFFFF), guestCall("read_i16_u", kinds))
    }

    @Test
    fun wrongKindsAreRejected() {

        val kinds = kinds()

        assertFailsWith<IllegalArgumentException> { readStructField(kinds, REFERENCE_FIELD) }
        assertFailsWith<IllegalArgumentException> { readStructReference(kinds, I32_FIELD) }
        assertFailsWith<IllegalArgumentException> { writeStructField(kinds, REFERENCE_FIELD, 1L) }
        assertFailsWith<IllegalArgumentException> { writeStructReference(kinds, I32_FIELD, "value") }
        assertFailsWith<IllegalArgumentException> { writeStructField(kinds, FIXED_FIELD, 1L) }
    }

    @Test
    fun indexesOutOfRangeAreRejected() {

        val kinds = kinds()

        assertFailsWith<IndexOutOfBoundsException> { readStructField(kinds, -1) }
        assertFailsWith<IndexOutOfBoundsException> { readStructReference(kinds, FIELD_COUNT) }
        assertFailsWith<IndexOutOfBoundsException> { writeStructField(kinds, FIELD_COUNT, 1L) }
        assertFailsWith<IndexOutOfBoundsException> { writeStructReference(kinds, -1, null) }
        assertFailsWith<IndexOutOfBoundsException> { readStructField(guestStruct("empty"), 0) }
    }

    @Test
    fun subtypesReachInheritedAndOwnFields() {

        val derived = guestStruct("derived")
        val value = Any()

        writeStructField(derived, 0, 11L)
        writeStructField(derived, 1, 2.5f.toRawBits().toLong())
        writeStructReference(derived, 2, value)

        assertEquals(11L, readStructField(derived, 0))
        assertEquals(2.5f.toRawBits().toLong(), readStructField(derived, 1))
        assertSame(value, readStructReference(derived, 2))
        assertEquals(I32(11), guestCall("read_inherited", derived))
    }

    @OptIn(UnsafeHostApi::class)
    @Test
    fun unsafeReferenceWritesOfTheWrongClassFailAtTheWrite() {

        val kinds = kinds()

        unsafeWriteStructReference(kinds, SELF_FIELD, kinds)
        assertSame(kinds, readStructReference(kinds, SELF_FIELD))

        assertFailsWith<ClassCastException> { unsafeWriteStructReference(kinds, SELF_FIELD, "not a struct") }
    }

    @Test
    fun typedReferenceArraysAllocate() {

        val type = arrayType(guestArray("boxes"))
        val kinds = kinds()

        val array = allocateArray(store, type, 3, kinds)

        assertEquals(3, arrayLength(array))
        assertSame(kinds, readArrayReference(array, 2))
    }

    private fun kinds(): StructReference {
        return guestStruct("kinds")
    }

    private fun guestStruct(name: String): StructReference {
        return (invoke(store, instance, name).expect(name).single() as ReferenceValue.Struct).reference
    }

    private fun guestArray(name: String): ArrayReference {
        return (invoke(store, instance, name).expect(name).single() as ReferenceValue.Array).reference
    }

    private fun guestCall(
        name: String,
        struct: StructReference,
    ): ExecutionValue {
        return invoke(store, instance, name, listOf(ReferenceValue.Struct(struct))).expect(name).single()
    }

    private fun instantiate(store: Store): Instance {

        val module = runBlocking { module(resourceBytes("modules/struct_accessors.wasm")) }.expect("struct_accessors")

        return runBlocking { instance(store, module) }.expect("struct_accessors")
    }

    private companion object {
        const val I32_FIELD = 0
        const val F32_FIELD = 2
        const val I8_FIELD = 4
        const val I16_FIELD = 5
        const val REFERENCE_FIELD = 6
        const val FIXED_FIELD = 7
        const val SELF_FIELD = 8
        const val FIELD_COUNT = 9
    }
}
