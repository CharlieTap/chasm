package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationStrategySelectorTest {

    private val policy = ParallelismPolicy(minimumWork = 20_000, workPerWorker = 10_000)

    @Test
    fun `selects serial validation for small workloads`() {
        val workers = selectValidationWorkers(
            functions = functions(32, 32),
            mode = ValidationMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(1, workers)
    }

    @Test
    fun `selects a worker for every share of expensive workloads`() {
        val workers = selectValidationWorkers(
            functions = functions(10_000, 10_000, 10_000, 10_000, 10_000, 10_000),
            mode = ValidationMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(6, workers)
    }

    @Test
    fun `one expensive function leaves only the rest to share`() {
        val workers = selectValidationWorkers(
            functions = functions(60_000, *IntArray(40) { 1_000 }),
            mode = ValidationMode.AUTO,
            policy = policy,
            availableProcessors = 8,
        )

        assertEquals(5, workers)
    }

    @Test
    fun `a forced strategy ignores how little there is to validate`() {
        val functions = functions(1_000, 1_000)

        assertEquals(2, selectValidationWorkers(functions, ValidationMode.PARALLEL, policy, availableProcessors = 4))
        assertEquals(1, selectValidationWorkers(functions, ValidationMode.PARALLEL, policy, availableProcessors = 1))
        assertEquals(1, selectValidationWorkers(functions, ValidationMode.SERIAL, policy, availableProcessors = 4))
    }

    @Test
    fun `batches functions in module order`() {
        val batches = batchFunctions(functions(3_000, 3_000, 100, 5_000, 100))

        assertEquals(listOf(0 to 2, 2 to 4, 4 to 5), List(batches.count) { batches.start(it) to batches.end(it) })
    }

    private fun functions(vararg instructionCounts: Int) = instructionCounts.mapIndexed { index, count ->
        function(
            idx = Index.FunctionIndex(index.toUInt()),
            body = Expression(List(count) { ControlInstruction.Nop }),
        )
    }
}
