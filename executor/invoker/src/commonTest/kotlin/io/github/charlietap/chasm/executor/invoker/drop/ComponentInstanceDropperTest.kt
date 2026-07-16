package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.error.instanceActiveComponentInvocationError
import io.github.charlietap.chasm.fixture.runtime.component.error.instanceHasDependantsComponentInvocationError
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentAllocation
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.guestRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.component.value.ownComponentResourceValue
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.stackFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.HostResourceHandleId
import io.github.charlietap.chasm.runtime.component.store.ComponentRootSlot
import io.github.charlietap.chasm.runtime.component.store.ComponentRootState
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.identity
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentInstanceDropperTest {

    @Test
    fun `drops owned core instances in reverse order before retiring the root`() {
        val first = moduleInstance()
        val second = moduleInstance()
        val state = componentRuntimeState(coreInstances = arrayOf(first, second))
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(root, runtimeComponentInstance(state = state))
        val dropped = mutableListOf<ModuleInstance>()
        val moduleInstanceDropper: ModuleInstanceDropper = { _, instance ->
            dropped += instance
            Ok(Unit)
        }

        val result = ComponentInstanceDropper(
            store = store(),
            componentStore = componentStore,
            root = root,
            moduleInstanceDropper = moduleInstanceDropper,
        )
        val actual = ComponentDropObservation(
            result = result,
            dropped = dropped,
            root = componentStore.root(root),
        )

        val expected = ComponentDropObservation(
            result = Ok(Unit),
            dropped = listOf(second, first),
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `drops resources then retires stack functions before dropping core instances`() {
        val stackFunction = stackFunctionInstance()
        val instruction = dispatchableInstruction()
        val coreInstance = moduleInstance()
        val store = store(
            functions = mutableListOf(stackFunction),
            instructions = mutableListOf(instruction),
        )
        val state = componentRuntimeState(coreInstances = arrayOf(coreInstance))
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(
            root,
            runtimeComponentInstance(
                state = state,
                allocation = componentAllocation(stackFunctions = listOf(functionAddress())),
            ),
        )
        val callbacks = mutableListOf<ComponentRetirementCallback>()
        val resourceTableDropper: ComponentResourceTableDropper = { _, actualStore, _, _, _ ->
            callbacks += ComponentRetirementCallback(
                event = "resources",
                store = actualStore,
                instance = null,
                stackFunctionActive = store.functions.single() === stackFunction,
            )
            Ok(Unit)
        }
        val moduleInstanceDropper: ModuleInstanceDropper = { actualStore, actualInstance ->
            callbacks += ComponentRetirementCallback(
                event = "core",
                store = actualStore,
                instance = actualInstance,
                stackFunctionActive = store.functions.single() === stackFunction,
            )
            Ok(Unit)
        }

        val result = ComponentInstanceDropper(
            store = store,
            componentStore = componentStore,
            root = root,
            resourceTableDropper = resourceTableDropper,
            moduleInstanceDropper = moduleInstanceDropper,
        )
        val invocation = RawFunctionInvoker(
            config = runtimeConfig(),
            store = store,
            instance = coreInstance,
            address = functionAddress(),
            values = longArrayOf(),
            valueCount = 0,
            results = longArrayOf(),
        )
        val actual = ComponentRetirementObservation(
            result = result,
            callbacks = callbacks,
            stackFunctionRetired = store.functions.single() is FunctionInstance.StackFunction,
            instructionRetired = store.instructions.single() !== instruction,
            invocation = invocation,
            root = componentStore.root(root),
        )

        val expected = ComponentRetirementObservation(
            result = Ok(Unit),
            callbacks = listOf(
                ComponentRetirementCallback("resources", store, null, true),
                ComponentRetirementCallback("core", store, coreInstance, false),
            ),
            stackFunctionRetired = true,
            instructionRetired = true,
            invocation = Err(InvocationError.InvocationOfADeinstantiatedInstance),
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `ordinary drop rejects a resource provider while its dependant is live`() {
        val componentStore = componentStore()
        val providerState = componentRuntimeState()
        val provider = componentStore.reserveRoot(providerState)
        componentStore.publishRoot(provider, runtimeComponentInstance(state = providerState))
        val dependantState = componentRuntimeState()
        val dependant = componentStore.reserveRoot(
            state = dependantState,
            rootProviders = setOf(provider),
        )
        componentStore.publishRoot(
            dependant,
            runtimeComponentInstance(
                state = dependantState,
                allocation = componentAllocation(rootProviders = setOf(provider)),
            ),
        )

        val blocked = ComponentInstanceDropper(store(), componentStore, provider)
        val dependantDrop = ComponentInstanceDropper(store(), componentStore, dependant)
        val providerDrop = ComponentInstanceDropper(store(), componentStore, provider)
        val actual = ComponentDependencyDropObservation(
            blocked = blocked,
            dependantDrop = dependantDrop,
            providerDrop = providerDrop,
            dependantCount = componentStore.dependantCount(provider),
        )

        val expected = ComponentDependencyDropObservation(
            blocked = Err(
                ComponentInstanceDropError.ComponentInvocation(
                    instanceHasDependantsComponentInvocationError(),
                ),
            ),
            dependantDrop = Ok(Unit),
            providerDrop = Ok(Unit),
            dependantCount = 0,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `ordinary drop rejects a guest resource origin while the embedding owns a handle`() {
        val store = store()
        val componentStore = componentStore()
        val state = componentRuntimeState()
        val root = componentStore.reserveRoot(state)
        val type = componentStore.resourceTypes.define(guestRuntimeResourceType(root = root))
        componentStore.publishRoot(
            root,
            runtimeComponentInstance(
                state = state,
                allocation = componentAllocation(resourceTypes = intArrayOf(type.address)),
            ),
        )
        val handle = componentStore.hostResourceHandles.insertOwn(type, 42)
        val resource = ownComponentResourceValue(store.identity(), handle)

        val blocked = ComponentInstanceDropper(store, componentStore, root)
        val resourceDrop = ComponentResourceDropper(store, componentStore, resource)
        val rootDrop = ComponentInstanceDropper(store, componentStore, root)
        val actual = OwnedResourceDependencyObservation(
            blocked = blocked,
            resourceDrop = resourceDrop,
            rootDrop = rootDrop,
        )

        val expected = OwnedResourceDependencyObservation(
            blocked = Err(
                ComponentInstanceDropError.ComponentInvocation(
                    instanceHasDependantsComponentInvocationError(),
                ),
            ),
            resourceDrop = Ok(Unit),
            rootDrop = Ok(Unit),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a resource escaping during teardown retains the root until its final handle is dropped`() {
        val store = store()
        val componentStore = componentStore()
        val state = componentRuntimeState()
        val root = componentStore.reserveRoot(state)
        val type = componentStore.resourceTypes.define(guestRuntimeResourceType(root = root))
        componentStore.publishRoot(
            root,
            runtimeComponentInstance(
                state = state,
                allocation = componentAllocation(resourceTypes = intArrayOf(type.address)),
            ),
        )
        var escapedHandle: HostResourceHandleId? = null
        val resourceTableDropper: ComponentResourceTableDropper = { _, _, actualComponentStore, _, _ ->
            escapedHandle = actualComponentStore.hostResourceHandles.insertOwn(type, 42)
            Ok(Unit)
        }
        val moduleInstanceDropper: ModuleInstanceDropper = { _, _ -> Ok(Unit) }

        val drop = ComponentInstanceDropper(
            store = store,
            componentStore = componentStore,
            root = root,
            resourceTableDropper = resourceTableDropper,
            moduleInstanceDropper = moduleInstanceDropper,
        )
        val retained = componentStore.root(root)?.rootState
        val resource = ownComponentResourceValue(store.identity(), checkNotNull(escapedHandle))
        val resourceDrop = ComponentResourceDropper(store, componentStore, resource)
        val actual = EscapedResourceDropObservation(
            drop = drop,
            retained = retained,
            resourceDrop = resourceDrop,
            finalRoot = componentStore.root(root),
        )

        val expected = EscapedResourceDropObservation(
            drop = Err(
                ComponentInstanceDropError.ComponentInvocation(
                    instanceHasDependantsComponentInvocationError(),
                ),
            ),
            retained = ComponentRootState.Retained,
            resourceDrop = Ok(Unit),
            finalRoot = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `ordinary drop rejects a root with an active call`() {
        val state = componentRuntimeState()
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(root, runtimeComponentInstance(state = state))
        componentStore.enterCall(root)

        val result = ComponentInstanceDropper(store(), componentStore, root)
        val actual = ActiveComponentDropObservation(
            result = result,
            rootState = componentStore.root(root)?.rootState,
            deallocated = state.deallocated,
        )
        componentStore.exitCall()

        val expected = ActiveComponentDropObservation(
            result = Err(
                ComponentInstanceDropError.ComponentInvocation(
                    instanceActiveComponentInvocationError(),
                ),
            ),
            rootState = ComponentRootState.Live,
            deallocated = false,
        )
        assertEquals(expected, actual)
    }
}

private data class ComponentDropObservation(
    val result: com.github.michaelbull.result.Result<Unit, *>,
    val dropped: List<ModuleInstance>,
    val root: ComponentRootSlot?,
)

private data class ComponentRetirementObservation(
    val result: Any,
    val callbacks: List<ComponentRetirementCallback>,
    val stackFunctionRetired: Boolean,
    val instructionRetired: Boolean,
    val invocation: Any,
    val root: ComponentRootSlot?,
)

private data class ComponentRetirementCallback(
    val event: String,
    val store: Store,
    val instance: ModuleInstance?,
    val stackFunctionActive: Boolean,
)

private data class ComponentDependencyDropObservation(
    val blocked: Any,
    val dependantDrop: Any,
    val providerDrop: Any,
    val dependantCount: Int,
)

private data class OwnedResourceDependencyObservation(
    val blocked: Any,
    val resourceDrop: Any,
    val rootDrop: Any,
)

private data class EscapedResourceDropObservation(
    val drop: Any,
    val retained: ComponentRootState?,
    val resourceDrop: Any,
    val finalRoot: ComponentRootSlot?,
)

private data class ActiveComponentDropObservation(
    val result: Any,
    val rootState: ComponentRootState?,
    val deallocated: Boolean,
)
