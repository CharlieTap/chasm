package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.assertTrapped
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals

// The JVM rejects an array of Int.MAX_VALUE elements without allocating it,
// so these tests reach the platform allocation failure cheaply.
class StoreLimitsJvmTest {

    @Test
    fun `table grow returns minus one when the platform cannot allocate the table`() = integrationTest(FIXTURE) {
        val runner = runner(RuntimeConfig())

        assertEquals(success(-1), grow(runner, Int.MAX_VALUE - 1), engine.name())
        assertEquals(success(1), grow(runner, 0), engine.name())
    }

    @Test
    fun `table grow traps when the platform cannot allocate the table and grow failures trap`() = integrationTest(FIXTURE) {
        val runner = runner(RuntimeConfig(limits = Limits(memory = MemoryLimits(onGrowthFailure = GrowthFailureStrategy.Trap))))

        assertTrapped(TrapReason.TABLE_GROW_FAILED, grow(runner, Int.MAX_VALUE - 1), engine.name())
        assertEquals(success(1), grow(runner, 0), engine.name())
    }

    private fun grow(
        runner: TestRunner,
        elements: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke("table-grow", listOf(NumberValue.I32(elements)))
    }

    private fun success(value: Int): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return ChasmResult.Success(listOf(NumberValue.I32(value)))
    }

    private companion object {
        const val FIXTURE = "store_limits"
    }
}
