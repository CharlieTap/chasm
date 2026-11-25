package io.github.charlietap.chasm.embedding.compiled.internal

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.store.Store
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ManagedStoreEngineTest {

    @Test
    fun `instance asks the instantiator and prepares the instance's exports`() {
        runTest {

            val store = Store()
            store.functions.add(hostFunctionInstance())
            val export = exportInstance(nameValue("run"), functionExternalValue(functionAddress(0)))
            val moduleInstance = moduleInstance(functionAddresses = mutableListOf(functionAddress(0)), exports = mutableListOf(export))
            val module = _module(emptyList(), emptyList(), Any())

            lateinit var instantiator: FakeManagedInstantiator
            val engine = ManagedStoreEngine(StoreRuntime(store), ManagedStore(store)) { instances ->
                FakeManagedInstantiator(instances, store.config, moduleInstance).also { created -> instantiator = created }
            }

            val instance = engine.instance(module, emptyList()).expect("instantiates")

            assertEquals(listOf(module), instantiator.requests)
            assertSame(engine.instanceOf(moduleInstance), instance)
            assertSame(engine.prepareFunction(instance, "run").expect("prepared"), engine.prepareFunction(instance, "run").expect("prepared"))
        }
    }

    @Test
    fun `instance returns the instantiator's error and registers no instance`() {
        runTest {

            val store = Store()
            store.functions.add(hostFunctionInstance())
            val export = exportInstance(nameValue("run"), functionExternalValue(functionAddress(0)))
            val moduleInstance = moduleInstance(functionAddresses = mutableListOf(functionAddress(0)), exports = mutableListOf(export))
            val module = _module(emptyList(), emptyList(), Any())
            val failure = ExecutionError.Unsupported("the instantiator refused the module")

            val engine = ManagedStoreEngine(StoreRuntime(store), ManagedStore(store)) { instances ->
                FakeManagedInstantiator(instances, store.config, moduleInstance, failure)
            }

            val result = engine.instance(module, emptyList())

            assertEquals(ChasmResult.Error(failure), result)
            assertNull(moduleInstance.compiledInstance)
        }
    }

    @Test
    fun `a dropped store refuses every call`() {
        runTest {

            val store = Store()
            lateinit var instantiator: FakeManagedInstantiator
            val engine = ManagedStoreEngine(StoreRuntime(store), ManagedStore(store)) { instances ->
                FakeManagedInstantiator(instances, store.config, moduleInstance()).also { created -> instantiator = created }
            }
            val memory = engine.memory(memoryType())
            val module = _module(emptyList(), emptyList(), Any())

            engine.dropStore()

            val calls = listOf<suspend () -> Unit>(
                { engine.dropStore() },
                { engine.addFuel(1) },
                { engine.resetFuel() },
                { engine.remainingFuel() },
                { engine.interrupt() },
                { engine.collect() },
                { engine.memory(memoryType()) },
                { engine.global(globalType(), NumberValue.I32(0)) },
                { engine.table(tableType(), null) },
                { engine.tag(tagType()) },
                { engine.sizeMemory(memory) },
                { engine.readInt(memory, 0) },
                { engine.instance(module, emptyList()) },
            )

            calls.forEach { call ->
                val failure = assertFailsWith<IllegalStateException> { call() }
                assertEquals(InvocationError.StoreDropped.toString(), failure.message)
            }

            assertTrue(instantiator.requests.isEmpty())
        }
    }
}
