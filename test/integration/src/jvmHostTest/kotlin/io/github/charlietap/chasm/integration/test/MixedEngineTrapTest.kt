package io.github.charlietap.chasm.integration.test

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.WasmStackTrace
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.integration.ClassFiles
import io.github.charlietap.chasm.integration.Engine
import io.github.charlietap.chasm.integration.Interpreter
import io.github.charlietap.chasm.integration.trapOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MixedEngineTrapTest {

    @Test
    fun `compiled code calling compiled code reports the frames the interpreter reports`() = runTest {
        val expected = trap(owner = Interpreter(), caller = Interpreter())

        assertEquals(listOf("boom", "call_boom", "call_owner"), expected.frames.map { frame -> frame.functionName })
        assertSameTrap(expected, trap(owner = ClassFiles(), caller = ClassFiles()))
    }

    private fun assertSameTrap(
        expected: WasmStackTrace,
        actual: WasmStackTrace,
    ) {
        assertTrue(expected.complete)
        assertEquals(expected.frames, actual.frames)
        assertEquals(expected.complete, actual.complete)
    }

    private suspend fun trap(
        owner: Engine,
        caller: Engine,
    ): WasmStackTrace {

        val ownerModule = owner.module(bytes(OWNER), DIAGNOSTICS)
        val callerModule = caller.module(bytes(CALLER), DIAGNOSTICS)
        val store = caller.store(callerModule, DIAGNOSTICS)
        val ownerInstance = instance(store, ownerModule).expect("owner instantiates")
        val boom = assertNotNull(exports(ownerInstance).function("boom"))
        val imports = listOf(Import("owner", "boom", boom))
        val callerInstance = instance(store, callerModule, imports).expect("caller instantiates")

        val result = invoke(callerInstance, "call_owner")

        val label = "${owner.name()} owner, ${caller.name()} caller"
        return assertNotNull(trapOf(result, label).stackTrace, label)
    }

    private fun bytes(fixture: String): ByteArray {
        return Resource("integration/$fixture.wasm").readBytes()
    }

    private companion object {
        const val OWNER = "trap_diagnostics_start_owner"
        const val CALLER = "trap_diagnostics_mixed"
        val DIAGNOSTICS = RuntimeConfig(stacktraces = true)
    }
}
