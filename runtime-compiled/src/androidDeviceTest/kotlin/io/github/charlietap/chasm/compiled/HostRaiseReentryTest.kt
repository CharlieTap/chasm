package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global.readGlobal
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
import io.github.charlietap.chasm.host.createException
import io.github.charlietap.chasm.host.raise
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.withTag
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HostRaiseReentryTest {

    private val tagsModule = dexModule("host_exceptions_tags")
    private val store = store(tagsModule)

    private var cleanup = "cleanup"

    private val instance = instantiate(store)

    @Test
    fun aRaiseSurvivesACleanupCallIntoTheGuest() {

        val caught = invoke(instance, "catch_after_cleanup", listOf(I32(4))).expect("catch_after_cleanup")

        assertEquals(I32(4), caught.single())
        assertEquals(1L, readGlobal(checkNotNull(exports(instance).global("cleanups"))))
    }

    @Test
    fun aRaiseSurvivesACleanupCallThatThrows() {

        cleanup = "throw_in_cleanup"

        val caught = invoke(instance, "catch_after_cleanup", listOf(I32(6))).expect("catch_after_cleanup")

        assertEquals(I32(6), caught.single())
    }

    @Test
    fun anUncaughtRaiseSurvivesCollection() {

        val result = invoke(instance, "uncaught_after_cleanup", listOf(I32(5)))
        val error = assertIs<ExecutionError.Thrown>(assertIs<ChasmResult.Error<*>>(result).error)

        System.gc()
        System.runFinalization()

        assertEquals(5L, readPayload(error.exception, 0))
    }

    private fun instantiate(store: Store): Instance {

        val tags = runBlocking { instance(store, tagsModule) }.expect("host_exceptions_tags")
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
                    invoke(created, cleanup)
                }
            },
        )
        val imports = listOf(
            Import("tags", "boom", checkNotNull(exports(tags).tag("boom"))),
            Import("host", "raise_with_cleanup", raise),
        )
        created = runBlocking { instance(store, dexModule("raise_reentry"), imports) }.expect("raise_reentry")

        return created
    }
}
