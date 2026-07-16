package io.github.charlietap.chasm.executor.invoker.component.canonical

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CanonicalCallContextTest {

    @Test
    fun `realloc rejects a zero-sized region beyond the end of memory`() {
        val memory = memoryInstance(
            type = memoryType(limits = limits(min = 1u)),
            data = LinearMemoryFactory(LinearMemory.Pages(1u)),
        )
        val store = store(memories = mutableListOf(memory))
        val state = componentRuntimeState(
            memories = intArrayOf(MEMORY_ADDRESS),
            reallocs = intArrayOf(REALLOC_ADDRESS),
        )
        val componentStore = componentStore()
        val scope = componentStore.enterCall()
        val coreInvoker: RawFunctionInvoker = { _, _, _, _, _, _, results ->
            results[0] = memory.size.toLong() + 1L
            Ok(1)
        }
        val context = CanonicalCallContext(
            config = runtimeConfig(),
            store = store,
            componentStore = componentStore,
            root = componentRootAddress(),
            owner = runtimeComponentInstanceIndex(),
            runtimeInfo = componentRuntimeInfo(),
            state = state,
            encoding = CanonicalStringEncoding.Utf8,
            memorySlot = 0,
            reallocSlot = 0,
            scratch = scope.scratch,
            scope = scope,
            coreInvoker = coreInvoker,
        )

        val actual = assertFailsWith<InvocationException> {
            context.realloc(alignment = 1, size = 0)
        }.error
        componentStore.exitCall()

        val expected = InvocationError.MemoryOperationOutOfBounds
        assertEquals(expected, actual)
    }
}

private const val MEMORY_ADDRESS = 0
private const val REALLOC_ADDRESS = 0
