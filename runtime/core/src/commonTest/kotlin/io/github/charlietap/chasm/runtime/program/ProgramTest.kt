package io.github.charlietap.chasm.runtime.program

import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionRegion
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ProgramTest {

    @Test
    fun `appends instructions at stable program addresses`() {
        val program = Program(initialCapacity = 1)
        val first = dispatchableInstruction()
        val second = dispatchableInstruction()

        val firstEntryIp = program.append(arrayOf(first))
        val secondEntryIp = program.append(arrayOf(second))

        assertEquals(0, firstEntryIp)
        assertEquals(1, secondEntryIp)
        assertEquals(2, program.size)
        assertSame(first, program.instructions[firstEntryIp])
        assertSame(second, program.instructions[secondEntryIp])
    }

    @Test
    fun `empty appends preserve the next program address`() {
        val program = Program(initialCapacity = 1)

        assertEquals(0, program.append(emptyArray()))
        assertEquals(0, program.size)
        assertEquals(0, program.append(arrayOf(dispatchableInstruction())))
    }

    @Test
    fun `replaces and truncates appended instructions`() {
        val program = Program(initialCapacity = 1)
        val first = dispatchableInstruction()
        val replacement = dispatchableInstruction()

        program.append(first)
        program.append(dispatchableInstruction())
        program.replace(0, replacement)
        program.truncate(1)

        assertEquals(1, program.size)
        assertSame(replacement, program.instructions[0])
    }

    @Test
    fun `exception metadata covers only its function and is removed on truncation`() {
        val program = Program(1)
        program.append(Array(30) { dispatchableInstruction() })
        assertFalse(program.hasExceptionHandlers)
        val first = FunctionExceptionTable(5, 5, 0, 0, emptyArray(), intArrayOf())
        val second = FunctionExceptionTable(20, 10, 0, 0, emptyArray(), intArrayOf())
        program.registerExceptionTable(first)
        program.registerExceptionTable(second)
        for (ip in listOf(-1, 0, 4, 10, 19, 30)) assertNull(program.exceptionTable(ip))
        assertSame(first, program.exceptionTable(5))
        assertSame(first, program.exceptionTable(9))
        assertSame(second, program.exceptionTable(29))
        program.truncate(25)
        assertNull(program.exceptionTable(20))
        assertTrue(program.hasExceptionHandlers)
        program.truncate(5)
        assertFalse(program.hasExceptionHandlers)
        program.append(Array(5) { dispatchableInstruction() })
        assertNull(program.exceptionTable(5))
        program.registerExceptionTable(first)
        assertSame(first, program.exceptionTable(5))
    }

    @Test
    fun `relocation shares relative metadata and rejects overlapping publication`() {
        val original = FunctionExceptionTable(
            0,
            5,
            1,
            1,
            arrayOf(ExceptionRegion(0, 2, -1, emptyArray())),
            intArrayOf(1),
        )
        val relocated = original.relocated(10)
        assertSame(original.regions, relocated.regions)
        assertSame(original.tailCallOffsets, relocated.tailCallOffsets)
        assertEquals(15, relocated.endIp)
        val program = Program()
        program.append(Array(20) { dispatchableInstruction() })
        program.registerExceptionTable(relocated)
        assertFailsWith<IllegalArgumentException> { program.registerExceptionTable(original) }
        assertFailsWith<IllegalArgumentException> { program.registerExceptionTable(original.relocated(18)) }
        assertSame(relocated, program.exceptionTable(10))
    }
}
