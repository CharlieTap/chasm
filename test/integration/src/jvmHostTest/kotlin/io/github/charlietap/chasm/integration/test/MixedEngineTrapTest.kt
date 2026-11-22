package io.github.charlietap.chasm.integration.test

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.WasmStackTrace
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.integration.Aot
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
        assertSameTrap(expected, trap(owner = Aot(), caller = Aot()))
    }

    @Test
    fun `a trap in a function compiled to several methods reports the frames the interpreter reports`() = runTest {
        val expected = trap(owner = Interpreter(), caller = Interpreter(), owned = SPLIT)
        val split = Aot { maxMethodBytes(MIN_METHOD_BYTES) }

        assertSameTrap(expected, trap(owner = split, caller = Aot(), owned = SPLIT))
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
        owned: String = OWNER,
    ): WasmStackTrace {

        val store = caller.store()
        val ownerModule = owner.module(bytes(owned), store, DIAGNOSTICS)
        val ownerInstance = instance(store, ownerModule, config = DIAGNOSTICS).expect("owner instantiates")
        val boom = assertNotNull(exports(ownerInstance).function("boom"))
        val imports = listOf(Import("owner", "boom", boom))
        val callerModule = caller.module(bytes(CALLER), store, DIAGNOSTICS)
        val callerInstance = instance(store, callerModule, imports, DIAGNOSTICS).expect("caller instantiates")

        val result = invoke(callerInstance, "call_owner")

        val label = "${owner.name()} owner, ${caller.name()} caller"
        return assertNotNull(trapOf(result, label).stackTrace, label)
    }

    private fun bytes(fixture: String): ByteArray {
        return Resource("integration/$fixture.wasm").readBytes()
    }

    private companion object {
        const val OWNER = "trap_diagnostics_start_owner"
        const val SPLIT = "trap_diagnostics_split"
        const val CALLER = "trap_diagnostics_mixed"
        const val MIN_METHOD_BYTES = 1_000
        val DIAGNOSTICS = RuntimeConfig(debugInfo = true)
    }
}
