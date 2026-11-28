package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.embedding.reference.readStructFields
import io.github.charlietap.chasm.runtime.compiled.gc.storage.Struct_I23_R18__I_II_II_IRx18_Spill
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StructStorageLayoutTest {

    private val layout = StructStorageLayout(FIELDS, bytes = 0) { rtt -> Struct_I23_R18__I_II_II_IRx18_Spill(rtt) }
    private val rtt = Rtt(0, "layout", fakeEngine(), null, layout)

    @Test
    fun `reading a range of fields returns what reading each field returns`() {

        val struct = populatedStruct()
        val buffer = LongArray(NUMBER_FIELDS.size)

        readStructFields(struct, 0, buffer, count = REFERENCE_FIELD)
        readStructFields(struct, REFERENCE_FIELD + 1, buffer, bufferOffset = REFERENCE_FIELD)

        val perField = LongArray(NUMBER_FIELDS.size) { position -> layout.readBits(struct, NUMBER_FIELDS[position]) }
        val expected = NUMBER_FIELDS.map { index -> VALUES[index] and MASKS[index] }.toLongArray()

        assertContentEquals(perField, buffer)
        assertContentEquals(expected, buffer)
    }

    @Test
    fun `reading a single field into a buffer returns what reading the field returns`() {

        val struct = populatedStruct()

        NUMBER_FIELDS.forEach { index ->
            val buffer = LongArray(3)
            readStructFields(struct, index, buffer, bufferOffset = 1, count = 1)
            assertContentEquals(longArrayOf(0L, layout.readBits(struct, index), 0L), buffer, "field $index")
        }
    }

    @Test
    fun `reading a range of fields that holds a reference throws illegal argument`() {

        val struct = populatedStruct()

        assertFailsWith<IllegalArgumentException> { readStructFields(struct, 0, LongArray(FIELDS.size)) }
        assertFailsWith<IllegalArgumentException> { readStructFields(struct, REFERENCE_FIELD, LongArray(2)) }
    }

    @Test
    fun `reading a range of fields leaves the rest of the buffer untouched`() {

        val struct = populatedStruct()
        val buffer = LongArray(REFERENCE_FIELD + 2) { UNTOUCHED }

        readStructFields(struct, 0, buffer, bufferOffset = 1, count = REFERENCE_FIELD)

        assertEquals(UNTOUCHED, buffer.first())
        assertEquals(UNTOUCHED, buffer.last())
        val expected = LongArray(REFERENCE_FIELD) { index -> VALUES[index] and MASKS[index] }
        assertContentEquals(expected, buffer.copyOfRange(1, REFERENCE_FIELD + 1))
    }

    @Test
    fun `reading a range that starts past the first field writes only its own cells`() {

        val struct = populatedStruct()
        val buffer = LongArray(8) { STALE }

        readStructFields(struct, 2, buffer, bufferOffset = 3, count = 2)

        val expected = LongArray(8) { STALE }
        expected[3] = VALUES[2] and MASKS[2]
        expected[4] = VALUES[3]
        assertContentEquals(expected, buffer)
    }

    @Test
    fun `reading a range joins both halves of a 64 bit field over stale buffer contents`() {

        val struct = populatedStruct()
        val buffer = LongArray(2) { STALE }

        readStructFields(struct, I64_FIELD, buffer, count = 2)

        assertEquals(0x1234_5678_9ABC_DEF0L, buffer[0])
    }

    @Test
    fun `reading a range sign extends a 32 bit field`() {

        val struct = populatedStruct()
        val buffer = LongArray(2)

        readStructFields(struct, 0, buffer, count = 2)

        assertEquals(-5L, buffer[0])
    }

    @Test
    fun `reading a range returns a spilled field and a field with one spilled half`() {

        val struct = populatedStruct()
        val buffer = LongArray(3) { STALE }

        readStructFields(struct, SPILLED_FIELD, buffer, count = 3)

        assertContentEquals(longArrayOf(VALUES[SPILLED_FIELD], VALUES[SPILLED_FIELD + 1], VALUES[SPILLED_FIELD + 2]), buffer)
    }

    private fun populatedStruct(): CompiledStruct {

        val struct = layout.newStruct(rtt) as Struct_I23_R18__I_II_II_IRx18_Spill
        struct.spilledInts = IntArray(SPILLED_INTS)
        struct.spilledReferences = arrayOfNulls(0)
        NUMBER_FIELDS.forEach { index -> layout.writeBits(struct, index, VALUES[index]) }
        layout.writeReference(struct, REFERENCE_FIELD, Any())

        return struct
    }

    private companion object {
        const val REFERENCE_FIELD = 6
        const val I64_FIELD = 3
        const val SPILLED_FIELD = 9
        const val SPILLED_INTS = 4
        const val UNTOUCHED = -7L
        const val STALE = -1L

        val FIELDS = listOf(
            field(StorageType.Value(ValueType.Number(NumberType.I32)), lane(0)),
            field(StorageType.Packed(PackedType.I8), lane(1)),
            field(StorageType.Packed(PackedType.I16), lane(2)),
            field(StorageType.Value(ValueType.Number(NumberType.I64)), lane(3), lane(4)),
            field(StorageType.Value(ValueType.Number(NumberType.F32)), lane(5)),
            field(StorageType.Value(ValueType.Number(NumberType.F64)), lane(6), lane(7)),
            field(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))), lane(0)),
            field(StorageType.Value(ValueType.Number(NumberType.I32)), lane(8)),
            field(StorageType.Value(ValueType.Number(NumberType.I32)), lane(22)),
            field(StorageType.Value(ValueType.Number(NumberType.I32)), spilled(0)),
            field(StorageType.Value(ValueType.Number(NumberType.I64)), spilled(1), spilled(2)),
            field(StorageType.Value(ValueType.Number(NumberType.I64)), lane(21), spilled(3)),
        )

        val NUMBER_FIELDS = FIELDS.indices.filter { index -> index != REFERENCE_FIELD }

        val VALUES = longArrayOf(
            -5L,
            0x1F3L,
            0x1_8001L,
            0x1234_5678_9ABC_DEF0L,
            0x4049_0FDBL,
            3.25.toRawBits(),
            0L,
            Int.MAX_VALUE.toLong(),
            Int.MIN_VALUE.toLong(),
            42L,
            -2L,
            0x7FFF_FFFF_0000_0001L,
        )

        val MASKS = longArrayOf(-1L, 0xFFL, 0xFFFFL, -1L, -1L, -1L, 0L, -1L, -1L, -1L, -1L, -1L)

        fun lane(index: Int): StorageLane {
            return StorageLane(index, false)
        }

        fun spilled(index: Int): StorageLane {
            return StorageLane(index, true)
        }

        fun field(
            storage: StorageType,
            vararg lanes: StorageLane,
        ): StorageField {
            return StorageField(FieldType(storage, Mutability.Var), lanes.toList())
        }
    }
}
