package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

// The JVM rejects an array of Int.MAX_VALUE elements without allocating it,
// so these tests reach the platform allocation failure cheaply.
class StoreLimitsJvmTest {

    @Test
    fun `table grow returns minus one when the platform cannot allocate the table`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig()))

        assertEquals(success(-1), grow(runner, Int.MAX_VALUE - 1), engine.name())
        assertEquals(success(1), grow(runner, 0), engine.name())
    }

    @Test
    fun `table grow traps when the platform cannot allocate the table and grow failures trap`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(limits = StoreLimits(trapOnGrowFailure = true))))

        assertEquals(
            ChasmResult.Error(ChasmError.ExecutionError(InvocationError.TableGrowFailed.toString())),
            grow(runner, Int.MAX_VALUE - 1),
            engine.name(),
        )
        assertEquals(success(1), grow(runner, 0), engine.name())
    }

    private fun grow(
        runner: TestRunner,
        elements: Int,
    ) = runner.invoke("table-grow", listOf(NumberValue.I32(elements)))

    private fun success(
        value: Int,
    ) = ChasmResult.Success(listOf(NumberValue.I32(value)))

    private companion object {
        const val FIXTURE = "store_limits"
    }
}
