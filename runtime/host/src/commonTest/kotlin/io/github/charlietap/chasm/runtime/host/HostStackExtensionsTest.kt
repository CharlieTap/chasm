package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.host.readF32
import io.github.charlietap.chasm.host.readF64
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.readI64
import io.github.charlietap.chasm.host.writeF32
import io.github.charlietap.chasm.host.writeF64
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.host.writeI64
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

        context(stack) {
            parameterBase.writeI32(0, -123456789)
            parameterBase.writeI64(1, Long.MIN_VALUE + 17)
            parameterBase.writeF32(2, float)
            resultBase.writeF64(0, double)

            assertEquals(-123456789, parameterBase.readI32(0))
            assertEquals(Long.MIN_VALUE + 17, parameterBase.readI64(1))
            assertEquals(float.toRawBits(), parameterBase.readF32(2).toRawBits())
            assertEquals(double.toRawBits(), resultBase.readF64(0).toRawBits())
        }
    }
}
