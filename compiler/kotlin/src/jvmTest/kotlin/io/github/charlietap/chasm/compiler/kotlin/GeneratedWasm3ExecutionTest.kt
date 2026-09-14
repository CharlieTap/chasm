package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.runtime.program.ProgramCompiler
import io.github.charlietap.chasm.runtime.value.NumberValue
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneratedWasm3ExecutionTest {
    @Test
    fun `references survive collection and exception continuations in all modes`() {
        val bytes = checkNotNull(javaClass.getResourceAsStream("/wasm3.wasm")).use { it.readBytes() }
        val module = module(bytes).expect("decode")
        val directory = Files.createTempDirectory("chasm-wasm3-aot").toFile()
        try {
            for (mode in listOf(null, KotlinCompilationMode.PREPARE, KotlinCompilationMode.CACHED)) {
                val compiler = mode?.let { JvmKotlinProgramCompiler(directory, it, countExecutions = true) }
                var handlerCount = 0
                val store = if (compiler == null) {
                    store()
                } else {
                    store(
                        ProgramCompiler { program, firstIp, instructions, entries ->
                            val handlers = entries.flatMap { entry ->
                                program.exceptionTable(entry)?.let { table ->
                                    table.regions.flatMap { region -> region.catches.map { table.entryIp + it.targetOffset } }
                                } ?: emptyList()
                            }
                            val original = handlers.associateWith { program.instructions[it] }
                            compiler.compile(program, firstIp, instructions, entries).also { error ->
                                if (error == null) handlerCount += handlers.count { program.instructions[it] !== original[it] }
                            }
                        },
                    )
                }
                try {
                    val samples = mutableListOf<Long>()
                    val bindings = imports(store) {
                        function {
                            moduleName = "env"
                            entityName = "sample"
                            type { }
                            reference { _, _ -> samples.add(contextOf<HostResources>().gc.allocatedBytes) }
                        }
                    }
                    val instance = instance(store, module, bindings, RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL, gcThreshold = GCThreshold.KB(0))).expect("instantiate $mode")

                    fun call(name: String, vararg args: Int): Int = (invoke(store, instance, name, args.map { NumberValue.I32(it) }).expect("$mode $name").single() as NumberValue.I32).value
                    assertEquals(85359, call("stress"))
                    assertEquals(512, samples.size)
                    assertTrue(samples.zipWithNext().any { (before, after) -> after <= before }, "$mode must actually collect during the active guest frame")
                    repeat(64) { assertEquals(321, call("exception")) }
                    assertEquals(579, call("handler_temporaries"))
                    assertEquals(579, call("call_temporaries"))
                    assertEquals(100, call("call_ref", 99))
                    assertEquals(100, call("tail", 99))
                    assertEquals(99, call("cast", 0))
                    assertEquals(-1, call("cast", 1))
                    if (compiler != null) {
                        assertTrue(handlerCount > 0, "Catch continuation must enter generated code")
                        assertTrue(compiler.generatedBlockExecutions > 0)
                    }
                } finally {
                    dropStore(store)
                    compiler?.close()
                }
            }
        } finally {
            directory.deleteRecursively()
        }
    }
}
