package io.github.charlietap.chasm.compiler.instruction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MemoryInstructionEmitterTest {

    @Test
    fun precomputesARepresentableEffectiveAddress() {
        assertEquals(12, precomputedEffectiveAddress(address = 4, offset = 8))
    }

    @Test
    fun preservesRuntimeTrapsForInvalidEffectiveAddresses() {
        assertNull(precomputedEffectiveAddress(address = -1, offset = 0))
        assertNull(precomputedEffectiveAddress(address = 0, offset = -1))
        assertNull(precomputedEffectiveAddress(address = Int.MAX_VALUE, offset = 1))
    }
}
