@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class JitCacheTest {

    private val module = module(resourceBytes("modules/add.wasm")).expect("module")

    @Test
    fun aModuleInANewStoreReusesTheStoredCompilation() {
        val cache = MapCache()

        repeat(2) { add(store(), cache) }

        assertEquals(1, cache.entries.size)
        assertEquals(1, cache.puts.get())
    }

    @Test
    fun twoInstancesOfAModuleInOneStoreHaveAnEntryEach() {
        val cache = MapCache()
        val store = store()

        repeat(2) { add(store, cache) }

        assertEquals(2, cache.entries.size)
    }

    @Test
    fun aCorruptEntryIsCompiledAgainAndReplaced() {
        val cache = MapCache()
        add(store(), cache)
        val (key, stored) = cache.entries.entries.single()
        cache.entries[key] = stored.copyOf(stored.size / 2)

        add(store(), cache)

        assertEquals(2, cache.puts.get())
        assertContentEquals(stored, cache.entries[key])
    }

    @Test
    fun anEntryWhoseDexFailsToLoadIsCompiledAgainAndReplaced() {
        val cache = MapCache()
        add(store(), cache)
        val (key, stored) = cache.entries.entries.single()
        val magic = stored.indexOfSequence(DEX_MAGIC)
        cache.entries[key] = stored.copyOf().also { entry -> entry.fill(0, magic, magic + DEX_MAGIC.size) }

        add(store(), cache)

        assertEquals(2, cache.puts.get())
        assertContentEquals(stored, cache.entries[key])
    }

    @Test
    fun aCacheThatThrowsIsIgnored() {
        add(store(), FailingCache)
    }

    private fun add(
        store: Store,
        cache: JitCache,
    ) {
        val instance = instance(store, module, emptyList(), RuntimeConfig(), cache).expect("instance")
        val result = invoke(store, instance, "add", listOf(I32(2), I32(3))).expect("add")
        assertEquals(listOf<ExecutionValue>(I32(5)), result)
    }

    private fun ByteArray.indexOfSequence(
        sequence: ByteArray,
    ): Int = (0..size - sequence.size).first { start -> sequence.indices.all { this[start + it] == sequence[it] } }

    private class MapCache : JitCache {

        val entries = ConcurrentHashMap<String, ByteArray>()
        val puts = AtomicInteger()

        override fun get(
            key: String,
        ): ByteArray? = entries[key]

        override fun put(
            key: String,
            value: ByteArray,
        ) {
            puts.incrementAndGet()
            entries[key] = value
        }
    }

    private object FailingCache : JitCache {

        override fun get(
            key: String,
        ): ByteArray = error("unreadable")

        override fun put(
            key: String,
            value: ByteArray,
        ) = error("unwritable")
    }

    private companion object {
        val DEX_MAGIC = "dex\n".encodeToByteArray()
    }
}
