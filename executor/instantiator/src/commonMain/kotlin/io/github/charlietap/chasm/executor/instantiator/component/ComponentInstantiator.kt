package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.CompiledModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.StackFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExportProjection
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreFunctionSource
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreImport
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.linking.ResolvedComponentImports
import io.github.charlietap.chasm.executor.invoker.component.FusedComponentFunctionBody
import io.github.charlietap.chasm.executor.invoker.component.LoweredComponentFunctionBody
import io.github.charlietap.chasm.executor.invoker.component.resource.CanonicalResourceFunctionBody
import io.github.charlietap.chasm.executor.invoker.drop.ComponentResourceTableDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceRollback
import io.github.charlietap.chasm.executor.invoker.drop.StackFunctionRetirer
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.component.instance.ComponentAllocation
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.instance.RuntimeComponentInstance
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunctionKind
import io.github.charlietap.chasm.runtime.component.resource.RuntimeGuestResourceDestructor
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.component.store.ComponentRootSlot
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiContext
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiSignatureOptions
import io.github.charlietap.chasm.type.component.canonical.CanonicalFunctionType
import io.github.charlietap.chasm.type.ext.functionType

typealias ComponentInstantiator = (
    RuntimeConfig,
    Store,
    ComponentStore,
    PreparedComponent,
    ResolvedComponentImports,
) -> Result<ComponentRootAddress, ComponentInstantiationFailure>

internal typealias FailedComponentResourceDropper = (
    RuntimeConfig,
    Store,
    ComponentStore,
    ComponentRootAddress,
    ComponentRuntimeState,
) -> Result<Unit, ComponentInvocationError>

sealed interface ComponentInstantiationFailure {

    data class Component(
        val error: ComponentInstantiationError,
    ) : ComponentInstantiationFailure

    data class CoreModule(
        val error: ModuleTrapError,
    ) : ComponentInstantiationFailure

    data class ComponentInvocation(
        val error: ComponentInvocationError,
    ) : ComponentInstantiationFailure

    data class InvalidPreparedComponent(
        val reason: String,
    ) : ComponentInstantiationFailure
}

fun ComponentInstantiator(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    component: PreparedComponent,
    imports: ResolvedComponentImports,
): Result<ComponentRootAddress, ComponentInstantiationFailure> = ComponentInstantiator(
    config = config,
    store = store,
    componentStore = componentStore,
    component = component,
    imports = imports,
    moduleInstantiator = ::ModuleInstantiator,
    moduleRollback = ::ModuleInstanceRollback,
    resourceTableDropper = ::ComponentResourceTableDropper,
)

internal inline fun ComponentInstantiator(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    component: PreparedComponent,
    imports: ResolvedComponentImports,
    crossinline moduleInstantiator: CompiledModuleInstantiator,
    crossinline moduleRollback: ModuleInstanceDropper,
    crossinline resourceTableDropper: FailedComponentResourceDropper = ::ComponentResourceTableDropper,
): Result<ComponentRootAddress, ComponentInstantiationFailure> = instantiateComponent(
    config = config,
    store = store,
    componentStore = componentStore,
    component = component,
    imports = imports,
    moduleInstantiator = { runtimeConfig, coreStore, module, coreImports ->
        moduleInstantiator(runtimeConfig, coreStore, module, coreImports)
    },
    moduleRollback = { coreStore, instance -> moduleRollback(coreStore, instance) },
    resourceTableDropper = { runtimeConfig, coreStore, runtimeComponentStore, root, state ->
        resourceTableDropper(runtimeConfig, coreStore, runtimeComponentStore, root, state)
    },
)

@PublishedApi
internal fun instantiateComponent(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    component: PreparedComponent,
    imports: ResolvedComponentImports,
    moduleInstantiator: CompiledModuleInstantiator,
    moduleRollback: ModuleInstanceDropper,
    resourceTableDropper: FailedComponentResourceDropper,
): Result<ComponentRootAddress, ComponentInstantiationFailure> {
    val rootProviders = importedRootProviders(componentStore, imports).fold(
        success = { it },
        failure = { error -> return Err(ComponentInstantiationFailure.Component(error)) },
    )
    rootProviders.forEach { provider ->
        if (componentStore.root(provider) !is ComponentRootSlot.Live) {
            return Err(
                ComponentInstantiationFailure.Component(
                    ComponentInstantiationError.RootProviderUnavailable(provider),
                ),
            )
        }
    }
    val state = ComponentRuntimeState.allocate(
        counts = component.counts,
        componentInstanceParents = component.componentInstanceParents,
    )
    val root = componentStore.reserveRoot(state, config, rootProviders)
    val initialized = mutableListOf<ModuleInstance>()
    val stackFunctions = mutableListOf<Address.Function>()
    val definedResourceTypes = mutableListOf<Int>()
    var failure: ComponentInstantiationFailure? = null

    if (imports.functions.size != state.hostFunctions.size) {
        failure = ComponentInstantiationFailure.InvalidPreparedComponent(
            "resolved host function count does not match prepared component state",
        )
    } else {
        imports.functions.copyInto(state.hostFunctions)
    }
    imports.resourceTypes.copyInto(state.resourceTypes)

    for (initializer in component.initializers) {
        if (failure != null) break
        when (initializer) {
            is ComponentInitializer.InstantiateCoreModule -> {
                val module: CompiledModule?
                val preparedImports: List<PreparedCoreImport>?
                when (val source = initializer.module) {
                    is PreparedCoreModuleSource.Embedded -> {
                        module = component.modules.getOrNull(source.moduleIndex)
                        preparedImports = initializer.imports
                    }
                    is PreparedCoreModuleSource.Import -> {
                        val imported = imports.coreModules.getOrNull(source.importIndex)
                        module = imported?.module
                        preparedImports = imported?.importIndexes?.let(initializer.imports::project)
                    }
                }
                if (module == null || preparedImports == null) {
                    failure = ComponentInstantiationFailure.InvalidPreparedComponent(
                        "core module initializer refers to a missing module",
                    )
                    break
                }

                val coreImports = mutableListOf<Import>()
                for (componentImport in preparedImports) {
                    val external = componentImport.value.resolve(state, imports)
                    if (external == null) {
                        failure = ComponentInstantiationFailure.InvalidPreparedComponent(
                            "core module initializer refers to an unresolved core export",
                        )
                        break
                    }
                    coreImports += Import(
                        moduleName = componentImport.moduleName,
                        entityName = componentImport.entityName,
                        externalValue = external,
                    )
                }
                if (failure != null) break

                moduleInstantiator(config, store, module, coreImports).fold(
                    success = { instance ->
                        state.coreInstances[initializer.instance.index] = instance
                        initialized += instance
                    },
                    failure = { error ->
                        failure = ComponentInstantiationFailure.CoreModule(error)
                    },
                )
                if (failure != null) break
            }
            is ComponentInitializer.ExtractCoreFunction -> {
                val function = initializer.function.resolve(state, imports) as? ExternalValue.Function
                if (function == null) {
                    failure = unresolvedCanonicalDependency()
                    break
                }
                state.coreFunctions[initializer.slot.index] = function.address.address
            }
            is ComponentInitializer.ExtractMemory -> {
                val memory = initializer.memory.resolve(state, imports) as? ExternalValue.Memory
                if (memory == null) {
                    failure = unresolvedCanonicalDependency()
                    break
                }
                state.memories[initializer.slot] = memory.address.address
            }
            is ComponentInitializer.ExtractRealloc -> {
                val function = initializer.function.resolve(state, imports) as? ExternalValue.Function
                if (function == null) {
                    failure = unresolvedCanonicalDependency()
                    break
                }
                state.reallocs[initializer.slot] = function.address.address
            }
            is ComponentInitializer.ExtractPostReturn -> {
                val function = initializer.function.resolve(state, imports) as? ExternalValue.Function
                if (function == null) {
                    failure = unresolvedCanonicalDependency()
                    break
                }
                state.postReturns[initializer.slot] = function.address.address
            }
            is ComponentInitializer.LowerImport -> {
                val plan = component.runtimeInfo.callPlans.getOrNull(initializer.callPlan) as? LinearMemoryLowerPlan
                val functionType = plan?.let { lowerPlan ->
                    CanonicalFunctionType(
                        type = lowerPlan.functionType,
                        options = CanonicalAbiSignatureOptions(addressType = AddressType.I32),
                        context = CanonicalAbiContext.Lower,
                    )?.functionType()
                }
                if (plan == null || functionType == null) {
                    failure = ComponentInstantiationFailure.InvalidPreparedComponent(
                        "canonical lower initializer has no executable function type",
                    )
                    break
                }
                val external = StackFunctionAllocator(
                    store = store,
                    functionType = functionType,
                    body = if (plan.fusedTarget == null) {
                        LoweredComponentFunctionBody(
                            componentStore = componentStore,
                            root = root,
                            runtimeInfo = component.runtimeInfo,
                            plan = plan,
                        )
                    } else {
                        FusedComponentFunctionBody(
                            componentStore = componentStore,
                            root = root,
                            runtimeInfo = component.runtimeInfo,
                            plan = plan,
                        )
                    },
                )
                state.coreFunctions[initializer.function.index] = external.address.address
                stackFunctions += external.address
            }
            is ComponentInitializer.DefineResourceType -> {
                val destructor = initializer.destructor?.let { source ->
                    val function = source.resolve(state, imports) as? ExternalValue.Function
                    val instance = source.instance(state)
                    if (function == null || instance == null) {
                        null
                    } else {
                        RuntimeGuestResourceDestructor(instance, function.address)
                    }
                }
                if (initializer.destructor != null && destructor == null) {
                    failure = unresolvedCanonicalDependency()
                    break
                }
                val address = componentStore.resourceTypes.define(
                    RuntimeResourceType.Guest(
                        root = root,
                        owner = initializer.owner,
                        destructor = destructor,
                    ),
                )
                state.resourceTypes[initializer.resourceType.index] = address.address
                definedResourceTypes += address.address
            }
            is ComponentInitializer.ResourceFunction -> {
                val external = StackFunctionAllocator(
                    store = store,
                    functionType = initializer.resource.functionType(),
                    body = CanonicalResourceFunctionBody(
                        componentStore = componentStore,
                        root = root,
                        state = state,
                        function = initializer.resource,
                    ),
                )
                state.coreFunctions[initializer.function.index] = external.address.address
                stackFunctions += external.address
            }
        }
    }

    val allocations = initialized.mapNotNull(ModuleInstance::allocation)
    val instance = RuntimeComponentInstance(
        config = config,
        runtimeInfo = component.runtimeInfo,
        state = state,
        allocation = ComponentAllocation(
            coreModules = allocations,
            stackFunctions = stackFunctions,
            providers = allocations.flatMap { allocation -> allocation.providers },
            rootProviders = rootProviders,
            resourceTypes = definedResourceTypes.toIntArray(),
        ),
    )
    if (failure == null) {
        componentStore.publishRoot(root, instance).fold(
            success = { return Ok(root) },
            failure = { error -> failure = ComponentInstantiationFailure.Component(error) },
        )
    }

    var rollbackFailure: ComponentInstantiationFailure? = null
    resourceTableDropper(config, store, componentStore, root, state).fold(
        success = {},
        failure = { error -> rollbackFailure = ComponentInstantiationFailure.ComponentInvocation(error) },
    )

    if (componentStore.hasOwnedResources(root)) {
        componentStore.retainRoot(root, instance).fold(
            success = { return Err(checkNotNull(rollbackFailure ?: failure)) },
            failure = { error ->
                if (rollbackFailure == null) rollbackFailure = ComponentInstantiationFailure.Component(error)
            },
        )
    }

    initialized.asReversed().forEach { instance ->
        moduleRollback(store, instance).fold(
            success = {},
            failure = { error ->
                if (rollbackFailure == null) rollbackFailure = ComponentInstantiationFailure.CoreModule(error)
            },
        )
    }
    stackFunctions.asReversed().forEach { address ->
        StackFunctionRetirer(store, address)
    }
    componentStore.resourceTypes.discard(definedResourceTypes.toIntArray())
    state.deallocated = true
    componentStore.markRootDead(root).fold(
        success = {},
        failure = { error ->
            if (rollbackFailure == null) rollbackFailure = ComponentInstantiationFailure.Component(error)
        },
    )

    return Err(checkNotNull(rollbackFailure ?: failure))
}

private fun importedRootProviders(
    componentStore: ComponentStore,
    imports: ResolvedComponentImports,
): Result<Set<ComponentRootAddress>, ComponentInstantiationError> {
    val providers = linkedSetOf<ComponentRootAddress>()
    imports.functions.forEach { function ->
        val linked = function as? RuntimeComponentHostFunction.Linked ?: return@forEach
        providers += linked.root
    }
    imports.resourceTypes.forEach { address ->
        if (address < 0) return@forEach
        val resourceTypeAddress = RuntimeResourceTypeAddress(address)
        when (val type = componentStore.resourceTypes[resourceTypeAddress]) {
            null -> return Err(ComponentInstantiationError.ResourceTypeUnavailable(resourceTypeAddress))
            is RuntimeResourceType.Guest -> providers += type.root
            is RuntimeResourceType.Host -> Unit
        }
    }
    return Ok(providers)
}

private fun io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunction.functionType(): FunctionType {
    val i32 = ValueType.Number(NumberType.I32)
    return when (kind) {
        CanonicalResourceFunctionKind.ResourceNew,
        CanonicalResourceFunctionKind.ResourceRep,
        -> FunctionType(ResultType(listOf(i32)), ResultType(listOf(i32)))
        CanonicalResourceFunctionKind.ResourceDrop -> FunctionType(ResultType(listOf(i32)), ResultType(emptyList()))
    }
}

private fun PreparedCoreExternalValue.resolve(
    state: ComponentRuntimeState,
    imports: ResolvedComponentImports,
): ExternalValue? {
    return when (this) {
        is PreparedCoreExternalValue.Function -> when (val function = source) {
            is PreparedCoreFunctionSource.Export -> {
                val instance = state.coreInstances.getOrNull(function.instance.index) ?: return null
                val index = function.projection.resolve(imports) ?: return null
                instance.functionAddresses.getOrNull(index)?.let(ExternalValue::Function)
            }
            is PreparedCoreFunctionSource.Lowered ->
                state.coreFunctions
                    .getOrNull(function.function.index)
                    ?.takeIf { address -> address >= 0 }
                    ?.let { address ->
                        ExternalValue.Function(io.github.charlietap.chasm.runtime.address.Address.Function(address))
                    }
        }
        is PreparedCoreExternalValue.Table -> {
            val instance = state.coreInstances.getOrNull(instance.index) ?: return null
            val index = projection.resolve(imports) ?: return null
            instance.tableAddresses.getOrNull(index)?.let(ExternalValue::Table)
        }
        is PreparedCoreExternalValue.Memory -> {
            val instance = state.coreInstances.getOrNull(instance.index) ?: return null
            val index = projection.resolve(imports) ?: return null
            instance.memAddresses.getOrNull(index)?.let(ExternalValue::Memory)
        }
        is PreparedCoreExternalValue.Global -> {
            val instance = state.coreInstances.getOrNull(instance.index) ?: return null
            val index = projection.resolve(imports) ?: return null
            instance.globalAddresses.getOrNull(index)?.let(ExternalValue::Global)
        }
        is PreparedCoreExternalValue.Tag -> {
            val instance = state.coreInstances.getOrNull(instance.index) ?: return null
            val index = projection.resolve(imports) ?: return null
            instance.tagAddresses.getOrNull(index)?.let(ExternalValue::Tag)
        }
    }
}

private fun PreparedCoreExternalValue.Function.instance(state: ComponentRuntimeState): ModuleInstance? = when (val function = source) {
    is PreparedCoreFunctionSource.Export -> state.coreInstances.getOrNull(function.instance.index)
    is PreparedCoreFunctionSource.Lowered -> state.adapterInstance
}

private fun PreparedCoreExportProjection.resolve(imports: ResolvedComponentImports): Int? = when (this) {
    is PreparedCoreExportProjection.Direct -> index
    is PreparedCoreExportProjection.Imported ->
        imports.coreModules
            .getOrNull(moduleImportIndex)
            ?.exportIndexes
            ?.getOrNull(exportIndex)
}

private fun unresolvedCanonicalDependency(): ComponentInstantiationFailure =
    ComponentInstantiationFailure.InvalidPreparedComponent("canonical initializer refers to an unresolved core export")

private fun List<PreparedCoreImport>.project(indexes: IntArray): List<PreparedCoreImport>? {
    val projected = ArrayList<PreparedCoreImport>(indexes.size)
    indexes.forEach { index -> projected += getOrNull(index) ?: return null }
    return projected
}
