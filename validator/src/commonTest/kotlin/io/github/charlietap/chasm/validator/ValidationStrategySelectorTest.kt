package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.fixture.ast.module.function
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ValidationStrategySelectorTest {

    @Test
    fun `selects serial validation for small workloads`() {
        val strategy = selectValidationStrategy(
            functions = functions(32, 32),
            mode = ValidationMode.AUTO,
            availableProcessors = 8,
        )

        assertEquals(ValidationStrategy.Serial, strategy)
    }

    @Test
    fun `selects parallel validation for balanced expensive workloads`() {
        val strategy = selectValidationStrategy(
            functions = functions(10_000, 10_000, 10_000, 10_000, 10_000, 10_000),
            mode = ValidationMode.AUTO,
            availableProcessors = 8,
        )

        val assignments = assertIs<ValidationStrategy.Parallel>(strategy).assignments
        assertEquals(6, assignments.size)
    }

    @Test
    fun `balances expensive functions before cheap functions`() {
        val strategy = selectValidationStrategy(
            functions = functions(1_000, 900, 100, 100),
            mode = ValidationMode.PARALLEL,
            availableProcessors = 3,
        )

        val assignments = assertIs<ValidationStrategy.Parallel>(strategy).assignments
        assertContentEquals(intArrayOf(0, 3), assignments[0])
        assertContentEquals(intArrayOf(1, 2), assignments[1])
    }

    @Test
    fun `selects serial validation when only one worker is available`() {
        val strategy = selectValidationStrategy(
            functions = functions(1_000, 1_000),
            mode = ValidationMode.PARALLEL,
            availableProcessors = 2,
        )

        assertEquals(ValidationStrategy.Serial, strategy)
    }

    private fun functions(vararg instructionCounts: Int) = instructionCounts.mapIndexed { index, count ->
        function(
            idx = io.github.charlietap.chasm.ast.module.Index.FunctionIndex(index.toUInt()),
            body = Expression(List(count) { ControlInstruction.Nop }),
        )
    }
}
