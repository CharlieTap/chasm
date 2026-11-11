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
    fun aCacheThatThrowsIsIgnored() {
        val store = store()
        val module = module(fixture("add")).expect("module")

        val instance = instance(store, module, emptyList(), RuntimeConfig(), FailingCache).expect("instance")
        val result = invoke(store, instance, "add", listOf(I32(2), I32(3))).expect("add")

        assertEquals(listOf<ExecutionValue>(I32(5)), result)
    }

    private class MapCache : JitCache {

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
