package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HostGcFieldInfoTest {

    @Test
    fun `exposes every storage type`() {
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.PACKED_I8).isPackedI8)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.PACKED_I16).isPackedI16)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.I32).isI32)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.I64).isI64)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.F32).isF32)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.F64).isF64)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.REFERENCE).isReference)
        assertTrue(HostGcFieldInfo(HostGcFieldInfo.V128).isV128)
    }

    @Test
    fun `exposes mutability separately from the storage type`() {
        val immutable = HostGcFieldInfo(HostGcFieldInfo.I64)
        val mutable = HostGcFieldInfo(HostGcFieldInfo.I64 or HostGcFieldInfo.MUTABLE_MASK)

        assertFalse(immutable.mutable)
        assertTrue(mutable.mutable)
        assertEquals(HostGcFieldInfo.I64, mutable.storageKind)
    }
}
