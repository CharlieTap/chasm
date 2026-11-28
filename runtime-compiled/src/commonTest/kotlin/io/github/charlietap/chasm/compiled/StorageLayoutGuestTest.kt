package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.CompiledFixture
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.NumberValue.F64
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.storage.layout.InlineSlot
import io.github.charlietap.chasm.storage.layout.NumberSlot
import io.github.charlietap.chasm.storage.layout.ReferenceSlot
import io.github.charlietap.chasm.storage.layout.Slot
import io.github.charlietap.chasm.storage.layout.SpilledSlot
import io.github.charlietap.chasm.storage.layout.StorageLayouts
import io.github.charlietap.chasm.storage.layout.StructLayout
import io.github.charlietap.chasm.storage.layout.WideNumberSlots
import io.github.charlietap.chasm.storage.layout.storageLayouts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StorageLayoutGuestTest {

    @Test
    fun `a guest struct is an instance of the class its layout names`() {

        val guestBytes = fixture("storage_layout_guest")
        val guest = CompiledFixture(guestBytes)
        val header = assertIs<ReferenceValue.Struct>(guest.call("make_header", I32(HEADER_TAG)).single())
        val child = assertIs<ReferenceValue.Struct>(guest.call("make_child", I32(CHILD_TAG), I64(LONG_BITS), header).single())

        val wideBytes = fixture("managed_objects")
        val wide = assertIs<ReferenceValue.Struct>(
            CompiledFixture(wideBytes).call("make_wide", I64(LONG_BITS), I32(0x1AB), F64(1.5)).single(),
        )

        assertSlotsHoldTheFields(assertNotNull(layouts(guestBytes).struct(GUEST_CHILD_TYPE)), assertIs(child.reference), holdsReferences = true)
        assertSlotsHoldTheFields(assertNotNull(layouts(wideBytes).struct(MANAGED_WIDE_TYPE)), assertIs(wide.reference), holdsReferences = false)
    }

    @Test
    fun `a guest subtype passes its supertypes' checks and a supertype fails its subtype's check`() {

        val bytes = fixture("storage_layout_guest")
        val guest = CompiledFixture(bytes)
        val layouts = layouts(bytes)
        val headerCheck = assertNotNull(layouts.struct(GUEST_HEADER_TYPE)).check
        val childCheck = assertNotNull(layouts.struct(GUEST_CHILD_TYPE)).check
        val header = assertIs<ReferenceValue.Struct>(guest.call("make_header", I32(HEADER_TAG)).single())
        val grandchild = assertIs<ReferenceValue.Struct>(guest.call("make_grandchild", I32(CHILD_TAG), I64(LONG_BITS), I32(EXTRA)).single())
        val grandchildRtt = assertIs<CompiledStruct>(grandchild.reference).rtt
        val headerRtt = assertIs<CompiledStruct>(header.reference).rtt

        assertEquals(1, childCheck.supertypes)
        assertTrue(grandchildRtt.depth >= childCheck.supertypes)
        assertEquals(childCheck.rttKey, grandchildRtt.supertypes[childCheck.supertypes].key)
        assertEquals(headerCheck.rttKey, grandchildRtt.supertypes[headerCheck.supertypes].key)
        assertTrue(headerRtt.depth < childCheck.supertypes)
    }

    private fun assertSlotsHoldTheFields(
        layout: StructLayout,
        struct: CompiledStruct,
        holdsReferences: Boolean,
    ) {

        assertEquals(layout.storageClass, struct.javaClass.name)
        assertEquals(layout.check.rttKey, struct.rtt.key)

        var nonZeroNumbers = 0
        var references = 0
        layout.fields.forEachIndexed { index, field ->
            when (val slots = field.slots) {
                is NumberSlot -> {
                    val bits = slotInt(struct, slots.slot).toLong()
                    assertEquals(readStructField(struct, index), bits, "field $index")
                    if (bits != 0L) nonZeroNumbers++
                }
                is WideNumberSlots -> {
                    val bits = (slotInt(struct, slots.low).toLong() and LOW_BITS) or (slotInt(struct, slots.high).toLong() shl Int.SIZE_BITS)
                    assertEquals(readStructField(struct, index), bits, "field $index")
                    if (bits != 0L) nonZeroNumbers++
                }
                is ReferenceSlot -> {
                    val reference = slotReference(struct, slots.slot)
                    assertSame(struct.readFieldReference(index), reference, "field $index")
                    if (reference != null) references++
                }
            }
        }

        assertTrue(nonZeroNumbers >= 2, "${layout.storageClass} has $nonZeroNumbers non zero number fields")
        assertEquals(holdsReferences, references > 0, "${layout.storageClass} holds $references references")
    }

    private fun slotInt(
        struct: CompiledStruct,
        slot: Slot,
    ): Int {
        return when (slot) {
            is InlineSlot -> struct.javaClass.getField(slot.field).getInt(struct)
            is SpilledSlot -> (struct.javaClass.getField(slot.array).get(struct) as IntArray)[slot.index]
        }
    }

    private fun slotReference(
        struct: CompiledStruct,
        slot: Slot,
    ): Any? {
        return when (slot) {
            is InlineSlot -> struct.javaClass.getField(slot.field).get(struct)
            is SpilledSlot -> (struct.javaClass.getField(slot.array).get(struct) as Array<*>)[slot.index]
        }
    }

    private fun layouts(bytes: ByteArray): StorageLayouts {
        return assertIs<ChasmResult.Success<StorageLayouts>>(storageLayouts(bytes)).result
    }

    private companion object {
        const val GUEST_HEADER_TYPE = 0
        const val GUEST_CHILD_TYPE = 1
        const val MANAGED_WIDE_TYPE = 1
        const val HEADER_TAG = 3
        const val CHILD_TAG = 7
        const val EXTRA = 11
        const val LONG_BITS = 0x1122_3344_5566_7788L
        const val LOW_BITS = 0xFFFFFFFFL
    }
}
