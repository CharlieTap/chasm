package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

// The JVM rejects an array of Int.MAX_VALUE elements without allocating it,
// so these tests reach the platform allocation failure cheaply.
class StoreLimitsJvmTest {

    @Test
    fun `table grow returns minus one when the platform cannot allocate the table`() {
        val (store, instance) = instantiate(StoreConfig())

        assertEquals(success(-1), grow(store, instance, Int.MAX_VALUE - 1))
        assertEquals(success(1), grow(store, instance, 0))
    }

    @Test
    fun `table grow traps when the platform cannot allocate the table and grow failures trap`() {
        val (store, instance) = instantiate(StoreConfig(limits = StoreLimits(trapOnGrowFailure = true)))

        assertEquals(
            ChasmResult.Error(ChasmError.ExecutionError(InvocationError.TableGrowFailed.toString())),
            grow(store, instance, Int.MAX_VALUE - 1),
        )
        assertEquals(success(1), grow(store, instance, 0))
    }

    private fun grow(store: Store, instance: Instance, elements: Int) =
        invoke(store, instance, "table-grow", listOf(NumberValue.I32(elements)))

    private fun instantiate(config: StoreConfig): Pair<Store, Instance> {
        val store = store(config)
        val module = module(Resource("integration/store_limits.wasm").readBytes()).expect("module decodes")
        return store to instance(store, module, emptyList()).expect("module instantiates")
    }

    private fun success(value: Int) = ChasmResult.Success(listOf(NumberValue.I32(value)))
}
