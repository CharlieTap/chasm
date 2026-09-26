package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.interrupt
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InterruptTest {

    @Test
    fun `interrupting a store that is not interruptible returns an error`() {
        val (store, _) = instantiate(StoreConfig())

        assertIs<ChasmResult.Error<*>>(interrupt(store))
    }

    @Test
    fun `an interrupt stops the running call at its next check`() {
        val (store, instance) = instantiate(StoreConfig(interruptible = true))

        assertEquals(interrupted(), invoke(store, instance, "interrupt_then_spin"))
    }

    @Test
    fun `a call after an interrupted one runs`() {
        val (store, instance) = instantiate(StoreConfig(interruptible = true))
        assertEquals(interrupted(), invoke(store, instance, "interrupt_then_spin"))

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(store, instance, 3))
    }

    @Test
    fun `an interrupt made while no call runs has no effect`() {
        val (store, instance) = instantiate(StoreConfig(interruptible = true))

        assertEquals(ChasmResult.Success(Unit), interrupt(store))
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), count(store, instance, 3))
    }

    @Test
    fun `an interruptible store compiles a check per function and loop, a plain one none`() {
        val (interruptible, _) = instantiate(StoreConfig(interruptible = true))
        val (plain, _) = instantiate(StoreConfig())

        // Four functions and four loops.
        assertEquals(8, interruptible.store.program.size - plain.store.program.size)
    }

    @Test
    fun `a metered interruptible store compiles one combined check per function and loop`() {
        val (both, _) = instantiate(StoreConfig(meterFuel = true, interruptible = true))
        val (plain, _) = instantiate(StoreConfig())

        assertEquals(8, both.store.program.size - plain.store.program.size)
    }

    @Test
    fun `a metered interruptible store traps on an interrupt or on running out of fuel`() {
        val (store, instance) = instantiate(StoreConfig(meterFuel = true, interruptible = true))
        addFuel(store, Long.MAX_VALUE)
        assertEquals(interrupted(), invoke(store, instance, "interrupt_then_spin"))

        val (metered, meteredInstance) = instantiate(StoreConfig(meterFuel = true, interruptible = true))
        addFuel(metered, 100)
        assertEquals(
            ChasmResult.Error(ChasmError.ExecutionError(InvocationError.FuelExhausted.toString())),
            invoke(metered, meteredInstance, "spin"),
        )
    }

    @Test
    fun `an interrupt raised in a call back into the store also stops its caller`() {
        var callbackResult: ChasmResult<*, *>? = null
        val (store, instance) = instantiate(StoreConfig(interruptible = true)) { store, instance ->
            callbackResult = invoke(store, instance, "interrupt_then_spin")
        }

        assertEquals(interrupted(), invoke(store, instance, "callback_then_spin"))
        assertEquals(interrupted(), callbackResult)
    }

    private fun count(store: Store, instance: Instance, times: Int) =
        invoke(store, instance, "count", listOf(NumberValue.I32(times)))

    private fun interrupted() = ChasmResult.Error(ChasmError.ExecutionError(InvocationError.Interrupted.toString()))

    companion object {
        private const val FIXTURE = "integration/interrupt.wasm"

        /**
         * Instantiates the fixture. Its `env.interrupt` import interrupts the store it runs on, and its
         * `env.callback` import runs [callback] with the store and the instance being created.
         */
        internal fun instantiate(
            config: StoreConfig,
            callback: (Store, Instance) -> Unit = { _, _ -> },
        ): Pair<Store, Instance> {
            val store = store(config)
            lateinit var instance: Instance
            val hostInterrupt = function(store, functionType(), HostFunction { _, _ -> interrupt(store) })
            val hostCallback = function(store, functionType(), HostFunction { _, _ -> callback(store, instance) })
            val module = module(Resource(FIXTURE).readBytes()).expect("module decodes")
            val imports = listOf(
                publicImport("env", "interrupt", hostInterrupt),
                publicImport("env", "callback", hostCallback),
            )
            instance = instance(store, module, imports).expect("module instantiates")
            return store to instance
        }
    }
}
