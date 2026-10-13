package io.github.charlietap.chasm.decoder.builder

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionBlockBuilderTest {

    @Test
    fun `builds an empty block`() {
        val builder = InstructionBlockBuilder()

        assertEquals(emptyList(), builder.build())
    }

    @Test
    fun `builds instructions in the order they were appended`() {
        val builder = InstructionBlockBuilder()

        builder.append(ControlInstruction.Nop)
        builder.appendEnd(2)

        assertEquals(listOf(ControlInstruction.Nop, ControlInstruction.End(2)), builder.build())
    }

    @Test
    fun `grows beyond its initial capacity`() {
        val builder = InstructionBlockBuilder()
        val expected = List<Instruction>(1_000) { NumericInstruction.I32Const(it) }

        expected.forEach(builder::append)

        assertEquals(expected, builder.build())
    }

    @Test
    fun `clearing does not affect blocks that were already built`() {
        val builder = InstructionBlockBuilder()

        builder.append(ControlInstruction.Nop)
        val first = builder.build()

        builder.clear()
        builder.append(ControlInstruction.Unreachable)
        val second = builder.build()

        assertEquals(listOf<Instruction>(ControlInstruction.Nop), first)
        assertEquals(listOf<Instruction>(ControlInstruction.Unreachable), second)
    }
}
