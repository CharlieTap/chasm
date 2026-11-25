package io.github.charlietap.chasm.embedding.compiled.internal

import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.WasmException
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ManagedReferenceEngineTest {

    @Test
    fun `a function reference reports the engine of its store`() {

        val store = Store()
        store.functions.add(hostFunctionInstance())
        val managedStore = ManagedStore(store)
        val engine = engine(store, managedStore)

        val function = managedStore.functions.at(0)

        assertSame(engine, function.engine())
    }

    @Test
    fun `an exception reports the engine of its store`() {

        val store = Store()
        val engine = engine(store, ManagedStore(store))

        val exception = WasmException(store, 0, LongArray(0), arrayOfNulls(0))

        assertSame(engine, exception.engine())
    }

    @Test
    fun `an exception whose store has been dropped reports no engine`() {

        val store = Store()
        val engine = engine(store, ManagedStore(store))
        val exception = WasmException(store, 0, LongArray(0), arrayOfNulls(0))

        engine.dropStore()

        assertFailsWith<IllegalStateException> {
            exception.engine()
        }
    }

    private fun engine(
        store: Store,
        managedStore: ManagedStore,
    ): ManagedStoreEngine {
        return ManagedStoreEngine(StoreRuntime(store), managedStore) { instances ->
            FakeManagedInstantiator(instances, store.config, moduleInstance())
        }
    }
}
