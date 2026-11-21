package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Module
import io.github.charlietap.chasm.embedding.compiled.PreparedFunction
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.readI32
import io.github.charlietap.chasm.embedding.compiled.host.writeI32
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.prepareFunction
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericBridgesTest {

    private val store = store()

    private var countdown: PreparedFunction? = null

    private val instance = instantiate(store)

    @Test
    fun numericHostImportsReturnTheirResults() {
        assertEquals(7, call("call_host", 3, 4))
    }

    @Test
    fun nestedBridgedCallsKeepEachResult() {
        assertEquals(16, call("call_host_nested", 5))
    }

    @Test
    fun numericTailCallsReachHostImports() {
        assertEquals(9, call("tail_host", 4, 5))
    }

    @Test
    fun numericTailCallsReachFunctionsOfAnotherInstance() {
        assertEquals(12, call("tail_other", 6))
    }

    @Test
    fun numericCallRefReachesHostFunctions() {
        assertEquals(11, call("call_ref_host", 5, 6))
    }

    @Test
    fun hostFunctionsReenteringTheGuestKeepEachActivationsValues() {
        assertEquals(15, call("countdown", 5))
    }

    private fun call(
        name: String,
        vararg arguments: Int,
    ): Int {

        val result = invoke(store, instance, name, arguments.map { argument -> I32(argument) }).expect(name)

        return (result.single() as I32).value
    }

    private fun instantiate(store: Store): Instance {

        val other = runBlocking { instance(store, module("function_exporter")) }.expect("function_exporter")
        val add = function(
            store,
            FunctionTypeBuilder().apply {
                params {
                    i32()
                    i32()
                }
                results { i32() }
            }.build(),
            HostFunction { parameters, results -> results.writeI32(0, parameters.readI32(0) + parameters.readI32(1)) },
        )
        val recurse = function(
            store,
            FunctionTypeBuilder().apply {
                params { i32() }
                results { i32() }
            }.build(),
            HostFunction { parameters, results ->
                val result = checkNotNull(countdown).invoke(listOf<ExecutionValue>(I32(parameters.readI32(0)))).expect("countdown")
                results.writeI32(0, (result.single() as I32).value)
            },
        )
        val imports = listOf(
            Import("host", "add", add),
            Import("host", "recurse", recurse),
            Import("other", "double", checkNotNull(exports(other).function("double"))),
        )
        val instance = runBlocking { instance(store, module("numeric_bridges"), imports) }.expect("numeric_bridges")
        countdown = prepareFunction(store, instance, "countdown").expect("prepare countdown")

        return instance
    }

    private fun module(name: String): Module {
        return runBlocking { module(resourceBytes("modules/$name.wasm")) }.expect(name)
    }
}
