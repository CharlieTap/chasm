package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.compiler.program.ProgramTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ControlStackTest {

    @Test
    fun reusesPoppedBlocksAtTheSameNestingDepth() {
        val stack = ControlStack()
        val first = stack.pushBlock(BlockKind.Block)
        first.reachedByBranch = true

        assertSame(first, stack.pop())

        val reused = stack.pushBlock(BlockKind.Loop)
        assertSame(first, reused)
        assertEquals(BlockKind.Loop, reused.kind)
        assertFalse(reused.inert)
        assertFalse(reused.reachedByBranch)
    }

    @Test
    fun resetsInertBlocksBeforeReuse() {
        val stack = ControlStack()
        val inert = stack.pushInert(BlockKind.If)
        inert.inElse = true

        assertTrue(inert.inert)
        stack.pop()

        val reused = stack.pushBlock(BlockKind.Block)
        assertSame(inert, reused)
        assertFalse(reused.inert)
        assertFalse(reused.inElse)
    }

    @Test
    fun clearsReusedBlocksWhenTheyBecomeInert() {
        val stack = ControlStack()
        val active = stack.pushBlock(BlockKind.Block)
        active.reachedByBranch = true
        active.entryFrameHeight = 3
        stack.pop()

        val inert = stack.pushInert(BlockKind.If)

        assertSame(active, inert)
        assertTrue(inert.inert)
        assertFalse(inert.reachedByBranch)
        assertEquals(0, inert.entryFrameHeight)
        assertTrue(inert.branchSlots.isEmpty())
        assertTrue(inert.resultTypes.isEmpty())
    }

    @Test
    fun reusesBlocksAcrossStacksWithTheSamePool() {
        val pool = ArrayList<BlockContext>()
        val first = ControlStack(pool).pushBlock(BlockKind.Block)
        val second = ControlStack(pool).pushBlock(BlockKind.Loop)

        assertSame(first, second)
        assertEquals(BlockKind.Loop, second.kind)
    }
}

private fun ControlStack.pushBlock(kind: BlockKind): BlockContext = push(
    kind = kind,
    baseHeight = 0,
    branchSlots = intArrayOf(),
    resultSlots = intArrayOf(),
    parameterSlots = intArrayOf(),
    branchTypes = emptyList(),
    resultTypes = emptyList(),
    parameterTypes = emptyList(),
    branchTarget = ProgramTarget(0),
    continuationTarget = ProgramTarget(0),
    handlerDepth = 0,
)
