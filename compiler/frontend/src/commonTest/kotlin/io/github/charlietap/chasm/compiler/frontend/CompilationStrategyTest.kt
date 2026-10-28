package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import kotlin.test.Test
import kotlin.test.assertEquals

class selectCompilationStrategyTest {

    private val policy = ParallelismPolicy(minimumWork = 8_000, workPerWorker = 2_000)

    @Test
    fun `serial mode always compiles serially`() {
        val functions = functions(6) { 10_000 }

        assertEquals(1, selectCompilationWorkers(functions, CompilationMode.SERIAL, policy, availableProcessors = 8))
    }

    @Test
    fun `parallel mode uses a worker for every processor`() {
        val functions = functions(10) { 1 }

        assertEquals(8, selectCompilationWorkers(functions, CompilationMode.PARALLEL, policy, availableProcessors = 8))
    }

    @Test
    fun `auto keeps a little work serial`() {
        val functions = functions(32) { 1 }

        assertEquals(1, selectCompilationWorkers(functions, CompilationMode.AUTO, policy, availableProcessors = 8))
    }

    @Test
    fun `auto keeps work dominated by one function serial`() {
        val functions = functions(6) { functionIndex -> if (functionIndex == 0) 20_000 else 100 }

        assertEquals(1, selectCompilationWorkers(functions, CompilationMode.AUTO, policy, availableProcessors = 8))
    }

    @Test
    fun `auto uses every processor for plenty of balanced work`() {
        val functions = functions(16) { 10_000 }

        assertEquals(8, selectCompilationWorkers(functions, CompilationMode.AUTO, policy, availableProcessors = 8))
    }

    @Test
    fun `batches functions in module order`() {
        val batches = batchFunctions(functions(5) { functionIndex -> intArrayOf(1_000, 1_000, 100, 2_000, 100)[functionIndex] })

        assertEquals(listOf(0 to 2, 2 to 4, 4 to 5), List(batches.count) { batches.start(it) to batches.end(it) })
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
