package io.github.charlietap.chasm.compiler.frontend.program

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ProgramBuilderTest {

    @Test
    fun patchesForwardTargetsWithFunctionLocalPositions() {
        val builder = ProgramBuilder()
        val target = builder.target()
        var targetPosition = -1

        builder.append(target) { resolvedTargetPosition ->
            targetPosition = resolvedTargetPosition
            patchedInstruction
        }
        builder.append(noOpInstruction)
        builder.bind(target)

        assertEquals(2, targetPosition)
        builder.finish()
        assertEquals(2, builder.size)
        assertEquals(2, builder.targetOffset(target.index))
        assertSame(patchedInstruction, builder.instructions()[0])
    }

    @Test
    fun patchesTargetsThatAreAlreadyBound() {
        val builder = ProgramBuilder()
        builder.append(noOpInstruction)
        val target = builder.target()
        var targetPosition = -1

        builder.bind(target)
        builder.append(target) { resolvedTargetPosition ->
            targetPosition = resolvedTargetPosition
            patchedInstruction
        }

        assertEquals(1, targetPosition)
        builder.finish()
        assertEquals(2, builder.size)
        assertSame(patchedInstruction, builder.instructions()[1])
    }

    @Test
    fun patchesTargetListsAfterEveryTargetIsBound() {
        val builder = ProgramBuilder()
        val first = builder.target()
        val second = builder.target()
        var targetPositions = intArrayOf()

        builder.append(intArrayOf(first.index, second.index)) { resolvedTargetPositions ->
            targetPositions = resolvedTargetPositions
            patchedInstruction
        }
        builder.bind(first)
        builder.append(noOpInstruction)
        builder.bind(second)

        assertEquals(listOf(1, 2), targetPositions.toList())
        builder.finish()
        assertEquals(2, builder.size)
        assertSame(patchedInstruction, builder.instructions()[0])
    }

    @Test
    fun rejectsUnboundTargets() {
        val builder = ProgramBuilder()
        builder.target()

        assertFailsWith<IllegalStateException> {
            builder.finish()
        }
    }

    @Test
    fun rejectsTargetsBoundTwice() {
        val builder = ProgramBuilder()
        val target = builder.target()
        builder.bind(target)

        assertFailsWith<IllegalStateException> {
            builder.bind(target)
        }
    }

    @Test
    fun rejectsChangesAfterFinishing() {
        val builder = ProgramBuilder()
        builder.finish()

        assertFailsWith<IllegalStateException> {
            builder.append(noOpInstruction)
        }
    }

    @Test
    fun rejectsReadingInstructionsBeforeFinishing() {
        val builder = ProgramBuilder()
        builder.append(noOpInstruction)

        assertFailsWith<IllegalStateException> {
            builder.instructions()
        }
    }
}

private val noOpInstruction: Instruction = AdminInstruction.Checkpoint

private val patchedInstruction: Instruction = ControlInstruction.Unreachable
