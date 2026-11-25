package io.github.charlietap.chasm.compiled.code

import io.github.charlietap.chasm.compiled.store
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmStackTrace
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import io.github.charlietap.chasm.embedding.store as interpreterStore

class SplitFunctionTrapTest {

    @Test
    fun `a trap in a function compiled to several methods reports the frames the interpreter reports`() = runBlocking<Unit> {
        val expected = trap(interpreterStore(DIAGNOSTICS), module(fixture(SPLIT)).expect("owner decodes"), module(fixture(CALLER)).expect("caller decodes"))
        val owner = generate(fixture(SPLIT), JvmOptions(entryClass = "chasm.aot.split.owner.WasmModule", runtime = DIAGNOSTICS, splitInstructions = 2))
        val caller = generate(fixture(CALLER), JvmOptions(entryClass = "chasm.aot.split.caller.WasmModule", runtime = DIAGNOSTICS))

        val ownerModule = owner.compiledModule()
        val actual = trap(store(ownerModule), ownerModule, caller.compiledModule())

        assertTrue(owner.classes.values.any { bytes -> "\$c1" in String(bytes, Charsets.ISO_8859_1) }, "the trapping function was not split")
        assertEquals(listOf("boom", "call_boom", "call_owner"), expected.frames.map { frame -> frame.functionName })
        assertEquals(expected.frames.map(::named), actual.frames.map(::named))
        assertTrue(expected.complete)
        assertEquals(expected.complete, actual.complete)
    }

    private suspend fun trap(
        store: Store,
        owner: Module,
        caller: Module,
    ): WasmStackTrace {

        val ownerInstance = instance(store, owner).expect("owner instantiates")
        val boom = assertNotNull(exports(ownerInstance).function("boom"))
        val callerInstance = instance(store, caller, listOf(Import("owner", "boom", boom))).expect("caller instantiates")

        val result = invoke(callerInstance, "call_owner")

        val trapped = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(result).error)
        return assertNotNull(trapped.trap.stackTrace)
    }

    private fun named(frame: WasmFrame): Triple<String?, String?, Int> {
        return Triple(frame.moduleName, frame.functionName, frame.functionIndex)
    }

    private companion object {
        const val SPLIT = "trap_diagnostics_split"
        const val CALLER = "trap_diagnostics_mixed"
        val DIAGNOSTICS = RuntimeConfig(stacktraces = true)
    }
}
