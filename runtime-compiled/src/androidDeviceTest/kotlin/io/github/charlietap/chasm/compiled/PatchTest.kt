package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.dex.CompileConfig
import io.github.charlietap.chasm.dex.DexOutput
import io.github.charlietap.chasm.dex.compile
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PatchTest {

    @Test
    fun everyExportOfAPatchLoadedFromMemoryReturnsItsValue() {

        val className = dexClassName()
        val output = dexOutput(resourceBytes(PATCH), className)

        assertExports(instantiate(module(DexInput.memory(output.dexFiles, output.resources), className).expect("loads")))
    }

    @Test
    fun everyExportOfAPatchLoadedFromADirectoryReturnsItsValue() {

        val directory = Files.createTempDirectory("patch").resolve("module")
        val className = dexClassName()
        runBlocking { compile(resourceBytes(PATCH), DexOutput.directory(directory), CompileConfig(className)) }.expect("compiles")

        assertExports(instantiate(module(DexInput.directory(directory), className).expect("loads")))
    }

    private fun instantiate(module: Module): Instance {

        val store = store(module)
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

        return runBlocking { instance(store, module, listOf(Import("host", "add", add))) }.expect("instantiates")
    }

    private fun assertExports(instance: Instance) {

        EXPECTED.forEach { (name, value) ->
            assertEquals(listOf(value), invoke(instance, name).expect(name), name)
        }

        val trap = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(invoke(instance, "trap")).error)
        assertEquals(TrapReason.UNREACHABLE, trap.trap.reason)
    }

    private companion object {
        const val PATCH = "modules/patch.wasm"
        val EXPECTED = listOf(
            "struct_inline" to I32(14),
            "struct_wide" to I64(4_294_967_303L),
            "subtype" to I32(9),
            "array_bytes" to I32(204),
            "array_references" to I32(10),
            "casts" to I32(18),
            "branch_casts" to I32(4),
            "data" to I32(12),
            "reference_global" to I32(8),
            "call_indirect" to I32(25),
            "host" to I32(42),
        )
    }
}
