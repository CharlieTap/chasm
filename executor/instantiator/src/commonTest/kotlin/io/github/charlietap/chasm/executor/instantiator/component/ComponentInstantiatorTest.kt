package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.CompiledModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.component.fixture.compiledModule
import io.github.charlietap.chasm.executor.instantiator.component.fixture.extractCoreFunctionComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.fixture.importedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.fixture.instantiateCoreModuleInitializer
import io.github.charlietap.chasm.executor.instantiator.component.fixture.lowerImportComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.fixture.preparedComponent
import io.github.charlietap.chasm.executor.instantiator.component.fixture.preparedCoreImport
import io.github.charlietap.chasm.executor.instantiator.component.fixture.preparedCoreMemoryExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.fixture.resolvedComponentImports
import io.github.charlietap.chasm.executor.instantiator.component.fixture.resolvedCoreModuleImport
import io.github.charlietap.chasm.executor.instantiator.component.fixture.resourceFunctionComponentInitializer
import io.github.charlietap.chasm.executor.invoker.drop.ComponentResourceTableDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceRollback
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.executor.instantiator.component.componentInstantiationFailure
import io.github.charlietap.chasm.fixture.executor.instantiator.component.coreModuleComponentInstantiationFailure
import io.github.charlietap.chasm.fixture.executor.instantiator.component.invalidPreparedComponentInstantiationFailure
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLowerPlan
import io.github.charlietap.chasm.fixture.runtime.component.error.resourceTypeUnavailableComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceCounts
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.guestRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.error.moduleRuntimeError
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.moduleAllocation
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.component.store.ComponentRootSlot
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.instanceLifetimes
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.runtime.instance.import as runtimeImport

class ComponentInstantiatorTest {

    @Test
    fun `executes core initializers in order and publishes a live root`() {
        val modules = List(2) { compiledModule() }
        val instances = List(2) { moduleInstance() }
        val counts = componentInstanceCounts(componentInstances = 1, coreInstances = modules.size)
        val component = preparedComponent(
            modules = modules,
            initializers = modules.indices.map { index -> instantiateCoreModuleInitializer(index) },
            counts = counts,
        )
        val initializedModules = mutableListOf<CompiledModule>()
        val rolledBack = mutableListOf<ModuleInstance>()
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, module, _ ->
            initializedModules += module
            Ok(instances[initializedModules.lastIndex])
        }
        val moduleRollback: ModuleInstanceDropper = { _, instance ->
            rolledBack += instance
            Ok(Unit)
        }
        val componentStore = componentStore()
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            component,
            resolvedComponentImports(),
        )
        val root = componentStore.roots.single()
        val liveState = (root as? ComponentRootSlot.Live)?.instance?.state
        val actual = InstantiationObservation(
            result = result,
            initializedModules = initializedModules,
            rolledBack = rolledBack,
            rootLive = root is ComponentRootSlot.Live,
            instances = liveState?.coreInstances?.toList(),
            componentInstanceParents = liveState?.states?.parents?.toList(),
        )

        val expected = InstantiationObservation(
            result = Ok(componentRootAddress()),
            initializedModules = modules,
            rolledBack = emptyList(),
            rootLive = true,
            instances = instances,
            componentInstanceParents = listOf(-1),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `an imported core module applies the linked import projection`() {
        val embeddedModule = compiledModule()
        val linkedModule = compiledModule()
        val providerInstance = moduleInstance(
            memAddresses = mutableListOf(memoryAddress(10), memoryAddress(20)),
        )
        val linkedInstance = moduleInstance()
        val initializedModules = mutableListOf<CompiledModule>()
        var linkedImports = emptyList<Import>()
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, module, imports ->
            initializedModules += module
            if (module === linkedModule) linkedImports = imports
            Ok(if (module === embeddedModule) providerInstance else linkedInstance)
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            error("core module rollback must not run")
        }
        val componentStore = componentStore()
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(
                modules = listOf(embeddedModule),
                initializers = listOf(
                    instantiateCoreModuleInitializer(),
                    instantiateCoreModuleInitializer(
                        index = 1,
                        module = importedCoreModuleSource(),
                        imports = listOf(
                            preparedCoreImport(
                                moduleName = "environment",
                                entityName = "first",
                                value = preparedCoreMemoryExternalValue(exportIndex = 0),
                            ),
                            preparedCoreImport(
                                moduleName = "environment",
                                entityName = "second",
                                value = preparedCoreMemoryExternalValue(exportIndex = 1),
                            ),
                        ),
                    ),
                ),
                counts = componentInstanceCounts(componentInstances = 1, coreInstances = 2),
            ),
            resolvedComponentImports(
                arrayOf(
                    resolvedCoreModuleImport(
                        module = linkedModule,
                        importIndexes = intArrayOf(1),
                    ),
                ),
            ),
        )
        val root = componentStore.roots.single()
        val actual = ImportedModuleInstantiationObservation(
            result = result,
            initializedModules = initializedModules,
            linkedImports = linkedImports,
            rootLive = root is ComponentRootSlot.Live,
            instances = (root as? ComponentRootSlot.Live)?.instance?.state?.coreInstances?.toList(),
        )

        val expected = ImportedModuleInstantiationObservation(
            result = Ok(componentRootAddress()),
            initializedModules = listOf(embeddedModule, linkedModule),
            linkedImports = listOf(
                runtimeImport(
                    moduleName = "environment",
                    entityName = "second",
                    externalValue = memoryExternalValue(memoryAddress(20)),
                ),
            ),
            rootLive = true,
            instances = listOf(providerInstance, linkedInstance),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `rolls back initialized modules in reverse and leaves a dead root on failure`() {
        val modules = List(3) { compiledModule() }
        val instances = List(2) { moduleInstance() }
        val error = moduleRuntimeError()
        val initializedModules = mutableListOf<CompiledModule>()
        val rolledBack = mutableListOf<ModuleInstance>()
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, module, _ ->
            initializedModules += module
            when (initializedModules.lastIndex) {
                0 -> Ok(instances[0])
                1 -> Ok(instances[1])
                else -> Err(error)
            }
        }
        val moduleRollback: ModuleInstanceDropper = { _, instance ->
            rolledBack += instance
            Ok(Unit)
        }
        val componentStore = componentStore()
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(
                modules = modules,
                initializers = modules.indices.map { index -> instantiateCoreModuleInitializer(index) },
                counts = componentInstanceCounts(componentInstances = 1, coreInstances = modules.size),
            ),
            resolvedComponentImports(),
        )
        val actual = FailureObservation(
            result = result,
            initializedModules = initializedModules,
            rolledBack = rolledBack,
            rootDead = componentStore.roots.single() === ComponentRootSlot.Dead,
        )

        val expected = FailureObservation(
            result = Err(coreModuleComponentInstantiationFailure(error)),
            initializedModules = modules,
            rolledBack = instances.asReversed(),
            rootDead = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `drains component resources before rolling back initialized modules`() {
        val modules = List(2) { compiledModule() }
        val instance = moduleInstance()
        val error = moduleRuntimeError()
        val events = mutableListOf<String>()
        var instantiation = 0
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            if (instantiation++ == 0) Ok(instance) else Err(error)
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            events += "module"
            Ok(Unit)
        }
        val resourceTableDropper: FailedComponentResourceDropper = { _, _, _, _, _ ->
            events += "resources"
            Ok(Unit)
        }
        val componentStore = componentStore()
        val subject = componentInstantiator(moduleInstantiator, moduleRollback, resourceTableDropper)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(
                modules = modules,
                initializers = modules.indices.map(::instantiateCoreModuleInitializer),
                counts = componentInstanceCounts(componentInstances = 1, coreInstances = modules.size),
            ),
            resolvedComponentImports(),
        )
        val actual = FailedResourceCleanupObservation(
            result = result,
            events = events,
            root = componentStore.roots.single(),
        )

        val expected = FailedResourceCleanupObservation(
            result = Err(coreModuleComponentInstantiationFailure(error)),
            events = listOf("resources", "module"),
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `retains a failed root while the host owns one of its resources`() {
        val modules = List(2) { compiledModule() }
        val instance = moduleInstance(allocation = moduleAllocation())
        val error = moduleRuntimeError()
        val rolledBack = mutableListOf<ModuleInstance>()
        var instantiation = 0
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            if (instantiation++ == 0) Ok(instance) else Err(error)
        }
        val moduleRollback: ModuleInstanceDropper = { _, instance ->
            rolledBack += instance
            Ok(Unit)
        }
        val resourceTableDropper: FailedComponentResourceDropper = { _, _, componentStore, root, _ ->
            val type = componentStore.resourceTypes.define(guestRuntimeResourceType(root = root))
            componentStore.hostResourceHandles.insertOwn(type, 42)
            Ok(Unit)
        }
        val componentStore = componentStore()
        val subject = componentInstantiator(moduleInstantiator, moduleRollback, resourceTableDropper)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(
                modules = modules,
                initializers = modules.indices.map(::instantiateCoreModuleInitializer),
                counts = componentInstanceCounts(componentInstances = 1, coreInstances = modules.size),
            ),
            resolvedComponentImports(),
        )
        val root = componentStore.roots.single()
        val actual = RetainedFailedRootObservation(
            result = result,
            rootState = root.rootState,
            deallocated = (root as? ComponentRootSlot.Retained)?.instance?.state?.deallocated,
            rolledBack = rolledBack,
        )

        val expected = RetainedFailedRootObservation(
            result = Err(coreModuleComponentInstantiationFailure(error)),
            rootState = io.github.charlietap.chasm.runtime.component.store.ComponentRootState.Retained,
            deallocated = false,
            rolledBack = emptyList(),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `rollback preserves a core module whose start allocation may have escaped`() {
        val modules = List(2) { compiledModule() }
        val instance = moduleInstance()
        val allocation = moduleAllocation()
        val error = moduleRuntimeError()
        val store = store()
        var instantiation = 0
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            if (instantiation++ == 0) {
                store.instanceLifetimes().apply {
                    begin(instance, emptyList())
                    register(instance, allocation)
                    publish(instance, allocationMayHaveEscaped = true)
                }
                Ok(instance)
            } else {
                Err(error)
            }
        }
        val componentStore = componentStore()
        val subject = componentInstantiator(moduleInstantiator, ::ModuleInstanceRollback)

        val result = subject(
            runtimeConfig(),
            store,
            componentStore,
            preparedComponent(
                modules = modules,
                initializers = modules.indices.map { index -> instantiateCoreModuleInitializer(index) },
                counts = componentInstanceCounts(componentInstances = 1, coreInstances = modules.size),
            ),
            resolvedComponentImports(),
        )
        val actual = EscapedAllocationRollbackObservation(
            result = result,
            deallocated = instance.deallocated,
            teardownAllocation = store.instanceLifetimes().prepareTeardown(instance),
            root = componentStore.roots.single(),
        )

        val expected = EscapedAllocationRollbackObservation(
            result = Err(coreModuleComponentInstantiationFailure(error)),
            deallocated = false,
            teardownAllocation = Ok(allocation),
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a lower import installs a stack function and publishes a live root`() {
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            error("core module instantiation must not run")
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            error("core module rollback must not run")
        }
        val componentStore = componentStore()
        val store = store()
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store,
            componentStore,
            preparedComponent(
                runtimeInfo = componentRuntimeInfo(
                    callPlans = listOf(linearMemoryLowerPlan()),
                ),
                initializers = listOf(lowerImportComponentInitializer()),
                counts = componentInstanceCounts(componentInstances = 1, coreFunctions = 1),
            ),
            resolvedComponentImports(),
        )
        val root = componentStore.roots.single()
        val state = (root as? ComponentRootSlot.Live)?.instance?.state
        val actual = LowerImportObservation(
            result = result,
            rootLive = root is ComponentRootSlot.Live,
            stackFunctionInstalled = store.functions.singleOrNull() is FunctionInstance.StackFunction,
            coreFunctions = state?.coreFunctions?.toList(),
        )

        val expected = LowerImportObservation(
            result = Ok(componentRootAddress()),
            rootLive = true,
            stackFunctionInstalled = true,
            coreFunctions = listOf(0),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `imported guest resource types retain their distinct origin roots on publication`() {
        val componentStore = componentStore()
        val providerState = componentRuntimeState()
        val provider = componentStore.reserveRoot(providerState)
        componentStore.publishRoot(provider, runtimeComponentInstance(state = providerState))
        val resourceType = componentStore.resourceTypes.define(guestRuntimeResourceType(root = provider))
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            error("core module instantiation must not run")
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            error("core module rollback must not run")
        }
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(counts = componentInstanceCounts(resourceTypes = 1)),
            resolvedComponentImports(resourceTypes = intArrayOf(resourceType.address)),
        )
        val instance = (componentStore.roots[1] as? ComponentRootSlot.Live)?.instance
        val actual = ResourceProviderInstantiationObservation(
            result = result,
            providers = instance?.allocation?.rootProviders,
            dependantCount = componentStore.dependantCount(provider),
        )

        val expected = ResourceProviderInstantiationObservation(
            result = Ok(componentRootAddress(1)),
            providers = setOf(provider),
            dependantCount = 1,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `failed instantiation does not retain imported resource origins`() {
        val componentStore = componentStore()
        val providerState = componentRuntimeState()
        val provider = componentStore.reserveRoot(providerState)
        componentStore.publishRoot(provider, runtimeComponentInstance(state = providerState))
        val resourceType = componentStore.resourceTypes.define(guestRuntimeResourceType(root = provider))
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            error("core module instantiation must not run")
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            error("core module rollback must not run")
        }
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(counts = componentInstanceCounts(resourceTypes = 1, hostFunctions = 1)),
            resolvedComponentImports(resourceTypes = intArrayOf(resourceType.address)),
        )
        val actual = FailedResourceProviderInstantiationObservation(
            result = result,
            dependantCount = componentStore.dependantCount(provider),
            root = componentStore.roots[1],
        )

        val expected = FailedResourceProviderInstantiationObservation(
            result = Err(
                invalidPreparedComponentInstantiationFailure(
                    "resolved host function count does not match prepared component state",
                ),
            ),
            dependantCount = 0,
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects an imported resource type after its provider is unavailable`() {
        val componentStore = componentStore()
        val resourceType = runtimeResourceTypeAddress(7)
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            error("core module instantiation must not run")
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            error("core module rollback must not run")
        }
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val actual = subject(
            runtimeConfig(),
            store(),
            componentStore,
            preparedComponent(counts = componentInstanceCounts(resourceTypes = 1)),
            resolvedComponentImports(resourceTypes = intArrayOf(resourceType.address)),
        )

        val expected = Err(
            componentInstantiationFailure(resourceTypeUnavailableComponentInstantiationError(resourceType)),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `failed instantiation retires allocated resource intrinsics`() {
        val store = store()
        val componentStore = componentStore()
        val moduleInstantiator: CompiledModuleInstantiator = { _, _, _, _ ->
            error("core module instantiation must not run")
        }
        val moduleRollback: ModuleInstanceDropper = { _, _ ->
            error("core module rollback must not run")
        }
        val subject = componentInstantiator(moduleInstantiator, moduleRollback)

        val result = subject(
            runtimeConfig(),
            store,
            componentStore,
            preparedComponent(
                initializers = listOf(
                    resourceFunctionComponentInitializer(),
                    extractCoreFunctionComponentInitializer(functionIndex = 1),
                ),
                counts = componentInstanceCounts(componentInstances = 1, coreFunctions = 2),
            ),
            resolvedComponentImports(),
        )
        val function = store.functions.single() as FunctionInstance.StackFunction
        val invocation = runCatching {
            function.body(vstack(), cstack(), store, executionContext(store = store))
        }.exceptionOrNull() as InvocationException
        val actual = FailedResourceFunctionInstantiationObservation(
            result = result,
            invocation = invocation.error,
            root = componentStore.roots.single(),
        )

        val expected = FailedResourceFunctionInstantiationObservation(
            result = Err(invalidPreparedComponentInstantiationFailure("canonical initializer refers to an unresolved core export")),
            invocation = InvocationError.InvocationOfADeinstantiatedInstance,
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }
}

private fun componentInstantiator(
    moduleInstantiator: CompiledModuleInstantiator,
    moduleRollback: ModuleInstanceDropper,
    resourceTableDropper: FailedComponentResourceDropper = ::ComponentResourceTableDropper,
): ComponentInstantiator = { config, store, componentStore, component, imports ->
    ComponentInstantiator(
        config = config,
        store = store,
        componentStore = componentStore,
        component = component,
        imports = imports,
        moduleInstantiator = moduleInstantiator,
        moduleRollback = moduleRollback,
        resourceTableDropper = resourceTableDropper,
    )
}

private data class InstantiationObservation(
    val result: Any,
    val initializedModules: List<CompiledModule>,
    val rolledBack: List<ModuleInstance>,
    val rootLive: Boolean,
    val instances: List<ModuleInstance?>?,
    val componentInstanceParents: List<Int>?,
)

private data class LowerImportObservation(
    val result: Any,
    val rootLive: Boolean,
    val stackFunctionInstalled: Boolean,
    val coreFunctions: List<Int>?,
)

private data class FailureObservation(
    val result: Any,
    val initializedModules: List<CompiledModule>,
    val rolledBack: List<ModuleInstance>,
    val rootDead: Boolean,
)

private data class FailedResourceCleanupObservation(
    val result: Any,
    val events: List<String>,
    val root: ComponentRootSlot,
)

private data class RetainedFailedRootObservation(
    val result: Any,
    val rootState: io.github.charlietap.chasm.runtime.component.store.ComponentRootState,
    val deallocated: Boolean?,
    val rolledBack: List<ModuleInstance>,
)

private data class ImportedModuleInstantiationObservation(
    val result: Any,
    val initializedModules: List<CompiledModule>,
    val linkedImports: List<Import>,
    val rootLive: Boolean,
    val instances: List<ModuleInstance?>?,
)

private data class EscapedAllocationRollbackObservation(
    val result: Any,
    val deallocated: Boolean,
    val teardownAllocation: Any,
    val root: ComponentRootSlot,
)

private data class ResourceProviderInstantiationObservation(
    val result: Any,
    val providers: Set<io.github.charlietap.chasm.runtime.address.ComponentRootAddress>?,
    val dependantCount: Int,
)

private data class FailedResourceProviderInstantiationObservation(
    val result: Any,
    val dependantCount: Int,
    val root: ComponentRootSlot,
)

private data class FailedResourceFunctionInstantiationObservation(
    val result: Any,
    val invocation: Any,
    val root: ComponentRootSlot,
)
