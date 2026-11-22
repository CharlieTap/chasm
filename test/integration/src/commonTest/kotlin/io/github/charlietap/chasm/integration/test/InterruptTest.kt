package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.assertTrapped
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class InterruptTest {

    @Test
    fun `interrupting a store that is not interruptible throws`() = integrationTest(INTERRUPT_FIXTURE) {
        val (store, _) = interruptRunner(StoreConfig())

        assertFailsWith<IllegalStateException>(engine.name()) { engine.interrupt(store) }
    }

    @Test
    fun `an interrupt stops the running call at its next check`() = integrationTest(INTERRUPT_FIXTURE) {
        val (_, runner) = interruptRunner(StoreConfig(interruptible = true))

        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("interrupt_then_spin"), engine.name())
    }

    @Test
    fun `a call after an interrupted one runs`() = integrationTest(INTERRUPT_FIXTURE) {
        val (_, runner) = interruptRunner(StoreConfig(interruptible = true))
        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("interrupt_then_spin"), engine.name())

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 3), engine.name())
    }

    @Test
    fun `an interrupt made while no call runs reports it and has no effect`() = integrationTest(INTERRUPT_FIXTURE) {
        val (store, runner) = interruptRunner(StoreConfig(interruptible = true))

        assertEquals(false, engine.interrupt(store), engine.name())
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(runner, 3), engine.name())
    }

    @Test
    fun `an interrupt lands at the next function entry and the next loop iteration`() = integrationTest(INTERRUPT_FIXTURE) {
        val (_, runner) = interruptRunner(StoreConfig(interruptible = true))

        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("interrupt_then_call"), engine.name())
        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("interrupt_then_spin"), engine.name())
    }

    @Test
    fun `a metered interruptible store spends the fuel a metered store does and stops on either`() = integrationTest(INTERRUPT_FIXTURE) {
        val (both, bothRunner) = interruptRunner(StoreConfig(meterFuel = true, interruptible = true))
        val (metered, meteredRunner) = interruptRunner(StoreConfig(meterFuel = true))
        engine.addFuel(both, 4)
        engine.addFuel(metered, 4)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(bothRunner, 3), engine.name())
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(meteredRunner, 3), engine.name())
        assertEquals(engine.remainingFuel(metered), engine.remainingFuel(both), engine.name())
        assertEquals(0L, engine.remainingFuel(both), engine.name())

        engine.addFuel(both, 100)
        assertTrapped(TrapReason.FUEL_EXHAUSTED, bothRunner.invoke("spin"), engine.name())
        engine.addFuel(both, Long.MAX_VALUE)
        assertTrapped(TrapReason.INTERRUPTED, bothRunner.invoke("interrupt_then_call"), engine.name())
    }

    @Test
    fun `a metered interruptible store traps on an interrupt or on running out of fuel`() = integrationTest(INTERRUPT_FIXTURE) {
        val (store, runner) = interruptRunner(StoreConfig(meterFuel = true, interruptible = true))
        engine.addFuel(store, Long.MAX_VALUE)
        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("interrupt_then_spin"), engine.name())

        val (metered, meteredRunner) = interruptRunner(StoreConfig(meterFuel = true, interruptible = true))
        engine.addFuel(metered, 100)
        assertTrapped(TrapReason.FUEL_EXHAUSTED, meteredRunner.invoke("spin"), engine.name())
    }

    @Test
    fun `a metered interruptible store reports an interrupt over running out of fuel`() = integrationTest(INTERRUPT_FIXTURE) {
        val (store, runner) = interruptRunner(StoreConfig(meterFuel = true, interruptible = true))
        // Enough for the interrupting function's entry check, none for its loop's.
        engine.addFuel(store, 1)

        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("interrupt_then_spin"), engine.name())
    }

    @Test
    fun `an interrupt made during a call reports that it landed`() = integrationTest(INTERRUPT_FIXTURE) {
        var interruptResult: Boolean? = null
        val (_, runner) = interruptRunner(StoreConfig(interruptible = true)) { store, _ ->
            interruptResult = engine.interrupt(store)
        }

        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("callback_then_spin"), engine.name())
        assertEquals(true, interruptResult, engine.name())
    }

    @Test
    fun `an interrupt raised in a call back into the store also stops its caller`() = integrationTest(INTERRUPT_FIXTURE) {
        var callbackResult: ChasmResult<*, *>? = null
        val (_, runner) = interruptRunner(StoreConfig(interruptible = true)) { _, runner ->
            callbackResult = runner.invoke("interrupt_then_spin")
        }

        assertTrapped(TrapReason.INTERRUPTED, runner.invoke("callback_then_spin"), engine.name())
        assertTrapped(TrapReason.INTERRUPTED, assertNotNull(callbackResult, engine.name()), engine.name())
    }

    @Test
    fun `an interrupt raised in a host function invoked directly stops its calls back into the store`() = integrationTest(INTERRUPT_FIXTURE) {
        var callbackResult: ChasmResult<*, *>? = null
        val (_, runner) = interruptRunner(StoreConfig(interruptible = true)) { store, runner ->
            engine.interrupt(store)
            callbackResult = count(runner, 3)
        }

        assertEquals(ChasmResult.Success(emptyList()), runner.invoke("callback"), engine.name())
        assertTrapped(TrapReason.INTERRUPTED, assertNotNull(callbackResult, engine.name()), engine.name())
    }

    private fun count(
        runner: TestRunner,
        times: Int,
    ) = runner.invoke("count", listOf(NumberValue.I32(times)))
}

internal const val INTERRUPT_FIXTURE = "interrupt"

internal fun TestScope.interruptRunner(
    config: StoreConfig,
    keepRunning: () -> Boolean = { false },
    callback: (Store, TestRunner) -> Unit = { _, _ -> },
): Pair<Store, TestRunner> {
    val store = store(config)
    lateinit var runner: TestRunner
    val hostInterrupt = function(store, functionType(), HostFunction { _, _ -> engine.interrupt(store) })
    val hostCallback = function(store, functionType(), HostFunction { _, _ -> callback(store, runner) })
    val hostKeepRunning = function(
        store,
        functionType(results = resultType(listOf(i32ValueType()))),
        HostFunction { _, results -> results.writeI32(0, if (keepRunning()) 1 else 0) },
    )
    val imports = listOf(
        Import("env", "interrupt", hostInterrupt),
        Import("env", "callback", hostCallback),
        Import("env", "keep_running", hostKeepRunning),
    )
    runner = runner(store, imports)
    return store to runner
}
