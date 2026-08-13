package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.function
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CompilationPlannerTest {

    @Test
    fun `serial strategy always compiles serially`() {
        val functions = functions(6) { 10_000 }

        val plan = CompilationPlanner(functions, CompilationMode.SERIAL, availableProcessors = 8)

        assertIs<CompilationPlan.Serial>(plan)
    }

    @Test
    fun `parallel strategy uses every available worker`() {
        val functions = functions(10) { 1 }

        val plan = CompilationPlanner(functions, CompilationMode.PARALLEL, availableProcessors = 8)

        val parallel = assertIs<CompilationPlan.Parallel>(plan)
        assertEquals(6, parallel.assignments.size)
        assertEquals(functions.indices.toSet(), parallel.assignments.flatMap(IntArray::asIterable).toSet())
    }

    @Test
    fun `auto keeps many tiny functions serial`() {
        val functions = functions(32) { 1 }

        val plan = CompilationPlanner(functions, CompilationMode.AUTO, availableProcessors = 8)

        assertIs<CompilationPlan.Serial>(plan)
    }

    @Test
    fun `auto keeps indivisible work serial`() {
        val functions = functions(6) { functionIndex ->
            if (functionIndex == 0) 20_000 else 1_000
        }

        val plan = CompilationPlanner(functions, CompilationMode.AUTO, availableProcessors = 8)

        assertIs<CompilationPlan.Serial>(plan)
    }

    @Test
    fun `auto parallelizes balanced work`() {
        val functions = functions(6) { 10_000 }

        val plan = CompilationPlanner(functions, CompilationMode.AUTO, availableProcessors = 8)

        val parallel = assertIs<CompilationPlan.Parallel>(plan)
        assertEquals(6, parallel.assignments.size)
        assertEquals(functions.indices.toSet(), parallel.assignments.flatMap(IntArray::asIterable).toSet())
    }

    @Test
    fun `auto selects the worker count that fits the work distribution`() {
        val functions = functions(6) { functionIndex ->
            if (functionIndex < 2) 10_000 else 100
        }

        val plan = CompilationPlanner(functions, CompilationMode.AUTO, availableProcessors = 8)

        val parallel = assertIs<CompilationPlan.Parallel>(plan)
        assertEquals(3, parallel.assignments.size)
        assertEquals(functions.indices.toSet(), parallel.assignments.flatMap(IntArray::asIterable).toSet())
    }

    private fun functions(
        count: Int,
        instructionCount: (Int) -> Int,
    ) = List(count) { functionIndex ->
        function(
            idx = Index.FunctionIndex(functionIndex.toUInt()),
            body = Expression(List(instructionCount(functionIndex)) { ControlInstruction.Nop }),
        )
    }
}
