package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiReferencesTest {

    @Test
    fun severalResultsCarryReferencesThroughCalls() {

        val instance = instantiate()
        val first = ReferenceValue.Extern("first")
        val second = ReferenceValue.Extern("second")

        val result = invoke(instance, "swap", listOf(first, second)).expect("swap")

        assertEquals(listOf(second, first, I32(7)), result)
    }

    @Test
    fun severalResultsCarryReferencesThroughTailCalls() {

        val instance = instantiate()
        val first = ReferenceValue.Extern("first")
        val second = ReferenceValue.Extern("second")

        val result = invoke(instance, "tail_swap", listOf(first, second)).expect("tail_swap")

        assertEquals(listOf(second, first, I32(7)), result)
    }

    @Test
    fun argumentArraysCarryReferences() {

        val instance = instantiate()
        val value = ReferenceValue.Extern("wide")

        val result = invoke(instance, "wide", listOf(value)).expect("wide")

        assertEquals(listOf(value, I64(WIDE_SUM)), result)
    }

    private fun instantiate(): Instance {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/multi_references.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, emptyList()) }.expect("instance")
    }

    private companion object {
        const val WIDE_SUM = 5050L
    }
}
