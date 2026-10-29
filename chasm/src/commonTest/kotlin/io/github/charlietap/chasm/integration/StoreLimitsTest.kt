@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.gc
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
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
    fun `retaining more than the gc heap limit traps and the store recovers once released`() {
        val (store, instance) = instantiate(
            StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES)),
        )

        assertEquals(trap(InvocationError.GuestHeapOutOfMemory), allocate(store, instance, "retain", 64))

        invoke(store, instance, "release").expect("release succeeds")
        assertEquals(ChasmResult.Success(emptyList()), allocate(store, instance, "retain", 8))
    }

    @Test
    fun `traditional collection reclaims garbage before the gc heap limit traps`() {
        val (store, instance) = instantiate(
            StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES)),
        )

        assertEquals(ChasmResult.Success(emptyList()), allocate(store, instance, "churn", 256))
    }

    @Test
    fun `arena traps once allocation since the last collection exceeds the gc heap limit`() {
        val (store, instance) = instantiate(
            StoreConfig(gcStrategy = GCStrategy.ARENA, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES)),
        )

        assertEquals(trap(InvocationError.GuestHeapOutOfMemory), allocate(store, instance, "churn", 256))

        gc(store).expect("collection succeeds")
        assertEquals(ChasmResult.Success(emptyList()), allocate(store, instance, "churn", 8))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `the store gc strategy takes precedence over the instance runtime config`() {
        val (store, instance) = instantiate(
            StoreConfig(gcStrategy = GCStrategy.TRADITIONAL, limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES)),
            RuntimeConfig(gcStrategy = GCStrategy.ARENA),
        )

        assertEquals(ChasmResult.Success(emptyList()), allocate(store, instance, "churn", 256))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `the instance runtime config gc strategy applies when the store sets none`() {
        val (store, instance) = instantiate(
            StoreConfig(limits = StoreLimits(gcHeapBytes = GC_HEAP_BYTES)),
            RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
        )

        assertEquals(ChasmResult.Success(emptyList()), allocate(store, instance, "churn", 256))
    }

    @Test
    fun `memory grow returns minus one past the store limit`() {
        val (store, instance) = instantiate(StoreConfig(limits = StoreLimits(memoryPages = 3)))

        assertEquals(success(1), grow(store, instance, "memory-grow", 2))
        assertEquals(success(-1), grow(store, instance, "memory-grow", 1))
        assertEquals(success(3), grow(store, instance, "memory-grow", 0))
    }

    @Test
    fun `memory grow traps past the store limit when grow failures trap`() {
        val (store, instance) = instantiate(
            StoreConfig(limits = StoreLimits(memoryPages = 3, trapOnGrowFailure = true)),
        )

        assertEquals(success(1), grow(store, instance, "memory-grow", 2))
        assertEquals(trap(InvocationError.MemoryGrowFailed), grow(store, instance, "memory-grow", 1))
    }

    @Test
    fun `host memory growth respects the store limit and still returns minus one`() {
        val (store, instance) = instantiate(
            StoreConfig(limits = StoreLimits(memoryPages = 3, trapOnGrowFailure = true)),
        )
        val memory = instance.exports.first { export -> export.name == "memory" }.value as Memory

        assertEquals(ChasmResult.Success(-1), growMemory(store, memory, 3))
        assertEquals(ChasmResult.Success(1), growMemory(store, memory, 2))
    }

    @Test
    fun `a host created memory keeps its initial size but cannot grow past the store limit`() {
        val store = store(StoreConfig(limits = StoreLimits(memoryPages = 3)))
        val memory = memory(store, MemoryType(AddressType.I32, Limits(5u), SharedStatus.Unshared))

        assertEquals(ChasmResult.Success(-1), growMemory(store, memory, 1))
        assertEquals(ChasmResult.Success(5), growMemory(store, memory, 0))
    }

    @Test
    fun `instantiating a module whose memory starts above the store limit fails`() {
        val store = store(StoreConfig(limits = StoreLimits(memoryPages = 3)))
        val module = module(Resource(FILE_DIR + LARGE_MEMORY_FIXTURE).readBytes()).expect("module decodes")

        val result = instance(store, module, emptyList())

        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result)
        assertTrue(result.error.error.contains("MemoryLimitExceeded"), result.error.error)
        assertEquals(0, store.store.memories.size)
    }

    @Test
    fun `table grow returns minus one past the store limit`() {
        val (store, instance) = instantiate(StoreConfig(limits = StoreLimits(tableElements = 4)))

        assertEquals(success(1), grow(store, instance, "table-grow", 3))
        assertEquals(success(-1), grow(store, instance, "table-grow", 1))
    }

    @Test
    fun `table grow traps past the store limit when grow failures trap`() {
        val (store, instance) = instantiate(
            StoreConfig(limits = StoreLimits(tableElements = 4, trapOnGrowFailure = true)),
        )

        assertEquals(trap(InvocationError.TableGrowFailed), grow(store, instance, "table-grow", 4))
    }

    @Test
    fun `instantiating a module whose table starts above the store limit fails`() {
        val store = store(StoreConfig(limits = StoreLimits(tableElements = 100)))
        val module = module(Resource(FILE_DIR + LARGE_TABLE_FIXTURE).readBytes()).expect("module decodes")

        val result = instance(store, module, emptyList())

        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result)
        assertTrue(result.error.error.contains("TableLimitExceeded"), result.error.error)
        assertEquals(0, store.store.tables.size)
    }

    @Test
    fun `runaway recursion traps once the stack reaches the store limit and the store recovers`() {
        val (store, instance) = instantiateStack(StoreConfig(limits = StoreLimits(stackBytes = STACK_BYTES)))

        assertEquals(trap(InvocationError.CallStackExhausted), invoke(store, instance, "runaway"))
        assertEquals(success(100), recurse(store, instance, 100))
    }

    @Test
    fun `recursion deeper than the stack limit allows traps`() {
        val (store, instance) = instantiateStack(StoreConfig(limits = StoreLimits(stackBytes = STACK_BYTES)))

        assertEquals(success(1000), recurse(store, instance, 1000))
        assertEquals(trap(InvocationError.CallStackExhausted), recurse(store, instance, 100_000))
    }

    @Test
    fun `the default stack limit admits deep recursion`() {
        val (store, instance) = instantiateStack(StoreConfig())

        assertEquals(success(100_000), recurse(store, instance, 100_000))
    }

    @Test
    fun `a call whose first frame exceeds the stack limit traps`() {
        val (store, instance) = instantiateStack(StoreConfig(limits = StoreLimits(stackBytes = 256)))
        val args = List(64) { index -> NumberValue.I64(index.toLong()) }

        assertEquals(trap(InvocationError.CallStackExhausted), invoke(store, instance, "wide", args))
        assertEquals(success(1), recurse(store, instance, 1))
    }

    @Test
    fun `a host function whose operands exceed the stack limit traps`() {
        val args = List(8) { index -> NumberValue.I64(index.toLong()) }

        val (limited, limitedInstance) = instantiateStack(StoreConfig(limits = StoreLimits(stackBytes = 32)))
        assertEquals(trap(InvocationError.CallStackExhausted), invoke(limited, limitedInstance, "host", args))

        val (store, instance) = instantiateStack(StoreConfig(limits = StoreLimits(stackBytes = 64)))
        assertEquals(ChasmResult.Success(emptyList()), invoke(store, instance, "host", args))
    }

    @Test
    fun `a negative stack limit is rejected`() {
        assertFailsWith<IllegalArgumentException> { StoreLimits(stackBytes = -1) }
    }

    private fun allocate(store: Store, instance: Instance, function: String, count: Int) =
        invoke(store, instance, function, listOf(NumberValue.I32(count), NumberValue.I32(ARRAY_LENGTH)))

    private fun grow(store: Store, instance: Instance, function: String, amount: Int) =
        invoke(store, instance, function, listOf(NumberValue.I32(amount)))

    private fun recurse(store: Store, instance: Instance, depth: Int) =
        invoke(store, instance, "recurse", listOf(NumberValue.I32(depth)))

    private fun instantiateStack(config: StoreConfig): Pair<Store, Instance> {
        val store = store(config)
        val hostType = functionType(params = resultType(List(8) { i64ValueType() }))
        val host = Import("env", "host", function(store, hostType, HostFunction { _, _ -> }))
        val module = module(Resource(FILE_DIR + STACK_FIXTURE).readBytes()).expect("module decodes")
        return store to instance(store, module, listOf(host)).expect("module instantiates")
    }

    private fun instantiate(
        config: StoreConfig,
        runtimeConfig: RuntimeConfig = RuntimeConfig(),
    ): Pair<Store, Instance> {
        val store = store(config)
        val module = module(Resource(FILE_DIR + FIXTURE).readBytes()).expect("module decodes")
        return store to instance(store, module, emptyList(), runtimeConfig).expect("module instantiates")
    }

    private fun success(value: Int) = ChasmResult.Success(listOf(NumberValue.I32(value)))

    private fun trap(error: InvocationError) = ChasmResult.Error(ChasmError.ExecutionError(error.toString()))

    companion object {
        private const val FILE_DIR = "integration/"
        private const val FIXTURE = "store_limits.wasm"
        private const val LARGE_MEMORY_FIXTURE = "store_limits_large_memory.wasm"
        private const val LARGE_TABLE_FIXTURE = "store_limits_large_table.wasm"
        private const val STACK_FIXTURE = "store_limits_stack.wasm"

        // Arrays this long take dedicated storage of 4097 words, about 32 KiB each.
        private const val ARRAY_LENGTH = 4096
        private const val GC_HEAP_BYTES = 1024L * 1024L
        private const val STACK_BYTES = 64L * 1024L
    }
}
