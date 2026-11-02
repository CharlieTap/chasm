package io.github.charlietap.chasm.integration.test

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.integration.AotEngine
import io.github.charlietap.chasm.integration.Engine
import io.github.charlietap.chasm.integration.Interpreter
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class MixedEngineStoreTest {

    @Test
    fun `a call across engines spends fuel in both from the one tank`() {
        pairs.forEach { (owner, caller) ->
            val store = store(StoreConfig(meterFuel = true))
            val ownerInstance = owner.instantiate(bytes(FUEL_OWNER), store, emptyList(), RuntimeConfig()).expect("owner instantiates")
            val count = owner.exports(ownerInstance).single { export -> export.name == "count" }.value
            val callerInstance = caller.instantiate(bytes(FUEL_CALLER), store, listOf(Import("owner", "count", count)), RuntimeConfig())
                .expect("caller instantiates")
            caller.addFuel(store, 100)

            val result = caller.invoke(store, callerInstance, "call_count", listOf(NumberValue.I32(10)))

            assertEquals(ChasmResult.Success(listOf(NumberValue.I32(0))), result, label(owner, caller))
            assertEquals(ChasmResult.Success(88L), caller.remainingFuel(store), label(owner, caller))
        }
    }

    @Test
    fun `an interrupt raised in one engine stops a loop running in the other`() {
        pairs.forEach { (owner, caller) ->
            val store = store(StoreConfig(interruptible = true))
            val ownerInstance = owner.instantiate(bytes(INTERRUPT_FIXTURE), store, interruptImports(store, owner), RuntimeConfig())
                .expect("owner instantiates")
            val spin = owner.exports(ownerInstance).single { export -> export.name == "spin" }.value
            val imports = listOf(
                Import("env", "interrupt", function(store, functionType(), HostFunction { _, _ -> caller.interrupt(store) })),
                Import("owner", "spin", spin),
            )
            val callerInstance = caller.instantiate(bytes(INTERRUPT_CALLER), store, imports, RuntimeConfig()).expect("caller instantiates")

            val result = caller.invoke(store, callerInstance, "interrupt_then_owner_spin", emptyList())

            assertEquals(interrupted(), result, label(owner, caller))
        }
    }

    private fun interruptImports(
        store: Store,
        engine: Engine,
    ): List<Import> = listOf(
        Import("env", "interrupt", function(store, functionType(), HostFunction { _, _ -> engine.interrupt(store) })),
        Import("env", "callback", function(store, functionType(), HostFunction { _, _ -> })),
        Import(
            "env",
            "keep_running",
            function(store, functionType(results = resultType(listOf(i32ValueType()))), HostFunction { _, results -> results.writeI32(0, 0) }),
        ),
    )

    private fun label(
        owner: Engine,
        caller: Engine,
    ) = "${caller.name()} calling ${owner.name()}"

    private fun bytes(
        fixture: String,
    ): ByteArray = Resource("integration/$fixture.wasm").readBytes()

    private companion object {
        const val FUEL_OWNER = "fuel"
        const val FUEL_CALLER = "fuel_mixed"
        const val INTERRUPT_CALLER = "interrupt_mixed"

        val pairs: List<Pair<Engine, Engine>> = listOf(
            Interpreter() to AotEngine(),
            AotEngine() to Interpreter(),
        )
    }
}
