package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.gc
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class GarbageCollectionTest {

    @Test
    fun `manual gc collects unreachable wasm objects when requested`() {
        val context = instantiate(GCStrategy.MANUAL)

        context.allocate()

        context.assertAllocatedObjects(structs = 4, arrays = 5)

        gc(context.store).expect("expected garbage collection to succeed")

        context.assertAllocatedObjects(structs = 2, arrays = 3)
        context.assertReachableObjects()
    }

    @Test
    fun `traditional gc collects unreachable wasm objects at the configured threshold`() {
        val context = instantiate(
            strategy = GCStrategy.TRADITIONAL,
            threshold = GCThreshold.KB(0),
        )

        context.allocate()

        context.assertAllocatedObjects(structs = 2, arrays = 3)
        context.assertReachableObjects()
    }

    @Test
    fun `arena gc collects unreachable wasm objects after an exported function returns`() {
        val context = instantiate(
            strategy = GCStrategy.ARENA,
            threshold = GCThreshold.KB(0),
        )

        context.allocate()

        context.assertAllocatedObjects(structs = 2, arrays = 3)
        context.assertReachableObjects()
    }

    private fun instantiate(
        strategy: GCStrategy,
        threshold: GCThreshold = GCThreshold.MB(8),
    ): TestContext {
        val module = module(Resource(FIXTURE).readBytes())
            .expect("expected garbage collection fixture to decode")
            .let(::validate)
            .expect("expected garbage collection fixture to validate")
        val store = Store()
        val config = RuntimeConfig(
            gcStrategy = strategy,
            gcThreshold = threshold,
        )
        val instance = instance(store, module, emptyList(), config)
            .expect("expected garbage collection fixture to instantiate")

        return TestContext(store, instance)
    }

    private fun TestContext.allocate() {
        val results = invoke(store, instance, "allocate")
            .expect("expected allocation function to succeed")

        assertEquals(emptyList(), results)
    }

    private fun TestContext.assertAllocatedObjects(
        structs: Int,
        arrays: Int,
    ) {
        assertEquals(structs, store.store.structs.count { it != null })
        assertEquals(arrays, store.store.arrays.count { it != null })
    }

    private fun TestContext.assertReachableObjects() {
        assertEquals(
            listOf(NumberValue.I32(5)),
            invoke(store, instance, "rooted-struct-value")
                .expect("expected globally rooted struct to remain reachable"),
        )
        assertEquals(
            listOf(NumberValue.I32(2)),
            invoke(store, instance, "nested-array-length")
                .expect("expected nested array to remain reachable"),
        )
        assertEquals(
            listOf(NumberValue.I32(3)),
            invoke(store, instance, "table-array-length")
                .expect("expected table-rooted array to remain reachable"),
        )
    }

    private data class TestContext(
        val store: Store,
        val instance: Instance,
    )

    companion object {
        private const val FIXTURE = "integration/gc.wasm"
    }
}
