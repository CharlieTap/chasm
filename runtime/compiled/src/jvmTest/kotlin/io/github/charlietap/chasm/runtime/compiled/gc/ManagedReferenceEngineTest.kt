package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.runtime.store.Store
import java.lang.reflect.Field
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ManagedReferenceEngineTest {

    private val engine = Proxy.newProxyInstance(Engine::class.java.classLoader, arrayOf(Engine::class.java)) { _, _, _ ->
        error("the engine is only compared")
    } as Engine

    @Test
    fun `a function reference reports the engine of its store`() {

        val store = Store()
        store.functions.add(hostFunctionInstance())
        val managedStore = managedStore(store)

        val function = managedStore.functions.at(0)

        assertSame(engine, function.engine())
    }

    @Test
    fun `an exception reports the engine of its store`() {

        val store = Store()
        managedStore(store)

        val exception = WasmException(store, 0, LongArray(0), arrayOfNulls(0))

        assertSame(engine, exception.engine())
    }

    @Test
    fun `an exception whose store has been dropped reports no engine`() {

        val exception = WasmException(Store(), 0, LongArray(0), arrayOfNulls(0))

        assertFailsWith<IllegalStateException> {
            exception.engine()
        }
    }

    private fun managedStore(store: Store): ManagedStore {

        val managedStore = ManagedStore(store, UnusedShapeAccessors())
        managedStore.engine = engine
        store.referenceStorage = managedStore

        return managedStore
    }
}

private class UnusedShapeAccessors : ShapeAccessors {

    override fun struct(
        shape: Class<*>,
        fields: List<Field>,
    ): StructAccessor {
        error("no shapes are loaded")
    }

    override fun array(
        shape: Class<*>,
        data: Field,
    ): ArrayAccessor {
        error("no shapes are loaded")
    }
}
