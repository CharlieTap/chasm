@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JitCacheTest {

    @Test
    fun aModuleInstantiatedWithACacheStoresItsCompilation() {
        val cache = MapCache()
        val store = store()
        val module = module(fixture("add")).expect("module")

        val instance = instance(store, module, emptyList(), RuntimeConfig(), cache).expect("instance")
        val result = invoke(store, instance, "add", listOf(I32(2), I32(3))).expect("add")

        assertEquals(listOf<ExecutionValue>(I32(5)), result)
        assertEquals(1, cache.entries.size)
        assertTrue(cache.entries.keys.single().all { char -> char.isLetterOrDigit() || char == '.' || char == '_' })
    }

    @Test
    fun aModuleInANewStoreReusesTheStoredCompilation() {
        val cache = MapCache()
        val module = module(fixture("add")).expect("module")

        val results = List(2) {
            val store = store()
            val instance = instance(store, module, emptyList(), RuntimeConfig(), cache).expect("instance")
            invoke(store, instance, "add", listOf(I32(2), I32(3))).expect("add")
        }

        assertEquals(List(2) { listOf<ExecutionValue>(I32(5)) }, results)
        assertEquals(1, cache.entries.size)
        assertEquals(1, cache.puts.get())
    }

    @Test
    fun twoInstancesOfAModuleInOneStoreHaveAnEntryEach() {
        val cache = MapCache()
        val store = store()
        val module = module(fixture("add")).expect("module")

        repeat(2) { instance(store, module, emptyList(), RuntimeConfig(), cache).expect("instance") }

        assertEquals(2, cache.entries.size)
    }

    @Test
    fun aCacheThatThrowsIsIgnored() {
        val store = store()
        val module = module(fixture("add")).expect("module")

        val instance = instance(store, module, emptyList(), RuntimeConfig(), FailingCache).expect("instance")
        val result = invoke(store, instance, "add", listOf(I32(2), I32(3))).expect("add")

        assertEquals(listOf<ExecutionValue>(I32(5)), result)
    }

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

    private fun fixture(
        name: String,
    ): ByteArray = checkNotNull(javaClass.classLoader?.getResourceAsStream("modules/$name.wasm")) { "missing $name" }.use { it.readBytes() }
}
