package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.jumpTargets
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import kotlin.test.Test
import kotlin.test.assertEquals

class LoopCutsTest {

    private val bytes = fixture("loops")
    private val function: CirFunction = ModuleCompiler(decodeModule(bytes)).getOrThrow { IllegalStateException(it.toString()) }
        .functions.single()

    private val loop: IntRange = function.instructions.withIndex()
        .flatMap { (source, instruction) -> instruction.jumpTargets().filter { it <= source }.map { it..source } }
        .single()

    @Test
    fun `cuts inside a loop cross it and cuts outside do not`() {
        val crossings = loopCrossings(function)

        (0..function.instructions.size).forEach { position ->
            val inside = position > loop.first && position <= loop.last
            assertEquals(if (inside) 1 else 0, crossings[position], "position $position")
        }
    }

    @Test
    fun `a chunk ends before a loop it cannot hold rather than inside it`() {
        val crossings = loopCrossings(function)

        assertEquals(loop.first, cheapestCut(crossings, start = 0, limit = loop.last))
    }

    @Test
    fun `a chunk takes the furthest cut that crosses no loop`() {
        val crossings = loopCrossings(function)
        val size = function.instructions.size

        assertEquals(size, cheapestCut(crossings, start = 0, limit = size))
        assertEquals(loop.last + 1, cheapestCut(crossings, start = loop.first, limit = loop.last + 1))
    }

    @Test
    fun `a chunk starting inside a loop ends as far along as it reaches`() {
        val crossings = loopCrossings(function)
        val start = loop.first + 1

        assertEquals(loop.last, cheapestCut(crossings, start = start, limit = loop.last))
    }
}
