package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Module
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.HostModuleInstance
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.payloadSize
import io.github.charlietap.chasm.embedding.compiled.readPayload
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.reference.ExceptionReference
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class InstantiationResultsTest {

    @Test
    fun missingImportsAreUnlinkable() {
        assertIs<ExecutionError.Unlinkable>(failure(store(), "instantiation_unlinkable"))
    }

    @Test
    fun aTrappingStartFunctionTraps() {

        val error = assertIs<ExecutionError.Trapped>(failure(store(), "instantiation_start_trap"))

        assertEquals(TrapReason.UNREACHABLE, error.trap.reason)
    }

    @Test
    fun aThrowingStartFunctionHandsOverTheException() {

        val error = assertIs<ExecutionError.Thrown<ExceptionReference>>(failure(store(), "instantiation_start_throw"))

        assertEquals(7L, readPayload(error.exception, 0))
        assertEquals(1, payloadSize(error.exception))
    }

    @Test
    fun tablesOverTheStoreLimitTrapWithTheLimitReason() {

        val store = store(StoreConfig(limits = StoreLimits(tableElements = 16)))

        val error = assertIs<ExecutionError.Trapped>(failure(store, "instantiation_table_limit"))

        assertEquals(TrapReason.TABLE_LIMIT_EXCEEDED, error.trap.reason)
    }

    @Test
    fun memoriesOverTheStoreLimitTrapWithTheLimitReason() {

        val store = store(StoreConfig(limits = StoreLimits(memoryPages = 16)))

        val error = assertIs<ExecutionError.Trapped>(failure(store, "instantiation_memory_limit"))

        assertEquals(TrapReason.MEMORY_LIMIT_EXCEEDED, error.trap.reason)
    }

    @Test
    fun importsFromAnotherStoreAreRejected() {

        val store = store()
        val foreign = function(store(), FunctionTypeBuilder().build(), HostFunction { _, _ -> })
        val module = load("instantiation_start_reentry")

        assertFailsWith<IllegalArgumentException> {
            runBlocking { instance(store, module, listOf(Import("host", "during_start", foreign))) }
        }
    }

    @Test
    fun hostFunctionsCalledByTheStartFunctionSeeAUsableInstance() {

        val store = store()
        var seen: List<ExecutionValue> = emptyList()
        val duringStart = function(
            store,
            FunctionTypeBuilder().build(),
            HostFunction { _, _ ->
                val module = contextOf<HostModuleInstance>()
                seen = invoke(store, module.instance, "value").expect("value during start")
            },
        )

        val instance = instantiate(store, "instantiation_start_reentry", listOf(Import("host", "during_start", duringStart)))

        assertEquals(listOf<ExecutionValue>(I32(3)), seen)
        assertEquals(listOf<ExecutionValue>(I32(3)), invoke(store, instance, "value").expect("value"))
    }

    private fun failure(
        store: Store,
        name: String,
    ): InstantiationError<ExceptionReference> {

        val result = runBlocking { instance(store, load(name), emptyList()) }

        return assertIs<ChasmResult.Error<InstantiationError<ExceptionReference>>>(result).error
    }

    private fun instantiate(
        store: Store,
        name: String,
        imports: List<Import>,
    ): Instance {
        return runBlocking { instance(store, load(name), imports) }.expect(name)
    }

    private fun load(name: String): Module {
        return runBlocking { module(resourceBytes("modules/$name.wasm")) }.expect(name)
    }
}
