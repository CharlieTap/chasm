@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GarbageCollectionTest {

    @Test
    fun `manual gc collects unreachable wasm objects when requested`() = integrationTest(FIXTURE) {
        val store = store()
        val runner = runner(store, config = config(GCStrategy.MANUAL))

        allocate(runner)

        val allocatedBytesBeforeCollection = store.store.heap.allocatedGuestBytes()

        runner.gc().expect("expected garbage collection to succeed")

        assertTrue(store.store.heap.allocatedGuestBytes() < allocatedBytesBeforeCollection, engine.name())
        assertReachableObjects(runner)
    }

    @Test
    fun `traditional gc collects unreachable wasm objects at the configured threshold`() = integrationTest(FIXTURE) {
        val store = store()
        val runner = runner(
            store = store,
            config = config(
                strategy = GCStrategy.TRADITIONAL,
                threshold = GCThreshold.KB(0),
            ),
        )

        allocate(runner)
        var previousGuestBytes = store.store.heap.allocatedGuestBytes()
        var collected = false
        var allocationCount = 0
        while (!collected && allocationCount < 1_000) {
            allocate(runner)
            val guestBytes = store.store.heap.allocatedGuestBytes()
            if (guestBytes <= previousGuestBytes) {
                collected = true
            }
            previousGuestBytes = guestBytes
            allocationCount++
        }
        assertTrue(collected, engine.name())
        assertReachableObjects(runner)
    }

    @Test
    fun `arena gc collects unreachable wasm objects after an exported function returns`() = integrationTest(FIXTURE) {
        val store = store()
        val runner = runner(
            store = store,
            config = config(
                strategy = GCStrategy.ARENA,
                threshold = GCThreshold.KB(0),
            ),
        )

        allocate(runner)
        val retainedBytes = store.store.heap.allocatedGuestBytes()
        allocate(runner)
        val secondAllocationGrowth = store.store.heap.allocatedGuestBytes() - retainedBytes
        assertTrue(secondAllocationGrowth < retainedBytes, engine.name())
        assertReachableObjects(runner)
    }

    @Suppress("DEPRECATION")
    private fun config(
        strategy: GCStrategy,
        threshold: GCThreshold = GCThreshold.MB(8),
    ) = RuntimeConfig(
        gcStrategy = strategy,
        gcThreshold = threshold,
    )

    private fun TestScope.allocate(runner: TestRunner) {
        val results = runner.invoke("allocate")
            .expect("expected allocation function to succeed")

        assertEquals(emptyList(), results, engine.name())
    }

    private fun TestScope.assertReachableObjects(runner: TestRunner) {
        assertEquals(
            listOf(NumberValue.I32(5)),
            runner.invoke("rooted-struct-value")
                .expect("expected globally rooted struct to remain reachable"),
            engine.name(),
        )
        assertEquals(
            listOf(NumberValue.I32(2)),
            runner.invoke("nested-array-length")
                .expect("expected nested array to remain reachable"),
            engine.name(),
        )
        assertEquals(
            listOf(NumberValue.I32(3)),
            runner.invoke("table-array-length")
                .expect("expected table-rooted array to remain reachable"),
            engine.name(),
        )
    }

    private companion object {
        const val FIXTURE = "gc"
    }
}
