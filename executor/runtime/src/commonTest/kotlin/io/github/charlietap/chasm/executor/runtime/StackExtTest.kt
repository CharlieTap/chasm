package io.github.charlietap.chasm.executor.runtime

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.constOperation
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.ext.testOperation
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import kotlin.test.Test
import kotlin.test.assertEquals

class StackExtTest {

    @Test
    fun `can push a stack frame to the stack`() {

        val stack = stack()
        val frame = frame()

        val actual = stack.pushFrame(frame)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val frameEntry = stack.popFrameOrNull()
        assertEquals(frame, frameEntry)
    }

    @Test
    fun `pushing too many frames to the stack returns an error`() {

        val frame = frame()
        val stack = stack(
            frames = List(Stack.MAX_DEPTH) { frame },
        )

        val actual = stack.pushFrame(frame)

        assertEquals(Err(InvocationError.CallStackExhausted), actual)
        assertEquals(Stack.MAX_DEPTH, stack.size())
    }

    @Test
    fun `can run an const operation on the stack`() {

        val stack = stack()

        stack.constOperation(117)
        stack.constOperation(117L)
        stack.constOperation(117f)
        stack.constOperation(117.0)

        assertEquals(4, stack.size())

        val f64 = stack.popValueOrNull()?.value as F64
        val f32 = stack.popValueOrNull()?.value as F32
        val i64 = stack.popValueOrNull()?.value as I64
        val i32 = stack.popValueOrNull()?.value as I32

        assertEquals(117.0, f64.value)
        assertEquals(117f, f32.value)
        assertEquals(117L, i64.value)
        assertEquals(117, i32.value)
    }

    @Test
    fun `can run an i32 unary operation on the stack`() {

        val stack = stack()
        val constructor = ::I32
        val op = Int::countTrailingZeroBits

        stack.push(Stack.Entry.Value(I32(16)))

        val actual = stack.unaryOperation(constructor, op)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(4, value.value)
    }

    @Test
    fun `can run an i64 unary operation on the stack`() {

        val stack = stack()
        val constructor = ::I64
        val op = Long::countTrailingZero

        stack.push(Stack.Entry.Value(I64(16)))

        val actual = stack.unaryOperation(constructor, op)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I64
        assertEquals(4, value.value)
    }

    @Test
    fun `can run an f32 unary operation on the stack`() {

        val stack = stack()
        val constructor = ::F32
        val op = Float::sqrt

        stack.push(Stack.Entry.Value(F32(16f)))

        val actual = stack.unaryOperation(constructor, op)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as F32
        assertEquals(4f, value.value)
    }

    @Test
    fun `can run an f64 unary operation on the stack`() {

        val stack = stack()
        val constructor = ::F64
        val op = Double::sqrt

        stack.push(Stack.Entry.Value(F64(16.0)))

        val actual = stack.unaryOperation(constructor, op)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as F64
        assertEquals(4.0, value.value)
    }

    @Test
    fun `can run an i32 binary operation on the stack`() {

        val stack = stack()
        val constructor = ::I32

        stack.push(Stack.Entry.Value(I32(1)))
        stack.push(Stack.Entry.Value(I32(3)))

        val actual = stack.binaryOperation(constructor, Int::plus)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(4, value.value)
    }

    @Test
    fun `can run an i64 binary operation on the stack`() {

        val stack = stack()
        val constructor = ::I64

        stack.push(Stack.Entry.Value(I64(1)))
        stack.push(Stack.Entry.Value(I64(3)))

        val actual = stack.binaryOperation(constructor, Long::plus)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I64
        assertEquals(4, value.value)
    }

    @Test
    fun `can run an f32 binary operation on the stack`() {

        val stack = stack()
        val constructor = ::F32

        stack.push(Stack.Entry.Value(F32(1f)))
        stack.push(Stack.Entry.Value(F32(3f)))

        val actual = stack.binaryOperation(constructor, Float::plus)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as F32
        assertEquals(4f, value.value)
    }

    @Test
    fun `can run an f64 binary operation on the stack`() {

        val stack = stack()
        val constructor = ::F64

        stack.push(Stack.Entry.Value(F64(1.0)))
        stack.push(Stack.Entry.Value(F64(3.0)))

        val actual = stack.binaryOperation(constructor, Double::plus)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as F64
        assertEquals(4.0, value.value)
    }

    @Test
    fun `can run an i32 test operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(I32(1)))

        val actual = stack.testOperation(Int::eqz)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(0, value.value)
    }

    @Test
    fun `can run a i64 test operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(I64(1)))

        val actual = stack.testOperation(Long::eqz)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(0, value.value)
    }

    @Test
    fun `can run an i32 relational operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(I32(1)))
        stack.push(Stack.Entry.Value(I32(1)))

        val actual = stack.relationalOperation(Int::eq)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(1, value.value)
    }

    @Test
    fun `can run an i64 relational operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(I64(1)))
        stack.push(Stack.Entry.Value(I64(1)))

        val actual = stack.relationalOperation(Long::eq)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(1, value.value)
    }

    @Test
    fun `can run an f32 relational operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(F32(1f)))
        stack.push(Stack.Entry.Value(F32(1f)))

        val actual = stack.relationalOperation(Float::eq)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(1, value.value)
    }

    @Test
    fun `can run an f64 relational operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(F64(1.0)))
        stack.push(Stack.Entry.Value(F64(1.0)))

        val actual = stack.relationalOperation(Double::eq)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(1, value.value)
    }

    @Test
    fun `can run a conversion operation on the stack`() {

        val stack = stack()

        stack.push(Stack.Entry.Value(I64(117L)))

        val actual = stack.convertOperation(::I32, Long::wrap)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val entry = stack.popValueOrNull()
        val value = entry?.value as I32
        assertEquals(117, value.value)
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
