package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.reference.payloadSize
import io.github.charlietap.chasm.embedding.reference.readPayload
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.createException
import io.github.charlietap.chasm.host.raise
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.readReference
import io.github.charlietap.chasm.host.withTag
import io.github.charlietap.chasm.host.writeReference
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

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

        val error = assertIs<ExecutionError.Thrown>(failure(store(), "instantiation_start_throw"))

        assertEquals(7L, readPayload(error.exception, 0))
        assertEquals(1, payloadSize(error.exception))
    }

    @Test
    fun tablesOverTheStoreLimitTrapWithTheLimitReason() {

        val store = store(RuntimeConfig(limits = Limits(memory = MemoryLimits(tables = 128.bytes))))

        val error = assertIs<ExecutionError.Trapped>(failure(store, "instantiation_table_limit"))

        assertEquals(TrapReason.TABLE_LIMIT_EXCEEDED, error.trap.reason)
    }

    @Test
    fun memoriesOverTheStoreLimitTrapWithTheLimitReason() {

        val store = store(RuntimeConfig(limits = Limits(memory = MemoryLimits(linearMemory = 16.pages))))

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
                seen = invoke(module.instance, "value").expect("value during start")
            },
        )

        val instance = instantiate(store, "instantiation_start_reentry", listOf(Import("host", "during_start", duringStart)))

        assertEquals(listOf<ExecutionValue>(I32(3)), seen)
        assertEquals(listOf<ExecutionValue>(I32(3)), invoke(instance, "value").expect("value"))
    }

    @Test
    fun aStartFunctionThatIsAHostImportRunsDuringInstantiation() {

        val store = store()
        var calls = 0
        val start = function(store, FunctionTypeBuilder().build(), HostFunction { _, _ -> calls++ })

        instantiate(store, "instantiation_start_import", listOf(Import("host", "start", start)))

        assertEquals(1, calls)
    }

    @Test
    fun aStartFunctionImportedFromAnotherInstanceRuns() {

        val store = store()
        val exporter = exports(instantiate(store, "instantiation_start_exporter", emptyList()))
        val bump = Import("other", "bump", checkNotNull(exporter.function("bump")))

        instantiate(store, "instantiation_start_imported", listOf(bump))

        assertEquals(1L, readGlobal(checkNotNull(exporter.global("count"))))
    }

    @Test
    fun aStartFunctionPassesReferencesThroughHostImports() {

        val store = store()
        val made = Any()
        var echoed: Any? = null
        val make = function(
            store,
            FunctionTypeBuilder().apply { results { externref() } }.build(),
            HostFunction { _, results -> results.writeReference(0, made) },
        )
        val echo = function(
            store,
            FunctionTypeBuilder().apply {
                params { externref() }
                results { externref() }
            }.build(),
            HostFunction { parameters, results ->
                echoed = parameters.readReference(0)
                results.writeReference(0, echoed)
            },
        )

        val instance = instantiate(store, "instantiation_start_references", listOf(Import("host", "make", make), Import("host", "echo", echo)))

        assertSame(made, echoed)
        assertSame(made, readGlobalReference(checkNotNull(exports(instance).global("kept"))))
    }

    @Test
    fun aHostRaiseDuringStartIsCaughtByTheGuest() {

        val store = store()

        val instance = instantiate(store, "instantiation_start_raise", raiseImports(store))

        assertEquals(40L, readGlobal(checkNotNull(exports(instance).global("caught"))))
    }

    @Test
    fun anUncaughtHostRaiseDuringStartHandsOverTheException() {

        val store = store()

        val result = runBlocking { instance(store, load("instantiation_start_raise_uncaught"), raiseImports(store)) }
        val error = assertIs<ExecutionError.Thrown>(assertIs<ChasmResult.Error<InstantiationError>>(result).error)

        assertEquals(50L, readPayload(error.exception, 0))
    }

    @Test
    fun aTrappingStartFunctionReportsFramesWithDebugInfo() {

        val result = runBlocking { instance(store(RuntimeConfig(stacktraces = true)), load("instantiation_start_trap"), emptyList()) }
        val error = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<InstantiationError>>(result).error)

        val frames = checkNotNull(error.trap.stackTrace).frames
        assertTrue(frames.isNotEmpty(), frames.toString())
    }

    private fun raiseImports(store: Store): List<Import> {

        val tags = instantiate(store, "host_exceptions_tags", emptyList())
        val raise = function(
            store,
            FunctionTypeBuilder().apply { params { i32() } }.build(),
            HostFunction { parameters, _ ->
                val words = longArrayOf(parameters.readI32(0) * PAYLOAD_SCALE)
                withTag(0) {
                    raise(createException(words, arrayOfNulls(1)))
                }
            },
        )

        return listOf(Import("tags", "boom", checkNotNull(exports(tags).tag("boom"))), Import("host", "raise", raise))
    }

    private fun failure(
        store: Store,
        name: String,
    ): InstantiationError {

        val result = runBlocking { instance(store, load(name), emptyList()) }

        return assertIs<ChasmResult.Error<InstantiationError>>(result).error
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

    private companion object {
        const val PAYLOAD_SCALE = 10L
    }
}
