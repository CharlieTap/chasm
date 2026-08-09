package io.github.charlietap.chasm.runtime.program

import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

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
}
