package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.resetFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.assertTrapped
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FuelTest {

    @Test
    fun `an unmetered store runs without fuel`() = integrationTest(FIXTURE) {
        val (_, runner) = instantiate(meterFuel = false)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
    }

    @Test
    fun `the fuel api throws for an unmetered store`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = false)

        assertFailsWith<IllegalStateException>(engine.name()) { addFuel(store, 10) }
        assertFailsWith<IllegalStateException>(engine.name()) { resetFuel(store) }
        assertFailsWith<IllegalStateException>(engine.name()) { remainingFuel(store) }
    }

    @Test
    fun `a fresh metered store has no fuel and traps on invocation`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)

        assertEquals(0L, remainingFuel(store), engine.name())
        assertTrapped(TrapReason.FUEL_EXHAUSTED, count(runner, 10), engine.name())
    }

    @Test
    fun `a function entry and each loop iteration spend a unit`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 100)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
        assertEquals(89L, remainingFuel(store), engine.name())
    }

    @Test
    fun `an exactly sufficient allowance succeeds and leaves zero`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 11)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
        assertEquals(0L, remainingFuel(store), engine.name())

        addFuel(store, 10)
        assertTrapped(TrapReason.FUEL_EXHAUSTED, count(runner, 10), engine.name())
        assertEquals(0L, remainingFuel(store), engine.name())
    }

    @Test
    fun `adding fuel preserves the existing balance`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = true)

        addFuel(store, 40)
        addFuel(store, 2)
        assertEquals(42L, remainingFuel(store), engine.name())

        addFuel(store, 0)
        assertEquals(42L, remainingFuel(store), engine.name())
    }

    @Test
    fun `adding fuel saturates at the maximum`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = true)

        addFuel(store, 42)
        addFuel(store, Long.MAX_VALUE - 42)
        assertEquals(Long.MAX_VALUE, remainingFuel(store), engine.name())

        addFuel(store, Long.MAX_VALUE)
        assertEquals(Long.MAX_VALUE, remainingFuel(store), engine.name())
    }

    @Test
    fun `adding negative fuel throws and leaves the balance unchanged`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = true)
        addFuel(store, 42)

        assertFailsWith<IllegalArgumentException>(engine.name()) { addFuel(store, -1) }
        assertEquals(42L, remainingFuel(store), engine.name())
    }

    @Test
    fun `resetting fuel empties the balance`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 42)

        resetFuel(store)
        assertEquals(0L, remainingFuel(store), engine.name())
        assertTrapped(TrapReason.FUEL_EXHAUSTED, count(runner, 1), engine.name())
    }

    @Test
    fun `a loop traps once the fuel runs out`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 1_000)

        assertTrapped(TrapReason.FUEL_EXHAUSTED, runner.invoke("spin"), engine.name())
        assertEquals(0L, remainingFuel(store), engine.name())
    }

    @Test
    fun `adding fuel after exhaustion lets a finite function complete`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 5)
        assertTrapped(TrapReason.FUEL_EXHAUSTED, count(runner, 10), engine.name())

        addFuel(store, 11)
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
        assertEquals(0L, remainingFuel(store), engine.name())
    }

    @Test
    fun `recursion traps once the fuel runs out`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 100)

        assertTrapped(TrapReason.FUEL_EXHAUSTED, runner.invoke("recurse"), engine.name())
    }

    @Test
    fun `tail calls and indirect calls trap once the fuel runs out`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 100)
        assertTrapped(TrapReason.FUEL_EXHAUSTED, runner.invoke("ping"), engine.name())

        addFuel(store, 100)
        assertTrapped(TrapReason.FUEL_EXHAUSTED, runner.invoke("indirect"), engine.name())
    }

    @Test
    fun `a looping start function exhausts fuel during instantiation`() = integrationTest(START_FIXTURE) {
        val runner = runner(METERED) { store ->
            addFuel(store, 1_000)
            []
        }

        assertTrapped(TrapReason.FUEL_EXHAUSTED, runner.instance(), engine.name())
        assertEquals(0L, remainingFuel(runner.store), engine.name())
    }

    @Test
    fun `every function entry and loop iteration spends exactly one unit and an unmetered store needs none`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        listOf(1, 2, 5, 10).forEach { times ->
            addFuel(store, times + 1L)

            assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, times), "${engine.name()} count($times)")
            assertEquals(0L, remainingFuel(store), "${engine.name()} count($times)")
        }

        val (_, plain) = instantiate(meterFuel = false)
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(plain, 10), engine.name())
    }

    @Test
    fun `a module large enough to compile in parallel is metered throughout`() = integrationTest(PARALLEL_FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        addFuel(store, 2L * PARALLEL_FUNCTIONS)

        repeat(PARALLEL_FUNCTIONS) { function ->
            assertEquals(ChasmResult.Success(emptyList()), runner.invoke("f$function"), "${engine.name()} f$function")
        }

        assertEquals(0L, remainingFuel(store), engine.name())
        assertTrapped(TrapReason.FUEL_EXHAUSTED, runner.invoke("f0"), engine.name())
    }

    private suspend fun TestScope.instantiate(meterFuel: Boolean): Pair<Store, TestRunner> {

        val runner = runner(if (meterFuel) METERED else RuntimeConfig())

        return runner.store to runner
    }

    private fun count(
        runner: TestRunner,
        times: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke("count", listOf(NumberValue.I32(times)))
    }

    private companion object {
        const val FIXTURE = "fuel"
        const val START_FIXTURE = "fuel_start"
        const val PARALLEL_FIXTURE = "fuel_parallel"
        const val PARALLEL_FUNCTIONS = 400
        val METERED = RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(0.fuel)))
    }
}
