package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.reference.readPayload
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.createException
import io.github.charlietap.chasm.host.raise
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.withTag
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HostExceptionsTest {

    private val tagsModule = dexModule("host_exceptions_tags")
    private val store = store(tagsModule)
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

        val trapped = invoke(instance, "trap_is_not_caught", emptyList())

        assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(trapped).error)
        assertEquals(31, call("catch_here", 3))
    }

    @Test
    fun uncaughtHostExceptionsFailOnlyTheirCall() {

        val uncaught = invoke(instance, "uncaught_reaches_the_host", listOf(I32(3)))

        val thrown = assertIs<ExecutionError.Thrown>(assertIs<ChasmResult.Error<*>>(uncaught).error)
        assertEquals(3 * PAYLOAD_SCALE, readPayload(thrown.exception, 0))
        assertEquals(32, call("catch_above", 3))
    }

    private fun call(
        name: String,
        argument: Int,
    ): Int {
        val result = invoke(instance, name, listOf(I32(argument))).expect(name).single()
        return (result as I32).value
    }

    private fun instantiate(store: Store): Instance {

        val tags = runBlocking { instance(store, tagsModule) }.expect("host_exceptions_tags")
        val boom = Import("tags", "boom", checkNotNull(exports(tags).tag("boom")))
        val relay = instance(store, "host_exceptions_relay", listOf(boom, import(store, "raise", raise) { params { i32() } }))
        val relayExports = exports(relay)

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
                Import("relay", "relay", checkNotNull(relayExports.function("relay"))),
                Import("relay", "chunked_relay", checkNotNull(relayExports.function("chunked_relay"))),
            ),
        )
    }

    private fun instance(
        store: Store,
        name: String,
        imports: List<Import>,
    ): Instance {

        val module = dexModule(name)

        return runBlocking { instance(store, module, imports) }.expect(name)
    }

    private fun import(
        store: Store,
        name: String,
        hostFunction: HostFunction,
        signature: FunctionTypeBuilder.() -> Unit,
    ): Import {
        return Import("host", name, function(store, FunctionTypeBuilder().apply(signature).build(), hostFunction))
    }

    private val raise = HostFunction { parameters, _ ->
        val words = longArrayOf(parameters.readI32(0) * PAYLOAD_SCALE)
        withTag(0) {
            raise(createException(words, arrayOfNulls(1)))
        }
    }

    private val raiseReference = HostFunction { parameters, _ ->
        val words = longArrayOf(parameters.readI32(1) * PAYLOAD_SCALE)
        withTag(0) {
            raise(createException(words, arrayOfNulls(1)))
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
