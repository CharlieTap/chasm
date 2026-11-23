package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.AotModule
import io.github.charlietap.chasm.aot.internal.AotModuleInstaller
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.collect
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@OptIn(InternalChasmApi::class)
class RootFrameTest {

    @Test
    fun `a reference held only by a frame survives a host collection two calls away`() {
        assertReturns(listOf(I32(42)), "survives_host_collection")
    }

    @Test
    fun `a reference held only by a frame survives guest collections two calls away`() {
        assertReturns(listOf(I32(42)), "survives_guest_collection")
    }

    @Test
    fun `a reference held only by a frame survives a collection beneath a tail call`() {
        assertReturns(listOf(I32(42)), "survives_collection_beneath_tail_call")
    }

    @Test
    fun `a reference held only by a frame survives a collection beneath an indirect tail call`() {
        assertReturns(listOf(I32(42)), "survives_collection_beneath_indirect_tail_call")
    }

    @Test
    fun `a reference held only by a frame survives a collection beneath a ref tail call`() {
        assertReturns(listOf(I32(42)), "survives_collection_beneath_ref_tail_call")
    }

    @Test
    fun `a reference written after a collection survives the next one reached through a loop`() {
        assertReturns(listOf(I32(3)), "survives_collection_across_a_back_edge")
    }

    @Test
    fun `a reference written in a try region survives a collection in its handler`() {
        assertReturns(listOf(I32(42)), "survives_collection_in_a_handler")
    }

    @Test
    fun `a reference written in a nested try region survives a collection in an enclosing handler`() {
        assertReturns(listOf(I32(42)), "survives_collection_in_an_enclosing_handler")
    }

    @Test
    fun `a reference passed to a self tail call survives the collection at the function's start`() {
        assertReturns(listOf(I32(1)), "survives_collection_after_a_self_tail_call")
    }

    @Test
    fun `an unchanged reference passed to a self tail call survives the collection at the function's start`() {
        assertReturns(listOf(I32(42)), "survives_collection_after_an_unchanged_self_tail_call")
    }

    @Test
    fun `references swapped across a self tail call survive the collection at the function's start`() {
        assertReturns(listOf(I32(21)), "survives_collection_after_a_swapping_self_tail_call")
    }

    @Test
    fun `a reference kept in place by a select survives a collection after it`() {
        assertReturns(listOf(I32(42)), "survives_collection_after_an_in_place_select")
    }

    @Test
    fun `a caught reference survives a collection after the catch`() {
        assertReturns(listOf(I32(42)), "survives_collection_after_catching_a_reference")
    }

    @Test
    fun `an array a frame holds across the probe is counted as live`() {
        run("holds_an_array_across_live").forEach { (split, results) ->
            assertTrue((results.single() as I64).value >= RELEASED_BYTES, "split=$split")
        }
    }

    @Test
    fun `a self tail call releases what the previous pass held in its locals`() {
        run("releases_locals_on_a_self_tail_call").forEach { (split, results) ->
            assertTrue((results.single() as I64).value < RELEASED_BYTES, "split=$split")
        }
    }

    @Test
    fun `a catch releases the operands it discards`() {
        run("releases_operands_a_catch_discards").forEach { (split, results) ->
            assertTrue((results.single() as I64).value < RELEASED_BYTES, "split=$split")
        }
    }

    private fun assertReturns(
        expected: List<Any>,
        export: String,
    ) {
        run(export).forEach { (split, results) ->
            assertEquals(expected, results, "split=$split")
        }
    }

    private fun run(export: String): Map<Int?, List<Any>> {
        return listOf(null, FORCED_SPLIT).associateWith { split ->

            val options = JvmOptions(entryClass = "chasm.aot.roots.WasmModule", splitInstructions = split)
            val compiled = generate(fixture("root_frames"), options).compiledModule()
            val payload = compiled._payload() as AotModule
            val runtime = RuntimeStore(config = payload.config)
            val store = interpreterEngine(StoreRuntime(runtime), AotModuleInstaller(payload)).store
            val collectType = FunctionType(ResultType(emptyList()), ResultType(emptyList()))
            val collectFunction = function(store, collectType, HostFunction { _, _ -> collect() })
            val liveType = FunctionType(ResultType(emptyList()), ResultType(listOf(ValueType.Number(NumberType.I64))))
            val liveFunction = function(
                store,
                liveType,
                HostFunction { _, results ->
                    collect()
                    results.writeI64(0, runtime.heap.allocatedBytes)
                },
            )
            val imports = listOf(Import("env", "collect", collectFunction), Import("env", "live", liveFunction))

            val instance = runBlocking { instance(store, compiled, imports) }.expect("instantiates")
            invoke(instance, export, emptyList()).expect(export)
        }
    }

    private companion object {
        const val FORCED_SPLIT = 3
        const val RELEASED_BYTES = 512L * 1024L
    }
}
