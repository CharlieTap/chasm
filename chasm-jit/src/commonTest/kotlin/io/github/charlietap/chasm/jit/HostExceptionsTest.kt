package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.dsl.functionImport
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.withExceptions
import io.github.charlietap.chasm.host.withTag
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HostExceptionsTest {

    private val store = store()
    private val instance by lazy { instantiate(store) }

    @Test
    fun hostExceptionsAreCaughtInTheCallingFunction() {
        assertEquals(31, call("catch_here", 3))
    }

    @Test
    fun hostExceptionsUnwindThroughFunctionsWithoutHandlers() {
        assertEquals(32, call("catch_above", 3))
        assertEquals(3, call("catch_all_above", 3))
    }

    @Test
    fun caughtHostExceptionsCanBeRethrownToAnOuterHandler() {
        assertEquals(34, call("rethrow_to_outer", 3))
    }

    @Test
    fun hostExceptionsFromReferenceSignaturesAreCaught() {
        assertEquals(35, call("catch_reference_lane", 3))
    }

    @Test
    fun hostExceptionsCrossAModuleWithoutHandlers() {
        assertEquals(36, call("catch_through_relay", 3))
    }

    @Test
    fun repeatedlyCaughtExceptionsLeaveTheValueStackBalanced() {
        assertEquals(CATCHES, call("catch_through_chunked_relay_repeatedly", CATCHES))
    }

    @Test
    fun hostTrapsAreNotCaughtByCatchAll() {

        val trapped = invoke(store, instance, "trap_is_not_caught", emptyList())

        assertIs<ChasmResult.Error<*>>(trapped)
        assertEquals(31, call("catch_here", 3))
    }

    @Test
    fun uncaughtHostExceptionsFailOnlyTheirCall() {

        val uncaught = invoke(store, instance, "uncaught_reaches_the_host", listOf(I32(3)))

        assertIs<ChasmResult.Error<*>>(uncaught)
        assertEquals(32, call("catch_above", 3))
    }

    private fun call(
        name: String,
        argument: Int,
    ): Int {
        val result = invoke(store, instance, name, listOf(I32(argument))).expect(name).single()
        return (result as I32).value
    }

    private fun instantiate(store: Store): Instance {

        val tags = instance(store, "host_exceptions_tags", emptyList())
        val boom = Import("tags", "boom", tags.exports.first { export -> export.name == "boom" }.value)
        val relay = instance(store, "host_exceptions_relay", listOf(boom, import(store, "raise", raise) { params { i32() } }))

        return instance(
            store,
            "host_exceptions",
            listOf(
                boom,
                import(store, "raise", raise) { params { i32() } },
                import(store, "raise_reference", raiseReference) {
                    params {
                        externref()
                        i32()
                    }
                },
                import(store, "fail", fail) {},
                Import("relay", "relay", relay.exports.first { export -> export.name == "relay" }.value),
                Import("relay", "chunked_relay", relay.exports.first { export -> export.name == "chunked_relay" }.value),
            ),
        )
    }

    private fun instance(
        store: Store,
        name: String,
        imports: List<Import>,
    ): Instance {

        val module = runBlocking { module(resourceBytes("modules/$name.wasm"), validate = true) }.expect(name)

        return runBlocking { instance(store, module, imports) }.expect(name)
    }

    private fun import(
        store: Store,
        name: String,
        function: HostFunction,
        signature: FunctionTypeBuilder.() -> Unit,
    ): Import {
        return functionImport(store) {
            moduleName = "host"
            entityName = name
            type(signature)
            reference(function)
        }
    }

    private val raise = HostFunction { parameters, _ ->
        val payload = longArrayOf(parameters.readI32(0) * PAYLOAD_SCALE)
        withTag(ModuleIndex.TagIndex(0)) {
            val tag = this
            withExceptions { raise(create(tag, payload, 0)) }
        }
    }

    private val raiseReference = HostFunction { parameters, _ ->
        val payload = longArrayOf(parameters.readI32(1) * PAYLOAD_SCALE)
        withTag(ModuleIndex.TagIndex(0)) {
            val tag = this
            withExceptions { raise(create(tag, payload, 0)) }
        }
    }

    private val fail = HostFunction { _, _ ->
        throw HostFunctionException("fail")
    }

    private companion object {
        const val PAYLOAD_SCALE = 10L
        const val CATCHES = 10_000
    }
}
