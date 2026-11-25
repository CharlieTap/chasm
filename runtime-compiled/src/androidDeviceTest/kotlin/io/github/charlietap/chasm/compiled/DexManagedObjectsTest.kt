package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.reference.allocateStruct
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.reference.runtimeType
import io.github.charlietap.chasm.embedding.reference.writeStructField
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.F64
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class DexManagedObjectsTest {

    private val module = dexModule("managed_objects")
    private val store = store(module)
    private val instance = runBlocking { instance(store, module) }.expect("instantiates")

    @Test
    fun aStructReturnedByAnExportIsTheObjectALaterCallReceives() {

        val point = point(1, 2)

        call("keep", point)
        val kept = assertIs<ReferenceValue.Struct>(call("kept").single())

        assertSame(point.reference, kept.reference)
    }

    @Test
    fun refEqSeesOneObjectAfterAHostRoundTrip() {

        val point = point(1, 2)
        val other = point(1, 2)

        assertEquals(listOf(I32(1)), call("same", point, point))
        assertEquals(listOf(I32(0)), call("same", point, other))
    }

    @Test
    fun aStructFieldWrittenByTheHostIsReadByTheGuest() {

        val point = point(1, 2)

        writeStructField(point.reference, 0, 42L)

        assertEquals(listOf(I32(42)), call("x", point))
    }

    @Test
    fun anExternrefIsTheHostsOwnObject() {

        val value = Any()

        val echoed = assertIs<ReferenceValue.Extern>(call("echo", ReferenceValue.Extern(value)).single())

        assertSame(value, echoed.value)
    }

    @Test
    fun aHostAllocatedStructWithSpilledPackedAndWideFieldsIsReadByTheGuest() {

        val fields = LongArray(WIDE_FIELDS)
        fields[LONG_FIELD] = LONG_BITS
        fields[BYTE_FIELD] = 0x1FFL
        fields[DOUBLE_FIELD] = 2.5.toRawBits()

        val type = runtimeType(instance, WIDE_TYPE)
        val wide = ReferenceValue.Struct(allocateStruct(store, type, fields, arrayOfNulls(WIDE_FIELDS)))

        assertEquals(listOf(I64(LONG_BITS)), call("wide_long", wide))
        assertEquals(listOf(I32(-1)), call("wide_byte_signed", wide))
        assertEquals(listOf(I32(0xFF)), call("wide_byte_unsigned", wide))
        assertEquals(listOf(F64(2.5)), call("wide_double", wide))
    }

    @Test
    fun aGuestAllocatedStructWithSpilledPackedAndWideFieldsIsReadAndWrittenByTheHost() {

        val wide = assertIs<ReferenceValue.Struct>(call("make_wide", I64(LONG_BITS), I32(0x1AB), F64(1.5)).single())

        assertEquals(LONG_BITS, readStructField(wide.reference, LONG_FIELD))
        assertEquals(0xABL, readStructField(wide.reference, BYTE_FIELD))
        assertEquals(1.5.toRawBits(), readStructField(wide.reference, DOUBLE_FIELD))

        writeStructField(wide.reference, LONG_FIELD, OTHER_LONG_BITS)
        writeStructField(wide.reference, DOUBLE_FIELD, 3.25.toRawBits())

        assertEquals(listOf(I64(OTHER_LONG_BITS)), call("wide_long", wide))
        assertEquals(listOf(F64(3.25)), call("wide_double", wide))
    }

    @Test
    fun defaultAllocationCreatesSpillArraysOfExactLength() {

        val wide = assertIs<ReferenceValue.Struct>(call("make_default_wide").single()).reference
        val spilledInts = wide.javaClass.getField(SPILLED_INTS).get(wide) as IntArray
        val spilledReferences = wide.javaClass.getField(SPILLED_REFERENCES).get(wide) as Array<*>

        assertEquals(SPILLED_INT_LANES, spilledInts.size)
        assertEquals(1, spilledReferences.size)
        assertEquals(0L, readStructField(wide, LONG_FIELD))
    }

    private fun call(
        name: String,
        vararg arguments: ExecutionValue,
    ): List<ExecutionValue> {
        return invoke(instance, name, arguments.toList()).expect(name)
    }

    private fun point(
        x: Int,
        y: Int,
    ): ReferenceValue.Struct {
        return assertIs<ReferenceValue.Struct>(call("make_point", I32(x), I32(y)).single())
    }

    private companion object {
        const val WIDE_TYPE = 1
        const val WIDE_FIELDS = 26
        const val LONG_FIELD = 22
        const val BYTE_FIELD = 23
        const val DOUBLE_FIELD = 24
        const val LONG_BITS = 0x1122_3344_5566_7788L
        const val OTHER_LONG_BITS = -0x0102_0304_0506_0708L
        const val SPILLED_INT_LANES = 4
        const val SPILLED_INTS = "spilledInts"
        const val SPILLED_REFERENCES = "spilledReferences"
    }
}
