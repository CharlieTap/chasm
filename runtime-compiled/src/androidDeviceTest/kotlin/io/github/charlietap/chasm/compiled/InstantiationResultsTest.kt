package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.dex.CompileConfig
import io.github.charlietap.chasm.dex.CompileError
import io.github.charlietap.chasm.dex.CompileError.Stage
import io.github.charlietap.chasm.dex.DexOutput
import io.github.charlietap.chasm.dex.compile
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
        assertIs<ExecutionError.Unlinkable>(failure(dexModule("instantiation_unlinkable")))
    }

    @Test
    fun aTrappingStartFunctionTraps() {

        val error = assertIs<ExecutionError.Trapped>(failure(dexModule("instantiation_start_trap")))

        assertEquals(TrapReason.UNREACHABLE, error.trap.reason)
    }

    @Test
    fun aThrowingStartFunctionHandsOverTheException() {

        val error = assertIs<ExecutionError.Thrown>(failure(dexModule("instantiation_start_throw")))

        assertEquals(7L, readPayload(error.exception, 0))
        assertEquals(1, payloadSize(error.exception))
    }

    @Test
    fun tablesOverTheCompiledLimitFailToCompile() {

        val config = RuntimeConfig(limits = Limits(memory = MemoryLimits(tables = 128.bytes)))

        val result = compileFixture("instantiation_table_limit", config)

        assertEquals(CompileError("table 0 starts at 64 elements, above the limit of 128 bytes", Stage.CONFIGURATION), result.error)
    }

    @Test
    fun memoriesOverTheCompiledLimitFailToCompile() {

        val config = RuntimeConfig(limits = Limits(memory = MemoryLimits(linearMemory = 16.pages)))

        val result = compileFixture("instantiation_memory_limit", config)

        assertEquals(CompileError("memory 0 starts at 64 pages, above the limit of 1 MB", Stage.CONFIGURATION), result.error)
    }

    private fun compileFixture(
        name: String,
        config: RuntimeConfig,
    ): ChasmResult.Error<CompileError> {

        val output = DexOutput.memory()
        val result = runBlocking { compile(resourceBytes("modules/$name.wasm"), output, CompileConfig(dexClassName(), config)) }

        assertTrue(output.dexFiles.isEmpty())
        return assertIs<ChasmResult.Error<CompileError>>(result)
    }

    @Test
    fun importsFromAnotherStoreAreRejected() {

        val module = dexModule("instantiation_start_reentry")
        val store = store(module)
        val foreign = function(dexStore(), FunctionTypeBuilder().build(), HostFunction { _, _ -> })

        assertFailsWith<IllegalArgumentException> {
            runBlocking { instance(store, module, listOf(Import("host", "during_start", foreign))) }
        }
    }

    @Test
    fun hostFunctionsCalledByTheStartFunctionSeeAUsableInstance() {

        val module = dexModule("instantiation_start_reentry")
        val store = store(module)
        var seen: List<ExecutionValue> = emptyList()
        val duringStart = function(
            store,
            FunctionTypeBuilder().build(),
            HostFunction { _, _ ->
                val hostModule = contextOf<HostModuleInstance>()
                seen = invoke(hostModule.instance, "value").expect("value during start")
            },
        )

        val instance = instantiate(store, module, listOf(Import("host", "during_start", duringStart)))

        assertEquals(listOf<ExecutionValue>(I32(3)), seen)
        assertEquals(listOf<ExecutionValue>(I32(3)), invoke(instance, "value").expect("value"))
    }

    @Test
    fun aStartFunctionThatIsAHostImportRunsDuringInstantiation() {

        val module = dexModule("instantiation_start_import")
        val store = store(module)
        var calls = 0
        val start = function(store, FunctionTypeBuilder().build(), HostFunction { _, _ -> calls++ })

        instantiate(store, module, listOf(Import("host", "start", start)))

        assertEquals(1, calls)
    }

    @Test
    fun aStartFunctionImportedFromAnotherInstanceRuns() {

        val module = dexModule("instantiation_start_exporter")
        val store = store(module)
        val exporter = exports(instantiate(store, module, emptyList()))
        val bump = Import("other", "bump", checkNotNull(exporter.function("bump")))

        instantiate(store, dexModule("instantiation_start_imported"), listOf(bump))

        assertEquals(1L, readGlobal(checkNotNull(exporter.global("count"))))
    }

    @Test
    fun aStartFunctionPassesReferencesThroughHostImports() {

        val module = dexModule("instantiation_start_references")
        val store = store(module)
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

        val instance = instantiate(store, module, listOf(Import("host", "make", make), Import("host", "echo", echo)))

        assertSame(made, echoed)
        assertSame(made, readGlobalReference(checkNotNull(exports(instance).global("kept"))))
    }

    @Test
    fun aHostRaiseDuringStartIsCaughtByTheGuest() {

        val module = dexModule("instantiation_start_raise")
        val store = store(module)

        val instance = instantiate(store, module, raiseImports(store))

        assertEquals(40L, readGlobal(checkNotNull(exports(instance).global("caught"))))
    }

    @Test
    fun anUncaughtHostRaiseDuringStartHandsOverTheException() {

        val module = dexModule("instantiation_start_raise_uncaught")
        val store = store(module)

        val result = runBlocking { instance(store, module, raiseImports(store)) }
        val error = assertIs<ExecutionError.Thrown>(assertIs<ChasmResult.Error<InstantiationError>>(result).error)

        assertEquals(50L, readPayload(error.exception, 0))
    }

    @Test
    fun aTrappingStartFunctionReportsFramesWithDebugInfo() {

        val module = dexModule("instantiation_start_trap", RuntimeConfig(stacktraces = true))

        val result = runBlocking { instance(store(module), module, emptyList()) }
        val error = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<InstantiationError>>(result).error)

        val frames = checkNotNull(error.trap.stackTrace).frames
        assertTrue(frames.isNotEmpty(), frames.toString())
    }

    private fun raiseImports(store: Store): List<Import> {

        val tags = instantiate(store, dexModule("host_exceptions_tags"), emptyList())
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

    private fun failure(module: Module): InstantiationError {

        val result = runBlocking { instance(store(module), module, emptyList()) }

        return assertIs<ChasmResult.Error<InstantiationError>>(result).error
    }

    private fun instantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Instance {
        return runBlocking { instance(store, module, imports) }.expect("instance")
    }

    private companion object {
        const val PAYLOAD_SCALE = 10L
    }
}
