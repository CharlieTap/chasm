package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HostReferencesTest {

    @Test
    fun `withScope opens and closes a reference scope`() {
        val references = RecordingHostReferences()

        val result = references.withScope(capacity = 3) {
            rootScoped(42L)
            117
        }

        assertEquals(117, result)
        assertEquals(3, references.requestedCapacity)
        assertEquals(0, references.top)
    }

    @Test
    fun `withScope closes the reference scope when the block throws`() {
        val references = RecordingHostReferences()

        assertFailsWith<IllegalStateException> {
            references.withScope {
                rootScoped(42L)
                error("expected")
            }
        }

        assertEquals(0, references.top)
    }
}

private class RecordingHostReferences : HostReferences {

    var requestedCapacity = 0
    var top = 0

    override fun beginScope(capacity: Int): Int {
        requestedCapacity = capacity
        return top
    }

    override fun rootScoped(reference: HostReference): HostReference {
        top++
        return reference
    }

    override fun endScope(marker: Int) {
        top = marker
    }

    override fun retain(reference: HostReference): HostReferenceRoot = error("unused")

    override fun reference(root: HostReferenceRoot): HostReference = error("unused")

    override fun release(root: HostReferenceRoot) = error("unused")
}
