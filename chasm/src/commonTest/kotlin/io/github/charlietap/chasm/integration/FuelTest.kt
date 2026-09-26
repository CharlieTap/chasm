package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.resetFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FuelTest {

    @Test
    fun `an unmetered store runs without fuel`() {
        val (store, instance) = instantiate(meterFuel = false)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(store, instance, 10))
    }

    @Test
    fun `the fuel api returns an error for an unmetered store and leaves its balance unchanged`() {
        val (store, _) = instantiate(meterFuel = false)

        assertIs<ChasmResult.Error<*>>(addFuel(store, 10))
        assertIs<ChasmResult.Error<*>>(resetFuel(store))
        assertIs<ChasmResult.Error<*>>(remainingFuel(store))
        assertEquals(0, store.store.fuel.remaining)
    }

    @Test
    fun `a fresh metered store has no fuel and traps on invocation`() {
        val (store, instance) = instantiate(meterFuel = true)

        assertEquals(ChasmResult.Success(0L), remainingFuel(store))
        assertEquals(trap(InvocationError.FuelExhausted), count(store, instance, 10))
    }

    @Test
    fun `a function entry and each loop iteration spend a unit`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 100)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(store, instance, 10))
        assertEquals(ChasmResult.Success(89L), remainingFuel(store))
    }

    @Test
    fun `an exactly sufficient allowance succeeds and leaves zero`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 11)

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(store, instance, 10))
        assertEquals(ChasmResult.Success(0L), remainingFuel(store))

        addFuel(store, 10)
        assertEquals(trap(InvocationError.FuelExhausted), count(store, instance, 10))
        assertEquals(ChasmResult.Success(0L), remainingFuel(store))
    }

    @Test
    fun `adding fuel preserves the existing balance`() {
        val (store, _) = instantiate(meterFuel = true)

        assertEquals(ChasmResult.Success(Unit), addFuel(store, 40))
        assertEquals(ChasmResult.Success(Unit), addFuel(store, 2))
        assertEquals(ChasmResult.Success(42L), remainingFuel(store))

        assertEquals(ChasmResult.Success(Unit), addFuel(store, 0))
        assertEquals(ChasmResult.Success(42L), remainingFuel(store))
    }

    @Test
    fun `adding fuel saturates at the maximum`() {
        val (store, _) = instantiate(meterFuel = true)

        addFuel(store, 42)
        assertEquals(ChasmResult.Success(Unit), addFuel(store, Long.MAX_VALUE - 42))
        assertEquals(ChasmResult.Success(Long.MAX_VALUE), remainingFuel(store))

        assertEquals(ChasmResult.Success(Unit), addFuel(store, Long.MAX_VALUE))
        assertEquals(ChasmResult.Success(Long.MAX_VALUE), remainingFuel(store))
    }

    @Test
    fun `adding negative fuel returns an error and leaves the balance unchanged`() {
        val (store, _) = instantiate(meterFuel = true)
        addFuel(store, 42)

        assertIs<ChasmResult.Error<*>>(addFuel(store, -1))
        assertEquals(ChasmResult.Success(42L), remainingFuel(store))
    }

    @Test
    fun `resetting fuel empties the balance`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 42)

        assertEquals(ChasmResult.Success(Unit), resetFuel(store))
        assertEquals(ChasmResult.Success(0L), remainingFuel(store))
        assertEquals(trap(InvocationError.FuelExhausted), count(store, instance, 1))
    }

    @Test
    fun `a loop traps once the fuel runs out`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 1_000)

        assertEquals(trap(InvocationError.FuelExhausted), invoke(store, instance, "spin"))
        assertEquals(ChasmResult.Success(0L), remainingFuel(store))
    }

    @Test
    fun `adding fuel after exhaustion lets a finite function complete`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 5)
        assertEquals(trap(InvocationError.FuelExhausted), count(store, instance, 10))

        addFuel(store, 11)
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(store, instance, 10))
        assertEquals(ChasmResult.Success(0L), remainingFuel(store))
    }

    @Test
    fun `recursion traps once the fuel runs out`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 100)

        assertEquals(trap(InvocationError.FuelExhausted), invoke(store, instance, "recurse"))
    }

    @Test
    fun `tail calls and indirect calls trap once the fuel runs out`() {
        val (store, instance) = instantiate(meterFuel = true)
        addFuel(store, 100)
        assertEquals(trap(InvocationError.FuelExhausted), invoke(store, instance, "ping"))

        addFuel(store, 100)
        assertEquals(trap(InvocationError.FuelExhausted), invoke(store, instance, "indirect"))
    }

    @Test
    fun `a looping start function exhausts fuel during instantiation`() {
        val store = store(StoreConfig(meterFuel = true))
        addFuel(store, 1_000)
        val module = module(Resource(FILE_DIR + START_FIXTURE).readBytes()).expect("module decodes")

        assertEquals(trap(InvocationError.FuelExhausted), instance(store, module, emptyList()))
        assertEquals(ChasmResult.Success(0L), remainingFuel(store))
    }

    @Test
    fun `a metered store compiles a check per function and loop and an unmetered store compiles none`() {
        val (metered, _) = instantiate(meterFuel = true)
        val (unmetered, _) = instantiate(meterFuel = false)

        // Six functions and two loops.
        assertEquals(8, metered.store.program.size - unmetered.store.program.size)
    }

    @Test
    fun `a module large enough to compile in parallel is metered throughout`() {
        val functions = 400
        val (metered, instance) = instantiate(meterFuel = true, fileName = PARALLEL_FIXTURE)
        val (unmetered, _) = instantiate(meterFuel = false, fileName = PARALLEL_FIXTURE)

        assertEquals(2 * functions, metered.store.program.size - unmetered.store.program.size)
        addFuel(metered, 1_000)
        assertEquals(trap(InvocationError.FuelExhausted), invoke(metered, instance, "last"))
    }

    private fun count(store: Store, instance: Instance, times: Int) =
        invoke(store, instance, "count", listOf(NumberValue.I32(times)))

    private fun instantiate(meterFuel: Boolean, fileName: String = FUEL_FIXTURE): Pair<Store, Instance> {
        val store = store(StoreConfig(meterFuel = meterFuel))
        val module = module(Resource(FILE_DIR + fileName).readBytes()).expect("module decodes")
        return store to instance(store, module, emptyList()).expect("module instantiates")
    }

    private fun trap(error: InvocationError) = ChasmResult.Error(ChasmError.ExecutionError(error.toString()))

    companion object {
        private const val FILE_DIR = "integration/"
        private const val FUEL_FIXTURE = "fuel.wasm"
        private const val START_FIXTURE = "fuel_start.wasm"
        private const val PARALLEL_FIXTURE = "fuel_parallel.wasm"
    }
}
