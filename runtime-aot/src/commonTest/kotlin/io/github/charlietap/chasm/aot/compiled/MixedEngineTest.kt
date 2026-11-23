package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.aot.store
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertIs
import io.github.charlietap.chasm.embedding.store as interpreterStore

class MixedEngineTest {

    @Test
    fun `an AOT store rejects a decoded module`() = runBlocking<Unit> {
        val compiled = generate(fixture("add"), JvmOptions(entryClass = "chasm.aot.mixed.owner.WasmModule")).compiledModule()
        val decoded = module(fixture("add")).expect("add decodes")

        val result = instance(store(compiled), decoded)

        assertIs<ExecutionError.Unsupported>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    @Test
    fun `an interpreter store rejects a compiled module`() = runBlocking<Unit> {
        val compiled = generate(fixture("add"), JvmOptions(entryClass = "chasm.aot.mixed.adder.WasmModule")).compiledModule()

        val result = instance(interpreterStore(), compiled)

        assertIs<ExecutionError.Unsupported>(assertIs<ChasmResult.Error<*>>(result).error)
    }
}
