package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.fixture.INTERRUPT_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostResources
import kotlinx.coroutines.test.runTest
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs

class InterruptTest {

    @Test
    fun `interrupting a store that is not interruptible throws illegal state`() {

        val store = interpreterStore()

        assertFailsWith<IllegalStateException> {
            interrupt(store)
        }
    }

    @Test
    fun `interrupting a store with no call running has no effect`() {

        val store = interpreterStore(store(config = StoreConfig(interruptible = true)))

        assertFalse(interrupt(store))
    }

    @Test
    fun `a call interrupted while it runs traps`() = runTest {

        val store = interpreterStore(store(config = StoreConfig(interruptible = true)))
        val requested = mutableListOf<Boolean>()
        val hostFunction = HostFunction { _, _ ->
            requested += interrupt(contextOf<HostResources>().store)
        }
        val import = Import("host", "interrupt", function(store, functionType(), hostFunction))
        val instance = instantiate(store, INTERRUPT_MODULE, listOf(import))

        val result = invoke(instance, "run")

        val error = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(result).error)
        assertEquals(TrapReason.INTERRUPTED, error.trap.reason)
        assertEquals(listOf(true), requested)
    }
}
