package io.github.charlietap.chasm.integration.test

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.WasmTrap
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.integration.AotEngine
import io.github.charlietap.chasm.integration.Engine
import io.github.charlietap.chasm.integration.Interpreter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MixedEngineTrapTest {

    @Test
    fun `compiled code calling interpreted code reports the frames the interpreter reports`() {
        val expected = trap(owner = Interpreter(), caller = Interpreter())

        assertEquals(listOf("boom", "call_boom", "call_owner"), expected.frames.map { frame -> frame.functionName })
        assertSameTrap(expected, trap(owner = Interpreter(), caller = AotEngine()))
    }

    @Test
    fun `interpreted code calling compiled code reports the frames the interpreter reports`() {
        val expected = trap(owner = Interpreter(), caller = Interpreter())

        assertSameTrap(expected, trap(owner = AotEngine(), caller = Interpreter()))
    }

    @Test
    fun `a trap in a function compiled to several methods reports the frames the interpreter reports`() {
        val expected = trap(owner = Interpreter(), caller = Interpreter(), owned = SPLIT)
        val split = AotEngine { maxMethodBytes(MIN_METHOD_BYTES) }

        assertSameTrap(expected, trap(owner = split, caller = Interpreter(), owned = SPLIT))
        assertSameTrap(expected, trap(owner = split, caller = AotEngine(), owned = SPLIT))
    }

    private fun assertSameTrap(
        expected: WasmTrap,
        actual: WasmTrap,
    ) {
        assertTrue(expected.traceComplete)
        assertEquals(expected.frames, actual.frames)
        assertEquals(expected.traceComplete, actual.traceComplete)
    }

    private fun trap(
        owner: Engine,
        caller: Engine,
        owned: String = OWNER,
    ): WasmTrap {
        val store = store()
        val ownerInstance = owner.instantiate(bytes(owned), store, emptyList(), DIAGNOSTICS).expect("owner instantiates")
        val boom = owner.exports(ownerInstance).single { export -> export.name == "boom" }.value
        val imports = listOf(Import("owner", "boom", boom))
        val callerInstance = caller.instantiate(bytes(CALLER), store, imports, DIAGNOSTICS).expect("caller instantiates")

        val result = caller.invoke(store, callerInstance, "call_owner", emptyList())

        val error = assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result, "${owner.name()} owner, ${caller.name()} caller").error
        return assertNotNull(error.trap, "${owner.name()} owner, ${caller.name()} caller")
    }

    private fun bytes(
        fixture: String,
    ): ByteArray = Resource("integration/$fixture.wasm").readBytes()

    private companion object {
        const val OWNER = "trap_diagnostics_start_owner"
        const val SPLIT = "trap_diagnostics_split"
        const val CALLER = "trap_diagnostics_mixed"
        const val MIN_METHOD_BYTES = 1_000
        val DIAGNOSTICS = RuntimeConfig(debugInfo = true)
    }
}
