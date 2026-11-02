@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FuelTest {

    @Test
    fun `an unmetered store runs without fuel`() = integrationTest(FIXTURE) {
        val (_, runner) = instantiate(meterFuel = false)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
    }

    @Test
    fun `the fuel api returns an error for an unmetered store and leaves its balance unchanged`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = false)

        assertIs<ChasmResult.Error<*>>(engine.addFuel(store, 10), engine.name())
        assertIs<ChasmResult.Error<*>>(engine.resetFuel(store), engine.name())
        assertIs<ChasmResult.Error<*>>(engine.remainingFuel(store), engine.name())
        assertEquals(0, store.store.fuel.remaining, engine.name())
    }

    @Test
    fun `a fresh metered store has no fuel and traps on invocation`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)

        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
        assertEquals(trap(InvocationError.FuelExhausted), count(runner, 10), engine.name())
    }

    @Test
    fun `a function entry and each loop iteration spend a unit`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 100)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
        assertEquals(ChasmResult.Success(89L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `an exactly sufficient allowance succeeds and leaves zero`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 11)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())

        engine.addFuel(store, 10)
        assertEquals(trap(InvocationError.FuelExhausted), count(runner, 10), engine.name())
        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `adding fuel preserves the existing balance`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = true)

        assertEquals(ChasmResult.Success(Unit), engine.addFuel(store, 40), engine.name())
        assertEquals(ChasmResult.Success(Unit), engine.addFuel(store, 2), engine.name())
        assertEquals(ChasmResult.Success(42L), engine.remainingFuel(store), engine.name())

        assertEquals(ChasmResult.Success(Unit), engine.addFuel(store, 0), engine.name())
        assertEquals(ChasmResult.Success(42L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `adding fuel saturates at the maximum`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = true)

        engine.addFuel(store, 42)
        assertEquals(ChasmResult.Success(Unit), engine.addFuel(store, Long.MAX_VALUE - 42), engine.name())
        assertEquals(ChasmResult.Success(Long.MAX_VALUE), engine.remainingFuel(store), engine.name())

        assertEquals(ChasmResult.Success(Unit), engine.addFuel(store, Long.MAX_VALUE), engine.name())
        assertEquals(ChasmResult.Success(Long.MAX_VALUE), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `adding negative fuel returns an error and leaves the balance unchanged`() = integrationTest(FIXTURE) {
        val (store, _) = instantiate(meterFuel = true)
        engine.addFuel(store, 42)

        assertIs<ChasmResult.Error<*>>(engine.addFuel(store, -1), engine.name())
        assertEquals(ChasmResult.Success(42L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `resetting fuel empties the balance`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 42)

        assertEquals(ChasmResult.Success(Unit), engine.resetFuel(store), engine.name())
        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
        assertEquals(trap(InvocationError.FuelExhausted), count(runner, 1), engine.name())
    }

    @Test
    fun `a loop traps once the fuel runs out`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 1_000)

        assertEquals(trap(InvocationError.FuelExhausted), runner.invoke("spin"), engine.name())
        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `adding fuel after exhaustion lets a finite function complete`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 5)
        assertEquals(trap(InvocationError.FuelExhausted), count(runner, 10), engine.name())

        engine.addFuel(store, 11)
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 10), engine.name())
        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `recursion traps once the fuel runs out`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 100)

        assertEquals(trap(InvocationError.FuelExhausted), runner.invoke("recurse"), engine.name())
    }

    @Test
    fun `tail calls and indirect calls trap once the fuel runs out`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 100)
        assertEquals(trap(InvocationError.FuelExhausted), runner.invoke("ping"), engine.name())

        engine.addFuel(store, 100)
        assertEquals(trap(InvocationError.FuelExhausted), runner.invoke("indirect"), engine.name())
    }

    @Test
    fun `a looping start function exhausts fuel during instantiation`() = integrationTest(START_FIXTURE) {
        val store = store(StoreConfig(meterFuel = true))
        engine.addFuel(store, 1_000)

        val runner = runner(store)

        assertEquals(trap(InvocationError.FuelExhausted), runner.instance(), engine.name())
        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
    }

    @Test
    fun `every function entry and loop iteration spends exactly one unit and an unmetered store spends none`() = integrationTest(FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        listOf(1, 2, 5, 10).forEach { times ->
            engine.addFuel(store, times + 1L)

            assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, times), "${engine.name()} count($times)")
            assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), "${engine.name()} count($times)")
        }

        val (unmetered, plain) = instantiate(meterFuel = false)
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(plain, 10), engine.name())
        assertEquals(0, unmetered.store.fuel.remaining, engine.name())
    }

    @Test
    fun `a module large enough to compile in parallel is metered throughout`() = integrationTest(PARALLEL_FIXTURE) {
        val (store, runner) = instantiate(meterFuel = true)
        engine.addFuel(store, 2L * PARALLEL_FUNCTIONS)

        repeat(PARALLEL_FUNCTIONS) { function ->
            assertEquals(ChasmResult.Success(emptyList()), runner.invoke("f$function"), "${engine.name()} f$function")
        }

        assertEquals(ChasmResult.Success(0L), engine.remainingFuel(store), engine.name())
        assertEquals(trap(InvocationError.FuelExhausted), runner.invoke("f0"), engine.name())
    }

    private fun TestScope.instantiate(
        meterFuel: Boolean,
    ): Pair<Store, TestRunner> {
        val store = store(StoreConfig(meterFuel = meterFuel))
        return store to runner(store)
    }

    private fun count(
        runner: TestRunner,
        times: Int,
    ) = runner.invoke("count", listOf(NumberValue.I32(times)))

    private fun trap(
        error: InvocationError,
    ) = ChasmResult.Error(ChasmError.ExecutionError(error.toString()))

    private companion object {
        const val FIXTURE = "fuel"
        const val START_FIXTURE = "fuel_start"
        const val PARALLEL_FIXTURE = "fuel_parallel"
        const val PARALLEL_FUNCTIONS = 400
    }
}
