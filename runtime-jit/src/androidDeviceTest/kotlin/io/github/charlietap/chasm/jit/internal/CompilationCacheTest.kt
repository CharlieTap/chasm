@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.dex.DexModule
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowerer
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CompilationCacheTest {

    private val module = module(fixture("memory")).expect("module")

    @Test
    fun aCompiledModuleIsStoredUnderItsPackageName() {
        val cache = MapCache()

        val run = Run(cache)
        run.roundtrip()

        assertEquals(1, run.lowered)
        assertEquals(1, cache.entries.size)
        assertTrue(PackageNames.hashed(cache.entries.keys.single()))
    }

    @Test
    fun anotherStoreReusesTheStoredCompilation() {
        val cache = MapCache()
        Run(cache).roundtrip()
        val stored = cache.entries.toMap()

        val run = Run(cache)
        run.roundtrip()

        assertEquals(0, run.lowered)
        assertEquals(stored.keys, cache.entries.keys)
        assertContentEquals(stored.values.single(), cache.entries.values.single())
    }

    @Test
    fun aSecondInstanceInTheSameStoreHasAnEntryOfItsOwn() {
        val cache = MapCache()
        val run = Run(cache)

        run.roundtrip()
        run.roundtrip()

        assertEquals(2, run.lowered)
        assertEquals(2, cache.entries.size)
    }

    @Test
    fun aCorruptEntryIsCompiledAgainAndReplaced() {
        val cache = MapCache()
        Run(cache).roundtrip()
        val key = cache.entries.keys.single()
        val stored = checkNotNull(cache.entries[key])
        cache.entries[key] = stored.copyOf(stored.size / 2)

        val run = Run(cache)
        run.roundtrip()

        assertEquals(1, run.lowered)
        assertContentEquals(stored, cache.entries[key])
    }

    @Test
    fun anEntryFromAnotherVersionOrPackageIsNotDecoded() {
        val cache = MapCache()
        Run(cache).roundtrip()
        val (key, stored) = cache.entries.entries.single()

        assertNotNull(decodeCompilation(stored, key))
        assertNull(decodeCompilation(stored, key, version = "0.0.0"))
        assertNull(decodeCompilation(stored, key + "_2"))
    }

    @Test
    fun anEntryThatFailsToLoadIsCompiledAgainAndReplaced() {
        val cache = MapCache()
        Run(cache).roundtrip()
        val key = cache.entries.keys.single()
        val stored = checkNotNull(cache.entries[key])
        cache.entries[key] = encodeCompilation(key, DexModule(listOf(ByteArray(DEX_HEADER_SIZE)), "chasm.Missing", false))

        val run = Run(cache)
        run.roundtrip()

        assertEquals(1, run.lowered)
        assertContentEquals(stored, cache.entries[key])
    }

    @Test
    fun anEntryRoundTripsSeveralDexFilesAndChunkStarts() {
        val first = byteArrayOf(1, 2, 3)
        val second = byteArrayOf(4, 5, 6, 7)
        val module = DexModule(listOf(first, second), "chasm.Installer", true, mapOf(3 to intArrayOf(0, 40), 9 to intArrayOf(0, 12, 80)))

        val decoded = checkNotNull(decodeCompilation(encodeCompilation(PACKAGE, module), PACKAGE))

        assertEquals(listOf(first.toList(), second.toList()), decoded.dex.map(::contents))
        assertEquals("chasm.Installer", decoded.installer)
        assertTrue(decoded.inlineMemory)
        assertEquals(module.chunkStarts.mapValues { (_, starts) -> starts.toList() }, decoded.chunkStarts.mapValues { (_, starts) -> starts.toList() })
    }

    @Test
    fun aTruncatedEntryDecodesToNothing() {
        val module = DexModule(listOf(byteArrayOf(1, 2, 3)), "chasm.Installer", false, mapOf(3 to intArrayOf(0, 40)))
        val entry = encodeCompilation(PACKAGE, module)

        (0 until entry.size).forEach { size -> assertNull(decodeCompilation(entry.copyOf(size), PACKAGE), "truncated to $size") }
    }

    @Test
    fun aFailingCacheLeavesCompilationUnaffected() {
        val run = Run(FailingCache())

        run.roundtrip()

        assertEquals(1, run.lowered)
    }

    private class MapCache : CompilationCache {

        val entries = ConcurrentHashMap<String, ByteArray>()

        override fun get(
            key: String,
        ): ByteArray? = entries[key]

        override fun put(
            key: String,
            value: ByteArray,
        ) {
            entries[key] = value
        }
    }

    private class FailingCache : CompilationCache {

        override fun get(
            key: String,
        ): ByteArray = error("unreadable")

        override fun put(
            key: String,
            value: ByteArray,
        ) = error("unwritable")
    }

    /** Compilation for one store's package names, sharing [cache] with other runs. */
    private inner class Run(
        private val cache: CompilationCache,
    ) {
        private val names = PackageNames()
        var lowered = 0
            private set

        fun roundtrip() {
            val store = store()
            val instance = instantiate(store).expect("instance")
            val result = invoke(store, instance, "roundtrip", listOf(I32(41))).expect("roundtrip")
            assertEquals(listOf<ExecutionValue>(I32(42)), result)
        }

        private fun instantiate(
            store: Store,
        ): ChasmResult<Instance, ChasmError.ExecutionError> = instance(
            store = store,
            module = module,
            imports = emptyList(),
            config = RuntimeConfig(),
            compiler = { compiled, config ->
                compile(
                    module = compiled,
                    config = config,
                    cache = cache,
                    packageNames = names::next,
                    moduleCompiler = { ast, types -> ModuleCompiler(ast, types) },
                    uncompilableFunctions = ::UncompilableFunctions,
                    moduleLowerer = { ast, code, options ->
                        lowered++
                        ModuleLowerer(ast, code, options)
                    },
                    dexLoader = ::DexLoader,
                )
            },
            instantiator = ::ModuleInstantiator,
        )
    }

    private fun contents(
        buffer: ByteBuffer,
    ): List<Byte> = ByteArray(buffer.remaining()).also { bytes -> buffer.duplicate().get(bytes) }.toList()

    private fun fixture(
        name: String,
    ): ByteArray = checkNotNull(javaClass.classLoader?.getResourceAsStream("modules/$name.wasm")) { "missing $name" }.use { it.readBytes() }

    private companion object {
        const val DEX_HEADER_SIZE = 112
        const val PACKAGE = "chasm.jit.h00"
    }
}
