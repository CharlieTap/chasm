package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.KB
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MB
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.growTable
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.integration.COMPILED
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.assertTrapped
import io.github.charlietap.chasm.integration.excludingManagedHeaps
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class StoreLimitsTest {

    @Test
    fun `a metered store runs without fuel being added`() = integrationTest(FUEL_FIXTURE) {
        val runner = runner(RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(100.fuel))))

        assertEquals(success(0), count(runner, 10), engine.name())
        assertEquals(89L, remainingFuel(runner.store), engine.name())
    }

    @Test
    fun `addFuel tops up a metered store`() = integrationTest(FUEL_FIXTURE) {
        val runner = runner(RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(5.fuel))))

        assertTrapped(TrapReason.FUEL_EXHAUSTED, count(runner, 10), engine.name())

        addFuel(runner.store, 11)

        assertEquals(success(0), count(runner, 10), engine.name())
        assertEquals(0L, remainingFuel(runner.store), engine.name())
    }

    @Test
    fun `retaining more than the gc heap limit traps and the store recovers once released`() = integrationTest(FIXTURE, excludingManagedHeaps(FIXTURE)) {
        val runner = runner(memoryLimits(MemoryLimits(gcHeap = GC_HEAP)))

        assertTrapped(TrapReason.GUEST_HEAP_EXHAUSTED, allocate(runner, "retain", 64), engine.name())

        runner.invoke("release").expect("release succeeds")
        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "retain", 8), engine.name())
    }

    @Test
    fun `collection reclaims garbage before the gc heap limit traps`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(gcHeap = GC_HEAP)))

        assertEquals(ChasmResult.Success(emptyList()), allocate(runner, "churn", 256), engine.name())
    }

    @Test
    fun `memory grow returns minus one past the store limit`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages)))

        assertEquals(success(1), grow(runner, "memory-grow", 2), engine.name())
        assertEquals(success(-1), grow(runner, "memory-grow", 1), engine.name())
        assertEquals(success(3), grow(runner, "memory-grow", 0), engine.name())
    }

    @Test
    fun `memory grow traps past the store limit when grow failures trap`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages, onGrowthFailure = GrowthFailureStrategy.Trap)))

        assertEquals(success(1), grow(runner, "memory-grow", 2), engine.name())
        assertTrapped(TrapReason.MEMORY_GROW_FAILED, grow(runner, "memory-grow", 1), engine.name())
    }

    @Test
    fun `host memory growth respects the store limit and still returns minus one`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages, onGrowthFailure = GrowthFailureStrategy.Trap)))
        val memory = assertNotNull(runner.exports().expect("exports").memory("memory"), engine.name())

        assertEquals(-1, growMemory(memory, 3), engine.name())
        assertEquals(1, growMemory(memory, 2), engine.name())
    }

    @Test
    fun `a host-created memory grows only to the limit`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages)))
        val memory = memory(runner.store, memoryType(limits = limits(1u)))

        assertEquals(1, growMemory(memory, 2), engine.name())
        assertEquals(-1, growMemory(memory, 1), engine.name())
        assertEquals(3, growMemory(memory, 0), engine.name())
    }

    @Test
    fun `a host-created memory above the limit is refused`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages)))

        assertFailsWith<IllegalArgumentException>(engine.name()) {
            memory(runner.store, memoryType(limits = limits(4u)))
        }
    }

    @Test
    fun `a host-created memory at the limit is created`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages)))
        val memory = memory(runner.store, memoryType(limits = limits(3u)))

        assertEquals(3, growMemory(memory, 0), engine.name())
        assertEquals(-1, growMemory(memory, 1), engine.name())
    }

    @Test
    fun `instantiating a module whose memory starts above the store limit fails`() = integrationTest(LARGE_MEMORY_FIXTURE, mapOf(COMPILED to setOf(LARGE_MEMORY_FIXTURE))) {
        val result = runner(memoryLimits(MemoryLimits(linearMemory = 3.pages))).instance()

        assertTrapped(TrapReason.MEMORY_LIMIT_EXCEEDED, result, engine.name())
    }

    @Test
    fun `table grow returns minus one past the store limit`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(tables = 32.bytes)))

        assertEquals(success(1), grow(runner, "table-grow", 3), engine.name())
        assertEquals(success(-1), grow(runner, "table-grow", 1), engine.name())
    }

    @Test
    fun `table grow traps past the store limit when grow failures trap`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(tables = 32.bytes, onGrowthFailure = GrowthFailureStrategy.Trap)))

        assertTrapped(TrapReason.TABLE_GROW_FAILED, grow(runner, "table-grow", 4), engine.name())
    }

    @Test
    fun `a host-created table grows only to the limit`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(tables = 32.bytes)))
        val table = table(runner.store, tableType(referenceType = FUNCREF, limits = limits(1u)), null)

        assertEquals(1, growTable(table, 3, null), engine.name())
        assertEquals(-1, growTable(table, 1, null), engine.name())
    }

    @Test
    fun `a host-created table above the limit is refused`() = integrationTest(FIXTURE) {
        val runner = runner(memoryLimits(MemoryLimits(tables = 32.bytes)))

        assertFailsWith<IllegalArgumentException>(engine.name()) {
            table(runner.store, tableType(referenceType = FUNCREF, limits = limits(5u)), null)
        }
    }

    @Test
    fun `instantiating a module whose table starts above the store limit fails`() = integrationTest(LARGE_TABLE_FIXTURE, mapOf(COMPILED to setOf(LARGE_TABLE_FIXTURE))) {
        val result = runner(memoryLimits(MemoryLimits(tables = 800.bytes))).instance()

        assertTrapped(TrapReason.TABLE_LIMIT_EXCEEDED, result, engine.name())
    }

    @Test
    fun `runaway recursion traps once the stack reaches the store limit and the store recovers`() = integrationTest(STACK_FIXTURE) {
        val runner = stackRunner(STACK)

        assertTrapped(TrapReason.CALL_STACK_EXHAUSTED, runner.invoke("runaway"), engine.name())
        assertEquals(success(100), recurse(runner, 100), engine.name())
    }

    @Test
    fun `recursion deeper than the stack limit allows traps`() = integrationTest(STACK_FIXTURE) {
        val runner = stackRunner(STACK)

        assertEquals(success(100), recurse(runner, 100), engine.name())
        assertTrapped(TrapReason.CALL_STACK_EXHAUSTED, recurse(runner, 100_000), engine.name())
    }

    @Test
    fun `the default stack limit admits recursion that a small limit rejects`() = integrationTest(DEEP_STACK_FIXTURE) {
        val defaults = runner(RuntimeConfig())
        val small = runner(memoryLimits(MemoryLimits(stack = 4.KB)))

        assertEquals(success(DEEP_RECURSION), recurse(defaults, DEEP_RECURSION), engine.name())
        assertTrapped(TrapReason.CALL_STACK_EXHAUSTED, recurse(small, DEEP_RECURSION), engine.name())
    }

    @Test
    fun `a call whose first frame exceeds the stack limit traps`() = integrationTest(STACK_FIXTURE) {
        val runner = stackRunner(256.bytes)
        val args = List(64) { index -> NumberValue.I64(index.toLong()) }

        assertTrapped(TrapReason.CALL_STACK_EXHAUSTED, runner.invoke("wide", args), engine.name())
        assertEquals(ChasmResult.Success(emptyList()), runner.invoke("host", hostArguments()), engine.name())
    }

    @Test
    fun `a host function whose operands exceed the stack limit traps`() = integrationTest(STACK_FIXTURE) {
        val limited = stackRunner(32.bytes)
        assertTrapped(TrapReason.CALL_STACK_EXHAUSTED, limited.invoke("host", hostArguments()), engine.name())

        val runner = stackRunner(64.bytes)
        assertEquals(ChasmResult.Success(emptyList()), runner.invoke("host", hostArguments()), engine.name())
    }

    private fun memoryLimits(memory: MemoryLimits): RuntimeConfig {
        return RuntimeConfig(limits = Limits(memory = memory))
    }

    private fun count(
        runner: TestRunner,
        times: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke("count", listOf(NumberValue.I32(times)))
    }

    private fun allocate(
        runner: TestRunner,
        function: String,
        count: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke(function, listOf(NumberValue.I32(count), NumberValue.I32(ARRAY_LENGTH)))
    }

    private fun grow(
        runner: TestRunner,
        function: String,
        amount: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke(function, listOf(NumberValue.I32(amount)))
    }

    private fun recurse(
        runner: TestRunner,
        depth: Int,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return runner.invoke("recurse", listOf(NumberValue.I32(depth)))
    }

    private fun hostArguments(): List<ExecutionValue> {
        return List(8) { index -> NumberValue.I64(index.toLong()) }
    }

    private suspend fun TestScope.stackRunner(stack: Bytes): TestRunner {

        val hostType = functionType(params = resultType(List(8) { i64ValueType() }))

        return runner(memoryLimits(MemoryLimits(stack = stack))) { store ->
            listOf(Import("env", "host", function(store, hostType, HostFunction { _, _ -> })))
        }
    }

    private fun success(value: Int): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return ChasmResult.Success(listOf(NumberValue.I32(value)))
    }

    private companion object {
        const val FIXTURE = "store_limits"
        const val FUEL_FIXTURE = "fuel"
        const val LARGE_MEMORY_FIXTURE = "store_limits_large_memory"
        const val LARGE_TABLE_FIXTURE = "store_limits_large_table"
        const val STACK_FIXTURE = "store_limits_stack"
        const val DEEP_STACK_FIXTURE = "store_limits_deep_stack"

        // Arrays this long take dedicated storage of 4097 words, about 32 KiB each.
        const val ARRAY_LENGTH = 4096
        const val DEEP_RECURSION = 1_000

        val GC_HEAP = 1.MB
        val STACK = 64.KB
        val FUNCREF = ReferenceType.RefNull(AbstractHeapType.Func)
    }
}
