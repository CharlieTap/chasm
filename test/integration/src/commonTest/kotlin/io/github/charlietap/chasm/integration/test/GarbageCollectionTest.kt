package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.KB
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals

class GarbageCollectionTest {

    @Test
    fun `unreachable wasm objects are collected when the heap fills`() = integrationTest(FIXTURE) {

        val runner = runner(LIMITED_STORE)

        repeat(ALLOCATIONS) {
            allocate(runner)
        }

        assertReachableObjects(runner)
    }

    @Test
    fun `collect keeps reachable wasm objects`() = integrationTest(FIXTURE) {

        val runner = runner(LIMITED_STORE)

        allocate(runner)
        collect(runner.store)
        assertReachableObjects(runner)

        allocate(runner)
        assertReachableObjects(runner)
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
        val LIMITED_STORE = RuntimeConfig(limits = Limits(memory = MemoryLimits(gcHeap = 128.KB)))
    }
}
