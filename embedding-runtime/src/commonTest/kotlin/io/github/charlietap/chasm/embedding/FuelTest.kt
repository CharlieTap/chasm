package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.runtime.store
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FuelTest {

    @Test
    fun `fuel can be added read and reset on a metered store`() {

        val store = meteredStore()

        addFuel(store, 10L)
        addFuel(store, 5L)

        assertEquals(15L, remainingFuel(store))

        resetFuel(store)

        assertEquals(0L, remainingFuel(store))
    }

    @Test
    fun `added fuel saturates instead of overflowing`() {

        val store = meteredStore()

        addFuel(store, Long.MAX_VALUE)
        addFuel(store, 1L)

        assertEquals(Long.MAX_VALUE, remainingFuel(store))
    }

    @Test
    fun `negative fuel is rejected`() {

        val store = meteredStore()

        assertFailsWith<IllegalArgumentException> {
            addFuel(store, -1L)
        }
    }

    @Test
    fun `fuel calls on a store that does not meter fuel throw illegal state`() {

        val store = interpreterStore()

        assertFailsWith<IllegalStateException> { addFuel(store, 1L) }
        assertFailsWith<IllegalStateException> { resetFuel(store) }
        assertFailsWith<IllegalStateException> { remainingFuel(store) }
    }

    @Test
    fun `a call consumes fuel`() = runTest {

        val store = meteredStore()
        addFuel(store, 1_000L)
        val instance = instantiate(store, INSTANCE_MODULE)

        val result = invoke(instance, "identity", listOf(NumberValue.I32(117)))

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(117))), result)
        assertTrue(remainingFuel(store) < 1_000L)
    }

    @Test
    fun `a call that runs out of fuel traps`() = runTest {

        val store = meteredStore()
        addFuel(store, 1_000L)
        val instance = instantiate(store, INSTANCE_MODULE)

        val result = invoke(instance, "spin")

        val error = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(result).error)
        assertEquals(TrapReason.FUEL_EXHAUSTED, error.trap.reason)
    }

    private fun meteredStore(): Store {
        return interpreterStore(store(config = StoreConfig(meterFuel = true)))
    }
}
