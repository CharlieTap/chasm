package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.withGc
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ResultType
import kotlin.test.Test
import kotlin.test.assertEquals

class RootFrameTest {

    @Test
    fun `a reference held only by a frame survives a host collection two calls away`() {
        assertEquals(listOf(I32(42)), run("survives_host_collection"))
    }

    @Test
    fun `a reference held only by a frame survives guest collections two calls away`() {
        assertEquals(listOf(I32(42)), run("survives_guest_collection"))
    }

    @Test
    fun `a reference held only by a frame survives a collection beneath a tail call`() {
        assertEquals(listOf(I32(42)), run("survives_collection_beneath_tail_call"))
    }

    @Test
    fun `a reference held only by a frame survives a collection beneath an indirect tail call`() {
        assertEquals(listOf(I32(42)), run("survives_collection_beneath_indirect_tail_call"))
    }

    @Test
    fun `a reference held only by a frame survives a collection beneath a ref tail call`() {
        assertEquals(listOf(I32(42)), run("survives_collection_beneath_ref_tail_call"))
    }

    private fun run(
        export: String,
    ): List<Any> {
        val store = store(StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, gcThreshold = GCThreshold.KB(64)))
        val collect = function(store, FunctionType(ResultType(emptyList()), ResultType(emptyList())), HostFunction { _, _ -> withGc { collect() } })
        val compiled = generate(fixture("root_frames"), JvmOptions(packageName = "chasm.aot.roots")).compiledModule()
        val instance = instance(store, compiled, listOf(Import("env", "collect", collect))).expect("instantiates")
        return invoke(store, instance, export, emptyList()).expect(export)
    }
}
