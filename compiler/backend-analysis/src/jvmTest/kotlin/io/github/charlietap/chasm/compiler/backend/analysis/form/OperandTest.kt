package io.github.charlietap.chasm.compiler.backend.analysis.form

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class OperandTest {

    @Test
    fun `shared and fresh slots compare equal on both sides of the shared range`() {

        assertEquals(Operand.Slot(0), slot(0))
        assertEquals(Operand.Slot(4_095), slot(4_095))
        assertEquals(Operand.Slot(4_096), slot(4_096))
        assertEquals(Operand.Slot(-1), slot(-1))
        assertSame(slot(17), slot(17))
    }

    @Test
    fun `shared and fresh immediates compare equal on both sides of the shared range`() {

        assertEquals(Operand.Immediate(-256), immediate(-256L))
        assertEquals(Operand.Immediate(-257), immediate(-257L))
        assertEquals(Operand.Immediate(4_095), immediate(4_095L))
        assertEquals(Operand.Immediate(4_096), immediate(4_096L))
        assertSame(immediate(42L), immediate(42))
    }

    @Test
    fun `immediates whose low bits match a shared value keep their full width`() {

        val wide = (1L shl 32) + 5

        assertNotEquals(immediate(5L), immediate(wide))
        assertEquals(wide, (immediate(wide) as Operand.Immediate).bits)
        assertEquals(-1L, (immediate(-1) as Operand.Immediate).bits)
    }

    @Test
    fun `floating point immediates keep their raw bits`() {

        val nan = Float.fromBits(0x7FC0_1234)

        assertEquals((-0.0f).toRawBits().toLong(), (immediate(-0.0f) as Operand.Immediate).bits)
        assertEquals(nan.toRawBits().toLong(), (immediate(nan) as Operand.Immediate).bits)
        assertEquals((-0.0).toRawBits(), (immediate(-0.0) as Operand.Immediate).bits)
        assertEquals(1.0.toRawBits(), (immediate(1.0) as Operand.Immediate).bits)
    }
}
