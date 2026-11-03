@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceTest {

    @Test
    fun aModuleRunsOnTheDevice() {
        val store = store()
        val module = module(fixture("loops")).expect("module")
        val instance = instance(store, module, emptyList()).expect("instance")

        val result = invoke(store, instance, "loops", listOf(I32(10))).expect("loops")

        assertEquals(listOf<ExecutionValue>(I32(expected(10))), result)
    }

    @Test
    fun theSameModuleInstantiatedTwiceRunsAsTwoInstances() {
        val store = store()
        val module = module(fixture("loops")).expect("module")
        val instances = List(2) { instance(store, module, emptyList()).expect("instance") }

        val results = instances.mapIndexed { index, instance -> invoke(store, instance, "loops", listOf(I32(index + 3))).expect("loops") }

        assertEquals(listOf(listOf<ExecutionValue>(I32(expected(3))), listOf<ExecutionValue>(I32(expected(4)))), results)
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
