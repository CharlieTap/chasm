@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceTest {

    @Test
    fun aModuleRunsOnTheDevice() {
        val module = dexModule("loops")
        val store = store(module)
        val instance = runBlocking { instance(store, module, emptyList()) }.expect("instance")

        val result = invoke(instance, "loops", listOf(I32(10))).expect("loops")

        assertEquals(listOf<ExecutionValue>(I32(expected(10))), result)
    }

    @Test
    fun theSameModuleInstantiatedTwiceRunsAsTwoInstances() {
        val module = dexModule("loops")
        val store = store(module)
        val instances = List(2) { runBlocking { instance(store, module, emptyList()) }.expect("instance") }

        val results = instances.mapIndexed { index, instance -> invoke(instance, "loops", listOf(I32(index + 3))).expect("loops") }

        assertEquals(listOf(listOf<ExecutionValue>(I32(expected(3))), listOf<ExecutionValue>(I32(expected(4)))), results)
    }

    @Test
    fun referencesSwappedAcrossSelfTailCallsSurviveCollections() {

        val module = dexModule("restart")
        val store = store(module)
        val instance = runBlocking { instance(store, module, emptyList()) }.expect("instance")

        val result = invoke(instance, "swap", listOf(I32(3))).expect("swap")

        assertEquals(listOf<ExecutionValue>(I32(21)), result)
    }

    private fun expected(
        n: Int,
    ): Int {
        var sum = ((n + 1) * 3) xor 5
        for (i in 0 until n) sum = (sum + i).rotateLeft(3)
        return (sum - 7) * 11
    }
}
