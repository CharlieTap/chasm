package io.github.charlietap.chasm.compiler.program

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.program.Program
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProgramBuilderTest {

    @Test
    fun patchesForwardTargetsWithAbsoluteInstructionPointers() {
        val program = programWithSize(41)
        val builder = ProgramBuilder(program)
        val target = builder.target()
        var targetIp = -1

        builder.append(target) { resolvedTargetIp ->
            targetIp = resolvedTargetIp
            noOpInstruction
        }
        builder.append(noOpInstruction)
        builder.bind(target)

        assertEquals(43, targetIp)
        builder.finish()
        assertEquals(43, program.size)
    }

    @Test
    fun patchesTargetsThatAreAlreadyBound() {
        val program = programWithSize(13)
        val builder = ProgramBuilder(program)
        val target = builder.target()
        var targetIp = -1

        builder.bind(target)
        builder.append(target) { resolvedTargetIp ->
            targetIp = resolvedTargetIp
            noOpInstruction
        }

        assertEquals(13, targetIp)
        builder.finish()
        assertEquals(14, program.size)
    }

    @Test
    fun patchesTargetListsAfterEveryTargetIsBound() {
        val program = programWithSize(5)
        val builder = ProgramBuilder(program)
        val first = builder.target()
        val second = builder.target()
        var targetIps = intArrayOf()

        builder.append(intArrayOf(first.index, second.index)) { resolvedTargetIps ->
            targetIps = resolvedTargetIps
            noOpInstruction
        }
        builder.bind(first)
        builder.append(noOpInstruction)
        builder.bind(second)

        assertEquals(listOf(6, 7), targetIps.toList())
        builder.finish()
        assertEquals(7, program.size)
    }

    @Test
    fun rejectsUnboundTargets() {
        val builder = ProgramBuilder(programWithSize(0))
        builder.target()

        assertFailsWith<IllegalStateException> {
            builder.finish()
        }
    }

    @Test
    fun rejectsTargetsBoundTwice() {
        val builder = ProgramBuilder(programWithSize(0))
        val target = builder.target()
        builder.bind(target)

        assertFailsWith<IllegalStateException> {
            builder.bind(target)
        }
    }

    @Test
    fun rejectsChangesAfterFinishing() {
        val builder = ProgramBuilder(programWithSize(0))
        builder.finish()

        assertFailsWith<IllegalStateException> {
            builder.append(noOpInstruction)
        }
    }
}

private fun programWithSize(size: Int): Program = Program(maxOf(size, 1)).apply {
    repeat(size) {
        append(noOpInstruction)
    }
}

private val noOpInstruction = DispatchableInstruction { _, _, _, _, nextIp -> nextIp }
