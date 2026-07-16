package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ComponentStoreResourceDropper
import io.github.charlietap.chasm.executor.invoker.drop.MemoryInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.exceptionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.hostInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleAllocation
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.sharedStatus
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.store.ComponentRootSlot
import io.github.charlietap.chasm.runtime.component.store.ComponentRootState
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.instanceLifetimes
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class DropStoreTest {

    @Test
    fun `drops live module instances in reverse allocation order`() {
        val store = publicStore()
        val first = moduleInstance()
        val second = moduleInstance()
        val lifetimes = store.store.instanceLifetimes()
        listOf(first, second).forEach { instance ->
            lifetimes.begin(instance, emptyList())
            lifetimes.register(instance, moduleAllocation())
            lifetimes.publish(instance)
        }
        val dropped = mutableListOf<ModuleInstance>()
        val instanceDropper: ModuleInstanceDropper = { actualStore, instance ->
            assertEquals(store.store, actualStore)
            dropped += instance
            Ok(Unit)
        }
        val memoryDropper: MemoryInstanceDropper = {}

        val actual = dropStore(store, memoryDropper, instanceDropper)

        assertEquals(ChasmResult.Success(Unit), actual)
        val expected = listOf(second, first)
        assertEquals(expected, dropped)
    }

    @Test
    fun `calling dropStore tears down components while roots are live before core cleanup`() {
        val store = publicStore()
        val state = componentRuntimeState()
        val componentStore = store.componentStore()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(root, runtimeComponentInstance(state = state))
        val liveRoot = componentStore.root(root)
        val coreInstance = moduleInstance()
        store.store.instanceLifetimes().apply {
            begin(coreInstance, emptyList())
            register(coreInstance, moduleAllocation())
            publish(coreInstance)
        }
        val callbacks = mutableListOf<ComponentStoreDropCallback>()
        val memoryDropper: MemoryInstanceDropper = {}
        val componentStoreResourceDropper: ComponentStoreResourceDropper = { _, actualComponentStore ->
            callbacks += ComponentStoreDropCallback(
                event = "resources",
                store = null,
                componentStore = actualComponentStore,
                root = null,
                instance = null,
                stateDeallocated = state.deallocated,
                rootState = componentStore.root(root),
            )
            Ok(Unit)
        }
        val componentInstanceDropper: ComponentInstanceDropper = { actualStore, actualComponentStore, actualRoot ->
            callbacks += ComponentStoreDropCallback(
                event = "component",
                store = actualStore,
                componentStore = actualComponentStore,
                root = actualRoot,
                instance = null,
                stateDeallocated = state.deallocated,
                rootState = componentStore.root(root),
            )
            Ok(Unit)
        }
        val instanceDropper: ModuleInstanceDropper = { actualStore, actualInstance ->
            callbacks += ComponentStoreDropCallback(
                event = "core",
                store = actualStore,
                componentStore = null,
                root = null,
                instance = actualInstance,
                stateDeallocated = state.deallocated,
                rootState = componentStore.root(root),
            )
            Ok(Unit)
        }

        val result = dropStore(
            store,
            memoryDropper,
            instanceDropper,
            componentInstanceDropper,
            componentStoreResourceDropper,
        )
        val actual = ComponentStoreDropObservation(
            result = result,
            stateDeallocated = state.deallocated,
            root = componentStore.root(root),
            callbacks = callbacks,
        )

        val expected = ComponentStoreDropObservation(
            result = ChasmResult.Success(Unit),
            stateDeallocated = true,
            root = ComponentRootSlot.Dead,
            callbacks = listOf(
                ComponentStoreDropCallback(
                    event = "resources",
                    store = null,
                    componentStore = componentStore,
                    root = null,
                    instance = null,
                    stateDeallocated = false,
                    rootState = liveRoot,
                ),
                ComponentStoreDropCallback(
                    event = "component",
                    store = store.store,
                    componentStore = componentStore,
                    root = root,
                    instance = null,
                    stateDeallocated = false,
                    rootState = liveRoot,
                ),
                ComponentStoreDropCallback(
                    event = "core",
                    store = store.store,
                    componentStore = null,
                    root = null,
                    instance = coreInstance,
                    stateDeallocated = true,
                    rootState = ComponentRootSlot.Dead,
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `calling dropStore wipes all the associated state in the store`() {

        val referenceValue = hostReferenceValue().toLong()

        val dataInstance = dataInstance(
            bytes = ubyteArrayOf(1u, 2u),
        )
        val exceptionInstance = exceptionInstance(
            fields = longArrayOf(referenceValue),
        )
        val elementInstance = elementInstance(
            elements = longArrayOf(referenceValue, referenceValue),
        )
        val globalInstance = globalInstance(
            value = referenceValue,
        )
        val memoryInstance = memoryInstance(
            type = memoryType(
                shared = sharedStatus(),
            ),
        )
        val tableInstance = tableInstance(
            elements = longArrayOf(referenceValue, referenceValue),
        )
        val instruction = dispatchableInstruction()

        val store = publicStore(
            store(
                data = mutableListOf(dataInstance),
                exceptions = mutableListOf(exceptionInstance),
                elements = mutableListOf(elementInstance),
                globals = mutableListOf(globalInstance),
                hosts = mutableListOf(hostInstance()),
                memories = mutableListOf(memoryInstance),
                tables = mutableListOf(tableInstance),
                functions = mutableListOf(functionInstance()),
                instructions = mutableListOf(instruction),
                tags = mutableListOf(tagInstance()),
            ),
        )
        store.store.heap.arrayReferencePool.add(1)
        store.store.heap.structReferencePool.add(1)
        store.store.heap.sizeInBytes = 16

        var memoryDropped = false
        val memoryDropper: MemoryInstanceDropper = { _memoryInstance ->
            assertEquals(memoryInstance, _memoryInstance)
            memoryDropped = true
        }

        val expected = ChasmResult.Success(Unit)
        val instanceDropper: ModuleInstanceDropper = { _, _ -> Ok(Unit) }
        val actual = dropStore(store, memoryDropper, instanceDropper)

        assertEquals(expected, actual)
        assertEquals(true, memoryDropped)
        assertContentEquals(ubyteArrayOf(), dataInstance.bytes)
        assertContentEquals(longArrayOf(), exceptionInstance.fields)
        assertContentEquals(longArrayOf(), elementInstance.elements)
        assertEquals(ExecutionValue.Uninitialised.toLongFromBoxed(), globalInstance.value)
        assertContentEquals(longArrayOf(), tableInstance.elements)
        assertEquals(0, store.store.data.size)
        assertEquals(0, store.store.exceptions.size)
        assertEquals(0, store.store.elements.size)
        assertEquals(0, store.store.functions.size)
        assertEquals(0, store.store.instructions.size)
        assertEquals(0, store.store.globals.size)
        assertEquals(0, store.store.hosts.size)
        assertEquals(0, store.store.memories.size)
        assertEquals(0, store.store.tables.size)
        assertEquals(0, store.store.tags.size)
        assertEquals(0, store.store.heap.arrayReferencePool.size)
        assertEquals(0, store.store.heap.structReferencePool.size)
        assertEquals(0, store.store.heap.sizeInBytes)
    }

    @Test
    fun `calling dropStore rejects an active component call without changing the store`() {
        val store = publicStore()
        val state = componentRuntimeState()
        val componentStore = store.componentStore()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(root, runtimeComponentInstance(state = state))
        componentStore.enterCall(root)

        val result = dropStore(store)
        val actual = ActiveStoreDropObservation(
            result = result,
            rootState = componentStore.root(root)?.rootState,
            deallocated = state.deallocated,
        )
        componentStore.exitCall()

        val expected = ActiveStoreDropObservation(
            result = ChasmResult.Error(
                ChasmError.ExecutionError(ComponentInvocationError.InstanceActive.toString()),
            ),
            rootState = ComponentRootState.Live,
            deallocated = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `calling dropStore preserves component identities when the store is reused`() {
        val store = publicStore()
        val componentStore = store.componentStore()
        val firstState = componentRuntimeState()
        val firstRoot = componentStore.reserveRoot(firstState)
        componentStore.publishRoot(firstRoot, runtimeComponentInstance(state = firstState))
        val firstType = componentStore.resourceTypes.define(hostRuntimeResourceType())
        val firstPayload = componentStore.hostResourcePayloads().insert("first")
        val firstHandle = componentStore.hostResourceHandles.insertOwn(firstType, firstPayload)

        val result = dropStore(store)
        val retainedStore = store.componentStore()
        val secondState = componentRuntimeState()
        val secondRoot = retainedStore.reserveRoot(secondState)
        val secondType = retainedStore.resourceTypes.define(hostRuntimeResourceType())
        val secondPayload = retainedStore.hostResourcePayloads().insert("second")
        val secondHandle = retainedStore.hostResourceHandles.insertOwn(secondType, secondPayload)
        val actual = ReusedStoreIdentityObservation(
            result = result,
            sameComponentStore = retainedStore === componentStore,
            roots = listOf(firstRoot.address, secondRoot.address),
            resourceTypes = listOf(firstType.address, secondType.address),
            changedHandleGeneration = (firstHandle.id shr 32) != (secondHandle.id shr 32),
        )

        val expected = ReusedStoreIdentityObservation(
            result = ChasmResult.Success(Unit),
            sameComponentStore = true,
            roots = listOf(0, 1),
            resourceTypes = listOf(0, 1),
            changedHandleGeneration = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `calling dropStore tears down a retained failed component root`() {
        val store = publicStore()
        val componentStore = store.componentStore()
        val state = componentRuntimeState()
        val root = componentStore.reserveRoot(state)
        componentStore.retainRoot(root, runtimeComponentInstance(state = state))

        val result = dropStore(store)
        val actual = RetainedRootDropObservation(
            result = result,
            root = componentStore.root(root),
            deallocated = state.deallocated,
        )

        val expected = RetainedRootDropObservation(
            result = ChasmResult.Success(Unit),
            root = ComponentRootSlot.Dead,
            deallocated = true,
        )
        assertEquals(expected, actual)
    }
}

private data class ComponentStoreDropObservation(
    val result: ChasmResult<Unit, ChasmError.ExecutionError>,
    val stateDeallocated: Boolean,
    val root: ComponentRootSlot?,
    val callbacks: List<ComponentStoreDropCallback>,
)

private data class ComponentStoreDropCallback(
    val event: String,
    val store: RuntimeStore?,
    val componentStore: ComponentStore?,
    val root: ComponentRootAddress?,
    val instance: ModuleInstance?,
    val stateDeallocated: Boolean,
    val rootState: ComponentRootSlot?,
)

private data class ActiveStoreDropObservation(
    val result: ChasmResult<Unit, ChasmError.ExecutionError>,
    val rootState: ComponentRootState?,
    val deallocated: Boolean,
)

private data class ReusedStoreIdentityObservation(
    val result: ChasmResult<Unit, ChasmError.ExecutionError>,
    val sameComponentStore: Boolean,
    val roots: List<Int>,
    val resourceTypes: List<Int>,
    val changedHandleGeneration: Boolean,
)

private data class RetainedRootDropObservation(
    val result: ChasmResult<Unit, ChasmError.ExecutionError>,
    val root: ComponentRootSlot?,
    val deallocated: Boolean,
)
