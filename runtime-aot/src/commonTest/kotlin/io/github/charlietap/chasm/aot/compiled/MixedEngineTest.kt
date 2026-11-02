package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.embedding.instance as interpretedInstance

class MixedEngineTest {

    @Test
    fun `compiled code calls and tail calls an interpreted import`() {
        val store = store()
        val adder = interpretedInstance(store, module(fixture("add")).expect("add decodes"), emptyList()).expect("add instantiates")
        val compiled = generate(fixture("call_add"), JvmOptions(packageName = "chasm.aot.mixed.caller")).compiledModule()
        val caller = instance(store, compiled, adderImports(adder)).expect("caller instantiates")

        assertEquals(listOf(I32(5)), invoke(store, caller, "call_add", listOf(I32(2), I32(3))).expect("call"))
        assertEquals(listOf(I32(9)), invoke(store, caller, "tail_add", listOf(I32(4), I32(5))).expect("tail call"))
    }

    @Test
    fun `interpreted code calls and tail calls a compiled import`() {
        val store = store()
        val compiled = generate(fixture("add"), JvmOptions(packageName = "chasm.aot.mixed.adder")).compiledModule()
        val adder = instance(store, compiled, emptyList()).expect("add instantiates")
        val caller = interpretedInstance(store, module(fixture("call_add")).expect("caller decodes"), adderImports(adder))
            .expect("caller instantiates")

        assertEquals(listOf(I32(5)), invoke(store, caller, "call_add", listOf(I32(2), I32(3))).expect("call"))
        assertEquals(listOf(I32(9)), invoke(store, caller, "tail_add", listOf(I32(4), I32(5))).expect("tail call"))
    }

    private fun adderImports(
        adder: Instance,
    ): List<Import> = exports(adder).map { export -> Import("add", export.name, export.value) }
}
