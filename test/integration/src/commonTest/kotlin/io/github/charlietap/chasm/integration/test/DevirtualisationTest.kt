package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DevirtualisationTest {

    @Test
    fun `each function in a closed table is called through its index`() = integrationTest(FIXTURE) {
        val runner = runner()

        assertEquals(result(9), dispatch(runner, selector = 0), engine.name())
        assertEquals(result(3), dispatch(runner, selector = 1), engine.name())
        assertEquals(result(18), dispatch(runner, selector = 2), engine.name())
    }

    @Test
    fun `a constant index calls its function directly`() = integrationTest(FIXTURE) {
        assertEquals(result(42), runner().invoke("dispatch-constant"), engine.name())
    }

    @Test
    fun `an entry of the wrong type or a null or out of bounds entry traps through the indirect call`() = integrationTest(FIXTURE) {
        val runner = runner()

        listOf(3, 4, 6, -1).forEach { selector ->
            assertIs<ChasmResult.Error<*>>(dispatch(runner, selector), "${engine.name()} selector $selector")
        }
    }

    @Test
    fun `a call result can replace the local that selected it`() = integrationTest(FIXTURE) {
        val runner = runner()

        assertEquals(result(11), runner.invoke("reuse", listOf(NumberValue.I32(0), NumberValue.I32(10))), engine.name())
        assertEquals(result(10), runner.invoke("reuse", listOf(NumberValue.I32(1), NumberValue.I32(10))), engine.name())
    }

    @Test
    fun `an exception thrown by a devirtualised callee reaches the handler in its caller`() = integrationTest(FIXTURE) {
        val arguments = listOf(NumberValue.I32(0), NumberValue.I32(2), NumberValue.I32(3))

        assertEquals(result(5), runner().invoke("caught", arguments), engine.name())
    }

    @Test
    fun `a reference held across allocating devirtualised calls survives collection`() = integrationTest(FIXTURE) {
        val runner = runner(RuntimeConfig(limits = Limits(memory = MemoryLimits(gcHeap = Bytes(ALLOCATIONS * ALLOCATION_BYTES / 2L)))))

        val retained = runner.invoke("retained", listOf(NumberValue.I32(1), NumberValue.I32(ALLOCATIONS)))

        assertEquals(result(ALLOCATIONS * 3 + 7), retained, engine.name())
    }

    private fun dispatch(
        runner: TestRunner,
        selector: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke("dispatch", listOf(NumberValue.I32(selector), NumberValue.I32(6), NumberValue.I32(3)))
    }

    private fun result(value: Int): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return ChasmResult.Success(listOf(NumberValue.I32(value)))
    }

    private companion object {
        const val FIXTURE = "devirtualisation"
        const val ALLOCATIONS = 2_000
        const val ALLOCATION_BYTES = 4_096
    }
}
