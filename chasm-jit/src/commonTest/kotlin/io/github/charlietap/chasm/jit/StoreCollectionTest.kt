package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.reference.StructReference
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class StoreCollectionTest {

    @Test
    fun storesThatRanNumericCodeAreCollected() {
        assertCollected(dropped("function_exporter") { instance -> invoke(instance, "double", listOf(I32(2))).expect("double") })
    }

    @Test
    fun storesWhoseStructsTheHostReadAreCollected() {
        assertCollected(
            dropped("struct_accessors") { instance ->
                val struct = (invoke(instance, "kinds").expect("kinds").single() as ReferenceValue.Struct).reference
                readStructField(struct, 0)
            },
        )
    }

    @Test
    fun structsOutlivingTheirStoreReportItGone() {

        var held: StructReference? = null
        val reference = dropped("struct_accessors") { instance ->
            held = (invoke(instance, "kinds").expect("kinds").single() as ReferenceValue.Struct).reference
        }

        assertCollected(reference)

        assertFailsWith<IllegalStateException> { readStructField(checkNotNull(held), 0) }
    }

    private fun dropped(
        name: String,
        body: (Instance) -> Unit,
    ): WeakReference<Store> {

        var reference: WeakReference<Store>? = null
        val worker = Thread {
            val created = store()
            val module = runBlocking { module(resourceBytes("modules/$name.wasm")) }.expect(name)
            val instance = runBlocking { instance(created, module) }.expect(name)
            body(instance)
            reference = WeakReference(created)
        }

        worker.start()
        worker.join()

        return checkNotNull(reference)
    }

    private fun assertCollected(reference: WeakReference<Store>) {

        repeat(GC_ATTEMPTS) {
            if (reference.get() != null) {
                Runtime.getRuntime().gc()
                System.runFinalization()
                Thread.sleep(GC_PAUSE_MS)
            }
        }

        assertNull(reference.get())
    }

    private companion object {
        const val GC_ATTEMPTS = 20
        const val GC_PAUSE_MS = 50L
    }
}
