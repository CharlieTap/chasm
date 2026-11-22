package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.assertTrapped
import io.github.charlietap.chasm.integration.excludingManagedHeaps
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals

class GarbageCollectionTest {

    @Test
    fun `manual gc collects unreachable wasm objects when requested`() = integrationTest(FIXTURE, excludingManagedHeaps(FIXTURE)) {
        val runner = runner(limitedStore(), config = config(GCStrategy.MANUAL))

        assertTrapped(TrapReason.GUEST_HEAP_EXHAUSTED, allocateUntilFailure(runner), engine.name())

        collect(runner.store)

        allocate(runner)
        assertReachableObjects(runner)
    }

    @Test
    fun `traditional gc collects unreachable wasm objects at the configured threshold`() = integrationTest(FIXTURE) {
        val runner = runner(
            store = limitedStore(),
            config = config(
                strategy = GCStrategy.TRADITIONAL,
                threshold = GCThreshold.KB(0),
            ),
        )

        repeat(ALLOCATIONS) {
            allocate(runner)
        }

        assertReachableObjects(runner)
    }

    @Test
    fun `arena gc collects unreachable wasm objects after an exported function returns`() = integrationTest(FIXTURE) {
        val runner = runner(
            store = limitedStore(),
            config = config(
                strategy = GCStrategy.ARENA,
                threshold = GCThreshold.KB(0),
            ),
        )

        repeat(ALLOCATIONS) {
            allocate(runner)
        }

        assertReachableObjects(runner)
    }

    @Suppress("DEPRECATION")
    private fun config(
        strategy: GCStrategy,
        threshold: GCThreshold = GCThreshold.MB(8),
    ): RuntimeConfig {
        return RuntimeConfig(
            gcStrategy = strategy,
            gcThreshold = threshold,
        )
    }

    private fun TestScope.limitedStore(): Store {
        return engine.store(StoreConfig(limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES)))
    }

    private fun allocateUntilFailure(runner: TestRunner): ChasmResult<List<ExecutionValue>, ExecutionError> {

        var result = runner.invoke("allocate")
        var allocations = 1

        while (result is ChasmResult.Success && allocations < ALLOCATIONS) {
            result = runner.invoke("allocate")
            allocations++
        }

        return result
    }

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
        const val ALLOCATIONS = 5_000
        const val GC_HEAP_BYTES = 128L * 1024L
    }
}
