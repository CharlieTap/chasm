package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.extern.createExternRef
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.runtime.value.NumberValue.I64
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiReferencesTest {

    @Test
    fun severalResultsCarryReferencesThroughCalls() {

        val (store, instance) = instantiate()
        val first = extern(store, "first")
        val second = extern(store, "second")

        val result = invoke(store, instance, "swap", listOf(first, second)).expect("swap")

        assertEquals(listOf(second, first, I32(7)), result)
    }

    @Test
    fun severalResultsCarryReferencesThroughTailCalls() {

        val (store, instance) = instantiate()
        val first = extern(store, "first")
        val second = extern(store, "second")

        val result = invoke(store, instance, "tail_swap", listOf(first, second)).expect("tail_swap")

        assertEquals(listOf(second, first, I32(7)), result)
    }

    @Test
    fun argumentArraysCarryReferences() {

        val (store, instance) = instantiate()
        val value = extern(store, "wide")

        val result = invoke(store, instance, "wide", listOf(value)).expect("wide")

        assertEquals(listOf(value, I64(WIDE_SUM)), result)
    }

    private fun instantiate(): Pair<Store, Instance> {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/multi_references.wasm"), validate = true) }.expect("module")
        val instance = runBlocking { instance(store, module, emptyList()) }.expect("instance")

        return store to instance
    }

    private fun extern(
        store: Store,
        value: Any,
    ): ExecutionValue {
        return createExternRef(store, value).expect("extern").asValue().expect("value")
    }

    private companion object {
        const val WIDE_SUM = 5050L
    }
}
