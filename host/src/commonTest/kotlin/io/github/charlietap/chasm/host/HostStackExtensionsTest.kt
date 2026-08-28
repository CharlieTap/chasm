package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertEquals

class HostStackExtensionsTest {

    @Test
    fun `reads and writes every raw slot encoding at an absolute base`() {
        val stack = LongArray(12)
        val parameterBase = 2
        val resultBase = 7
        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678)
        val reference = 0x123456789ABCDEFL

        context(stack) {
            parameterBase.writeI32(0, -123456789)
            parameterBase.writeI64(1, Long.MIN_VALUE + 17)
            parameterBase.writeF32(2, float)
            resultBase.writeF64(0, double)
            resultBase.writeRawReference(1, reference)

            assertEquals(-123456789, parameterBase.readI32(0))
            assertEquals(Long.MIN_VALUE + 17, parameterBase.readI64(1))
            assertEquals(float.toRawBits(), parameterBase.readF32(2).toRawBits())
            assertEquals(double.toRawBits(), resultBase.readF64(0).toRawBits())
            assertEquals(reference, resultBase.readRawReference(1))
        }
    }
}
