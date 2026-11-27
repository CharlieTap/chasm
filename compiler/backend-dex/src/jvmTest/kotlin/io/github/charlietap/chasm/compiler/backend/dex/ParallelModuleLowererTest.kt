package io.github.charlietap.chasm.compiler.backend.dex

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Fuel
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ParallelModuleLowererTest {

    @Test
    fun `fixtures lower to the same dex on several workers in any claim order`() {
        for (name in FIXTURES) {
            assertSameDex(name, decodeModule(fixture(name)))
        }
    }

    @Test
    fun `corpus modules lower to the same dex on several workers in any claim order`() {
        val root = System.getProperty("codegen.determinism")?.let(::File) ?: return
        val modules = root.walkTopDown().filter { file -> file.extension == "wasm" }.sortedBy { file -> file.path }.toList()
        assertTrue(modules.isNotEmpty(), "no corpus modules under $root")
        for (file in modules) {
            val module = runCatching { decodeModule(file.readBytes()) }.getOrNull() ?: continue
            assertSameDex(file.name, module)
        }
    }

    @Test
    fun `the earliest failing batch is reported whichever worker reaches it first`() {

        val module = decodeModule(fixture("bench"))
        val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
        val failing = code.functions.map { function -> function.index.index }.toSet()

        val failure = assertFailsWith<IllegalStateException> {
            runSuspending {
                ParallelModuleLowerer(
                    module = module,
                    code = code,
                    options = DexOptions(),
                    declarations = ModuleDeclarationsFactory(module),
                    trapSites = emptyMap(),
                    resources = CollectedResources(),
                    limits = MetadataFiles.DEX_LIMITS,
                    taskExecutor = ThreadTaskExecutor(),
                    workers = { _, _, _ -> WORKERS },
                    claimOrder = { _, layout -> layout.files.indices.reversed().toList().toIntArray() },
                    functionLowerer = { _, function, _, _ -> if (function.index.index in failing) error("batch of f${function.index.index}") else null },
                    entryLowerer = { _, group, _ -> "Entry${group.number}" },
                    installLowerer = { _, _, _, _ -> emptyList() },
                )
            }
        }

        assertEquals("batch of f${code.functions.first().index.index}", failure.message)
    }

    private fun assertSameDex(
        name: String,
        module: Module,
    ) {

        for (options in OPTIONS) {
            val code = ModuleCompiler(module, module.typeResolver(), recordProvenance = false, devirtualiseIndirectCalls = true)
                .getOrThrow { IllegalStateException(it.toString()) }
            val serialResources = CollectedResources()
            val parallelResources = CollectedResources()
            val serial = runCatching { lower(module, code, options, serialResources) }
            val parallel = runCatching {
                runSuspending {
                    ParallelModuleLowerer(
                        module = module,
                        code = code,
                        options = options,
                        declarations = ModuleDeclarationsFactory(module),
                        trapSites = emptyMap(),
                        resources = parallelResources,
                        limits = MetadataFiles.DEX_LIMITS,
                        taskExecutor = ThreadTaskExecutor(),
                        workers = { _, _, _ -> WORKERS },
                        claimOrder = { _, layout -> layout.files.indices.shuffled(Random(name.hashCode())).toIntArray() },
                        functionLowerer = ::FunctionLowerer,
                        entryLowerer = ::EntryLowerer,
                        installLowerer = ::InstallLowerer,
                    )
                }
            }

            val expected = serial.getOrNull()
            val actual = parallel.getOrNull()
            if (expected == null || actual == null) {
                assertEquals(serial.exceptionOrNull()?.message, parallel.exceptionOrNull()?.message, "$name failed differently")
                continue
            }
            assertEquals(expected.dex.size, actual.dex.size, "$name file count")
            expected.dex.indices.forEach { file -> assertContentEquals(expected.dex[file], actual.dex[file], "$name file $file") }
            assertEquals(expected.moduleClass, actual.moduleClass, "$name module class")
            assertEquals(expected.classes, actual.classes, "$name classes")
            assertEquals(serialResources.resources.keys, parallelResources.resources.keys, "$name resources")
            serialResources.resources.forEach { (path, bytes) -> assertContentEquals(bytes, parallelResources.resources[path], "$name $path") }
        }
    }

    private class ThreadTaskExecutor : ParallelTaskExecutor {

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {

            val results = arrayOfNulls<Any?>(tasks.size)
            val failures = arrayOfNulls<Throwable>(tasks.size)
            val threads = tasks.mapIndexed { index, task ->
                Thread {
                    try {
                        results[index] = task(ParallelTaskScope {})
                    } catch (failure: Throwable) {
                        failures[index] = failure
                    }
                }.apply(Thread::start)
            }
            threads.forEach(Thread::join)
            failures.firstOrNull { failure -> failure != null }?.let { failure -> throw failure }

            @Suppress("UNCHECKED_CAST")
            return results.asList() as List<T>
        }
    }

    private fun <T> runSuspending(block: suspend () -> T): T {

        var outcome: Result<T>? = null
        block.startCoroutine(Continuation(EmptyCoroutineContext) { result -> outcome = result })

        return checkNotNull(outcome) { "the block suspended" }.getOrThrow()
    }

    private companion object {
        const val WORKERS = 4
        val FIXTURES = listOf("bench", "bench_gc", "calls", "control", "devirtualisation", "eh", "gc", "memory", "restart", "routes", "routes_eh", "scalar", "slot_kinds", "tail", "wide")
        val OPTIONS = listOf(
            DexOptions(entryClass = "com.example.Guest", byteArrayBaseOffset = 16L),
            DexOptions(
                entryClass = "com.example.Guest",
                runtime = RuntimeConfig(interrupts = true, stacktraces = true, limits = Limits(cpu = CpuLimits.Metered(Fuel(FUEL)))),
                byteArrayBaseOffset = 16L,
                splitInstructions = 40,
            ),
        )
        const val FUEL = 1_000_000L
    }
}
