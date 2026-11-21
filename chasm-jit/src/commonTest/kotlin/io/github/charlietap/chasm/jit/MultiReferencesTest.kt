package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I64
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiReferencesTest {

    @Test
    fun severalResultsCarryReferencesThroughCalls() {

        val (store, instance) = instantiate()
        val first = ReferenceValue.Extern("first")
        val second = ReferenceValue.Extern("second")

        val result = invoke(store, instance, "swap", listOf(first, second)).expect("swap")

        assertEquals(listOf(second, first, I32(7)), result)
    }

    @Test
    fun severalResultsCarryReferencesThroughTailCalls() {

        val (store, instance) = instantiate()
        val first = ReferenceValue.Extern("first")
        val second = ReferenceValue.Extern("second")

        val result = invoke(store, instance, "tail_swap", listOf(first, second)).expect("tail_swap")

        assertEquals(listOf(second, first, I32(7)), result)
    }

    @Test
    fun argumentArraysCarryReferences() {

        val (store, instance) = instantiate()
        val value = ReferenceValue.Extern("wide")

        val result = invoke(store, instance, "wide", listOf(value)).expect("wide")

        assertEquals(listOf(value, I64(WIDE_SUM)), result)
    }

    private fun instantiate(): Pair<Store, Instance> {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/multi_references.wasm"), validate = true) }.expect("module")
        val instance = runBlocking { instance(store, module, emptyList()) }.expect("instance")

        return store to instance
    }

    private companion object {
        const val WIDE_SUM = 5050L
    }
}
