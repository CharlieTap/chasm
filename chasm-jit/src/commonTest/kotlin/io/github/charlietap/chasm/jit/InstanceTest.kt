@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceTest {

    @Test
    fun aModuleRunsOnTheDevice() {
        val store = store()
        val module = runBlocking { module(fixture("loops"), validate = true) }.expect("module")
        val instance = runBlocking { instance(store, module, emptyList()) }.expect("instance")

        val result = invoke(store, instance, "loops", listOf(I32(10))).expect("loops")

        assertEquals(listOf<ExecutionValue>(I32(expected(10))), result)
    }

    @Test
    fun theSameModuleInstantiatedTwiceRunsAsTwoInstances() {
        val store = store()
        val module = runBlocking { module(fixture("loops"), validate = true) }.expect("module")
        val instances = List(2) { runBlocking { instance(store, module, emptyList()) }.expect("instance") }

        val results = instances.mapIndexed { index, instance -> invoke(store, instance, "loops", listOf(I32(index + 3))).expect("loops") }

        assertEquals(listOf(listOf<ExecutionValue>(I32(expected(3))), listOf<ExecutionValue>(I32(expected(4)))), results)
    }

    @Test
    fun referencesSwappedAcrossSelfTailCallsSurviveCollections() {

        val store = store(StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, gcThreshold = GCThreshold.KB(64)))
        val module = runBlocking { module(fixture("restart"), validate = true) }.expect("module")
        val instance = runBlocking { instance(store, module, emptyList()) }.expect("instance")

        val result = invoke(store, instance, "swap", listOf(I32(3))).expect("swap")

        assertEquals(listOf<ExecutionValue>(I32(21)), result)
    }

    private fun expected(
        n: Int,
    ): Int {
        var sum = ((n + 1) * 3) xor 5
        for (i in 0 until n) sum = (sum + i).rotateLeft(3)
        return (sum - 7) * 11
    }

    private fun fixture(
        name: String,
    ): ByteArray = checkNotNull(javaClass.classLoader?.getResourceAsStream("modules/$name.wasm")) { "missing $name" }.use { it.readBytes() }
}
