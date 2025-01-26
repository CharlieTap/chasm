package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.constOperation
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.ext.testOperation
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.stack.vstack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StackExtTest {

    @Test
    fun `can push a stack frame to the stack`() {

        val stack = cstack()
        val frame = frame()

        stack.push(frame)

        assertEquals(1, stack.size())

        val frameEntry = stack.popFrame()
        assertEquals(frame, frameEntry)
    }

    @Test
    fun `pushing too many frames to the stack returns an error`() {

        val frame = frame()
        val controlStack = cstack(
            frames = List(ControlStack.MAX_DEPTH) { frame },
        )

        val actual = assertFailsWith<InvocationException> {
            controlStack.push(frame)
        }

        assertEquals(InvocationError.CallStackExhausted, actual.error)
        assertEquals(ControlStack.MAX_DEPTH, controlStack.size())
    }

    @Test
    fun `can run an const operation on the stack`() {

        val stack = vstack()

        stack.constOperation(117)
        stack.constOperation(117L)
        stack.constOperation(117f)
        stack.constOperation(117.0)

        assertEquals(4, stack.depth())

        val f64 = stack.popF64()
        val f32 = stack.popF32()
        val i64 = stack.popI64()
        val i32 = stack.popI32()

        assertEquals(117.0, f64)
        assertEquals(117f, f32)
        assertEquals(117L, i64)
        assertEquals(117, i32)
    }

    @Test
    fun `can run an i32 unary operation on the stack`() {

        val stack = vstack()
        val op = Int::countTrailingZeroBits

        stack.pushI32(16)

        val actual = stack.unaryOperation(op)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(4, value)
    }

    @Test
    fun `can run an i64 unary operation on the stack`() {

        val stack = vstack()
        val op = Long::countTrailingZero

        stack.pushI64(16)

        val actual = stack.unaryOperation(op)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI64()
        assertEquals(4, value)
    }

    @Test
    fun `can run an f32 unary operation on the stack`() {

        val stack = vstack()
        val op = Float::sqrt

        stack.pushF32(16f)

        val actual = stack.unaryOperation(op)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popF32()
        assertEquals(4f, value)
    }

    @Test
    fun `can run an f64 unary operation on the stack`() {

        val stack = vstack()
        val op = Double::sqrt

        stack.pushF64(16.0)

        val actual = stack.unaryOperation(op)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popF64()
        assertEquals(4.0, value)
    }

    @Test
    fun `can run an i32 binary operation on the stack`() {

        val stack = vstack()

        stack.pushI32(1)
        stack.pushI32(3)

        val actual = stack.binaryOperation(Int::plus)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(4, value)
    }

    @Test
    fun `can run an i64 binary operation on the stack`() {

        val stack = vstack()

        stack.pushI64(1)
        stack.pushI64(3)

        val actual = stack.binaryOperation(Long::plus)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI64()
        assertEquals(4, value)
    }

    @Test
    fun `can run an f32 binary operation on the stack`() {

        val stack = vstack()

        stack.pushF32(1f)
        stack.pushF32(3f)

        val actual = stack.binaryOperation(Float::plus)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popF32()
        assertEquals(4f, value)
    }

    @Test
    fun `can run an f64 binary operation on the stack`() {

        val stack = vstack()

        stack.pushF64(1.0)
        stack.pushF64(3.0)

        val actual = stack.binaryOperation(Double::plus)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popF64()

        assertEquals(4.0, value)
    }

    @Test
    fun `can run an i32 test operation on the stack`() {

        val stack = vstack()

        stack.pushI32(1)

        val actual = stack.testOperation(Int::eqz)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(0, value)
    }

    @Test
    fun `can run a i64 test operation on the stack`() {

        val stack = vstack()

        stack.pushI64(1)

        val actual = stack.testOperation(Long::eqz)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(0, value)
    }

    @Test
    fun `can run an i32 relational operation on the stack`() {

        val stack = vstack()

        stack.pushI32(1)
        stack.pushI32(1)

        val actual = stack.relationalOperation(Int::eq)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(1, value)
    }

    @Test
    fun `can run an i64 relational operation on the stack`() {

        val stack = vstack()

        stack.pushI64(1)
        stack.pushI64(1)

        val actual = stack.relationalOperation(Long::eq)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(1, value)
    }

    @Test
    fun `can run an f32 relational operation on the stack`() {

        val stack = vstack()

        stack.pushF32(1f)
        stack.pushF32(1f)

        val actual = stack.relationalOperation(Float::eq)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(1, value)
    }

    @Test
    fun `can run an f64 relational operation on the stack`() {

        val stack = vstack()

        stack.pushF64(1.0)
        stack.pushF64(1.0)

        val actual = stack.relationalOperation(Double::eq)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(1, value)
    }

    @Test
    fun `can run a conversion operation on the stack`() {

        val stack = vstack()

        stack.pushI64(117L)

        val actual = stack.convertOperation(Long::wrap)

        assertEquals(Unit, actual)
        assertEquals(1, stack.depth())

        val value = stack.popI32()
        assertEquals(117, value)
    }
}

private fun Int.eq(other: Int): Boolean = this == other

private fun Long.eq(other: Long): Boolean = this == other

private fun Float.eq(other: Float): Boolean = this == other

private fun Double.eq(other: Double): Boolean = this == other

private fun Int.eqz(): Boolean = this == 0

private fun Long.eqz(): Boolean = this == 0L

private fun Long.countTrailingZero(): Long = countTrailingZeroBits().toLong()

private fun Long.wrap(): Int = this.toInt()

private fun Float.sqrt(): Float = kotlin.math.sqrt(this)

private fun Double.sqrt(): Double = kotlin.math.sqrt(this)
