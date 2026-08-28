package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExternalReferenceCollectionTest {

    @Test
    fun `collection reports host references wrapped by extern references`() {
        val heap = GarbageCollectedHeap()
        val marker = RecordingHostReferenceMarker()
        val hostReference = (117L shl RV_SHIFT_BITS) or RV_TYPE_HOST
        val externReference = (hostReference shl RV_SHIFT_BITS) or RV_TYPE_EXTERN

        heap.beginCollection(marker)
        heap.markRoot(externReference)
        heap.finishCollection()

        assertEquals(listOf(hostReference), marker.references)
    }

    @Test
    fun `collection traces guest references wrapped by extern references`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(0, 0, intArrayOf())
        val guestReference = heap.allocateStruct(descriptor, longArrayOf())
        val externReference = (guestReference shl RV_SHIFT_BITS) or RV_TYPE_EXTERN

        heap.beginCollection()
        heap.markRoot(externReference)
        heap.finishCollection()

        assertTrue(heap.isAllocatedReferenceForTesting(guestReference))
    }
}

private class RecordingHostReferenceMarker : GcHostReferenceMarker {

    val references = mutableListOf<Long>()

    override fun markHostReference(rawReference: Long) {
        references += rawReference
    }
}
