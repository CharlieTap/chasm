package io.github.charlietap.chasm.runtime.diagnostic

import io.github.charlietap.chasm.compiled.format.MEMORY_ACCESS_READ
import io.github.charlietap.chasm.compiled.format.MEMORY_ACCESS_WRITE
import kotlin.test.Test
import kotlin.test.assertEquals

class FailedMemoryAccessTest {

    @Test
    fun `the access kinds compiled code passes are the ordinals of read and write`() {
        assertEquals(MEMORY_ACCESS_READ, FailedMemoryAccess.Kind.READ.ordinal)
        assertEquals(MEMORY_ACCESS_WRITE, FailedMemoryAccess.Kind.WRITE.ordinal)
    }
}
