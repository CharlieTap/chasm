@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StoreLimitsTest {

    @Test
    fun `retaining more than the gc heap limit traps and the store recovers once released`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES))))

        assertEquals(trap(InvocationError.GuestHeapOutOfMemory), allocate(runner, "retain", 64), engine.name())

        runner.invoke("release").expect("release succeeds")
        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "retain", 8), engine.name())
    }

    @Test
    fun `traditional collection reclaims garbage before the gc heap limit traps`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES))))

        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "churn", 256), engine.name())
    }

    @Test
    fun `arena traps once allocation since the last collection exceeds the gc heap limit`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(gcStrategy = GCStrategy.ARENA, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES))))

        assertEquals(trap(InvocationError.GuestHeapOutOfMemory), allocate(runner, "churn", 256), engine.name())

        runner.gc().expect("collection succeeds")
        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "churn", 8), engine.name())
    }

    @Suppress("DEPRECATION")
    @Test
    fun `the store gc strategy takes precedence over the instance runtime config`() = integrationTest(FIXTURE) {
        val runner = runner(
            store = store(StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES))),
            config = RuntimeConfig(gcStrategy = GCStrategy.ARENA),
        )

        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "churn", 256), engine.name())
    }

    @Suppress("DEPRECATION")
    @Test
    fun `the instance runtime config gc strategy applies when the store sets none`() = integrationTest(FIXTURE) {
        val runner = runner(
            store = store(StoreConfig(limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES))),
            config = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
        )

        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "churn", 256), engine.name())
    }

    @Test
    fun `memory grow returns minus one past the store limit`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(limits = StoreLimits(memoryPages = 3))))

        assertEquals(success(1), grow(runner, "memory-grow", 2), engine.name())
        assertEquals(success(-1), grow(runner, "memory-grow", 1), engine.name())
        assertEquals(success(3), grow(runner, "memory-grow", 0), engine.name())
    }

    @Test
    fun `memory grow traps past the store limit when grow failures trap`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(limits = StoreLimits(memoryPages = 3, trapOnGrowFailure = true))))

        assertEquals(success(1), grow(runner, "memory-grow", 2), engine.name())
        assertEquals(trap(InvocationError.MemoryGrowFailed), grow(runner, "memory-grow", 1), engine.name())
    }

    @Test
    fun `host memory growth respects the store limit and still returns minus one`() = integrationTest(FIXTURE) {
        val store = store(StoreConfig(limits = StoreLimits(memoryPages = 3, trapOnGrowFailure = true)))
        val runner = runner(store)
        val memory = runner.exports().expect("exports").first { export -> export.name == "memory" }.value as Memory

        assertEquals(ChasmResult.Success(-1), engine.growMemory(store, memory, 3), engine.name())
        assertEquals(ChasmResult.Success(1), engine.growMemory(store, memory, 2), engine.name())
    }

    @Test
    fun `a host created memory keeps its initial size but cannot grow past the store limit`() = integrationTest(FIXTURE) {
        val store = store(StoreConfig(limits = StoreLimits(memoryPages = 3)))
        val memory = memory(store, MemoryType(AddressType.I32, Limits(5u), SharedStatus.Unshared))

        assertEquals(ChasmResult.Success(-1), engine.growMemory(store, memory, 1), engine.name())
        assertEquals(ChasmResult.Success(5), engine.growMemory(store, memory, 0), engine.name())
    }

    @Test
    fun `instantiating a module whose memory starts above the store limit fails`() = integrationTest(LARGE_MEMORY_FIXTURE) {
        val store = store(StoreConfig(limits = StoreLimits(memoryPages = 3)))

        val result = runner(store).instance()

        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result, engine.name())
        assertTrue(result.error.error.contains("MemoryLimitExceeded"), "${engine.name()}: ${result.error.error}")
        assertEquals(0, store.store.memories.size, engine.name())
    }

    @Test
    fun `table grow returns minus one past the store limit`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(limits = StoreLimits(tableElements = 4))))

        assertEquals(success(1), grow(runner, "table-grow", 3), engine.name())
        assertEquals(success(-1), grow(runner, "table-grow", 1), engine.name())
    }

    @Test
    fun `table grow traps past the store limit when grow failures trap`() = integrationTest(FIXTURE) {
        val runner = runner(store(StoreConfig(limits = StoreLimits(tableElements = 4, trapOnGrowFailure = true))))

        assertEquals(trap(InvocationError.TableGrowFailed), grow(runner, "table-grow", 4), engine.name())
    }

    @Test
    fun `instantiating a module whose table starts above the store limit fails`() = integrationTest(LARGE_TABLE_FIXTURE) {
        val store = store(StoreConfig(limits = StoreLimits(tableElements = 100)))

        val result = runner(store).instance()

        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result, engine.name())
        assertTrue(result.error.error.contains("TableLimitExceeded"), "${engine.name()}: ${result.error.error}")
        assertEquals(0, store.store.tables.size, engine.name())
    }

    @Test
    fun `runaway recursion traps once the stack reaches the store limit and the store recovers`() = integrationTest(STACK_FIXTURE) {
        val runner = stackRunner(StoreConfig(limits = StoreLimits(stackBytes = STACK_BYTES)))

        assertEquals(trap(InvocationError.CallStackExhausted), runner.invoke("runaway"), engine.name())
        assertEquals(success(100), recurse(runner, 100), engine.name())
    }

    @Test
    fun `recursion deeper than the stack limit allows traps`() = integrationTest(STACK_FIXTURE) {
        val runner = stackRunner(StoreConfig(limits = StoreLimits(stackBytes = STACK_BYTES)))

        assertEquals(success(100), recurse(runner, 100), engine.name())
        assertEquals(trap(InvocationError.CallStackExhausted), recurse(runner, 100_000), engine.name())
    }

    @Test
    fun `the default stack limit admits recursion that a small limit rejects`() = integrationTest(STACK_FIXTURE) {
        val defaults = stackRunner(StoreConfig())
        val small = stackRunner(StoreConfig(limits = StoreLimits(stackBytes = SMALL_STACK_BYTES)))

        assertEquals(success(DEEP_RECURSION), recurse(defaults, DEEP_RECURSION), engine.name())
        assertEquals(trap(InvocationError.CallStackExhausted), recurse(small, DEEP_RECURSION), engine.name())
    }

    @Test
    fun `a call whose first frame exceeds the stack limit traps`() = integrationTest(STACK_FIXTURE) {
        val runner = stackRunner(StoreConfig(limits = StoreLimits(stackBytes = 256)))
        val args = List(64) { index -> NumberValue.I64(index.toLong()) }

        assertEquals(trap(InvocationError.CallStackExhausted), runner.invoke("wide", args), engine.name())
        assertEquals(ChasmResult.Success(emptyList()), runner.invoke("host", hostArguments()), engine.name())
    }

    @Test
    fun `a host function whose operands exceed the stack limit traps`() = integrationTest(STACK_FIXTURE) {
        val limited = stackRunner(StoreConfig(limits = StoreLimits(stackBytes = 32)))
        assertEquals(trap(InvocationError.CallStackExhausted), limited.invoke("host", hostArguments()), engine.name())

        val runner = stackRunner(StoreConfig(limits = StoreLimits(stackBytes = 64)))
        assertEquals(ChasmResult.Success(emptyList()), runner.invoke("host", hostArguments()), engine.name())
    }

    @Test
    fun `a negative stack limit is rejected`() {
        assertFailsWith<IllegalArgumentException> { StoreLimits(stackBytes = -1) }
    }

    private fun allocate(
        runner: TestRunner,
        function: String,
        count: Int,
    ) = runner.invoke(function, listOf(NumberValue.I32(count), NumberValue.I32(ARRAY_LENGTH)))

    private fun grow(
        runner: TestRunner,
        function: String,
        amount: Int,
    ) = runner.invoke(function, listOf(NumberValue.I32(amount)))

    private fun recurse(
        runner: TestRunner,
        depth: Int,
    ) = runner.invoke("recurse", listOf(NumberValue.I32(depth)))

    private fun hostArguments() = List(8) { index -> NumberValue.I64(index.toLong()) }

    private fun TestScope.stackRunner(
        config: StoreConfig,
    ): TestRunner {
        val store = store(config)
        val hostType = functionType(params = resultType(List(8) { i64ValueType() }))
        val host = Import("env", "host", function(store, hostType, HostFunction { _, _ -> }))
        return runner(store, listOf(host))
    }

    private fun success(
        value: Int,
    ) = ChasmResult.Success(listOf(NumberValue.I32(value)))

    private fun trap(
        error: InvocationError,
    ) = ChasmResult.Error(ChasmError.ExecutionError(error.toString()))

    private companion object {
        const val FIXTURE = "store_limits"
        const val LARGE_MEMORY_FIXTURE = "store_limits_large_memory"
        const val LARGE_TABLE_FIXTURE = "store_limits_large_table"
        const val STACK_FIXTURE = "store_limits_stack"

        // Arrays this long take dedicated storage of 4097 words, about 32 KiB each.
        const val ARRAY_LENGTH = 4096
        const val GC_HEAP_BYTES = 1024L * 1024L
        const val STACK_BYTES = 64L * 1024L
        const val SMALL_STACK_BYTES = 4L * 1024L
        const val DEEP_RECURSION = 1_000
    }
}
