package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Module
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.createException
import io.github.charlietap.chasm.embedding.compiled.host.raise
import io.github.charlietap.chasm.embedding.compiled.host.readI32
import io.github.charlietap.chasm.embedding.compiled.host.withTag
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.readGlobal
import io.github.charlietap.chasm.embedding.compiled.readPayload
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.reference.ExceptionReference
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HostRaiseReentryTest {

    private val store = store()

    private var cleanup = "cleanup"

    private val instance = instantiate(store)

    @Test
    fun aRaiseSurvivesACleanupCallIntoTheGuest() {

        val caught = invoke(store, instance, "catch_after_cleanup", listOf(I32(4))).expect("catch_after_cleanup")

        assertEquals(I32(4), caught.single())
        assertEquals(1L, readGlobal(checkNotNull(exports(instance).global("cleanups"))))
    }

    @Test
    fun aRaiseSurvivesACleanupCallThatThrows() {

        cleanup = "throw_in_cleanup"

        val caught = invoke(store, instance, "catch_after_cleanup", listOf(I32(6))).expect("catch_after_cleanup")

        assertEquals(I32(6), caught.single())
    }

    @Test
    fun anUncaughtRaiseSurvivesCollection() {

        val result = invoke(store, instance, "uncaught_after_cleanup", listOf(I32(5)))
        val error = assertIs<ExecutionError.Thrown<ExceptionReference>>(assertIs<ChasmResult.Error<*>>(result).error)

        System.gc()
        System.runFinalization()

        assertEquals(5L, readPayload(error.exception, 0))
    }

    private fun instantiate(store: Store): Instance {

        val tags = runBlocking { instance(store, module("host_exceptions_tags")) }.expect("host_exceptions_tags")
        lateinit var created: Instance
        val raise = function(
            store,
            FunctionTypeBuilder().apply { params { i32() } }.build(),
            HostFunction { parameters, _ ->
                val words = longArrayOf(parameters.readI32(0).toLong())
                try {
                    withTag(0) {
                        raise(createException(words, arrayOfNulls(1)))
                    }
                } finally {
                    invoke(store, created, cleanup)
                }
            },
        )
        val imports = listOf(
            Import("tags", "boom", checkNotNull(exports(tags).tag("boom"))),
            Import("host", "raise_with_cleanup", raise),
        )
        created = runBlocking { instance(store, module("raise_reentry"), imports) }.expect("raise_reentry")

        return created
    }

    private fun module(name: String): Module {
        return runBlocking { module(resourceBytes("modules/$name.wasm")) }.expect(name)
    }
}
