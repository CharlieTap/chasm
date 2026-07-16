package io.github.charlietap.chasm.runtime.component.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.instance.RuntimeComponentInstance
import io.github.charlietap.chasm.runtime.component.resource.HostResourceHandleTable
import io.github.charlietap.chasm.runtime.component.resource.HostResourcePayloadTable
import io.github.charlietap.chasm.runtime.component.resource.ResourceTypeTable
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType

class ComponentStore {
    private val mutableRoots = mutableListOf<ComponentRootSlot>()
    private var rootDependants = IntArray(INITIAL_ROOT_CAPACITY)
    private var rootOwnedResources = IntArray(INITIAL_ROOT_CAPACITY)
    private val callScopes = mutableListOf<ComponentCallScope>()
    private var callDepth = 0
    private var nextCallGeneration = FIRST_CALL_GENERATION
    private var mutableHostResourcePayloads: HostResourcePayloadTable? = null

    val resourceTypes = ResourceTypeTable()
    val hostResourceHandles = HostResourceHandleTable(
        onOwnInserted = ::recordOwnedResource,
        onOwnRemoved = ::releaseOwnedResource,
    )

    private val rootView = object : AbstractList<ComponentRootSlot>() {
        override val size: Int
            get() = mutableRoots.size

        override fun get(index: Int): ComponentRootSlot = mutableRoots[index]
    }

    val roots: List<ComponentRootSlot>
        get() = rootView

    fun root(address: ComponentRootAddress): ComponentRootSlot? = mutableRoots.getOrNull(address.address)

    fun reserveRoot(
        state: ComponentRuntimeState,
        config: RuntimeConfig = RuntimeConfig(),
        rootProviders: Set<ComponentRootAddress> = emptySet(),
    ): ComponentRootAddress {
        val address = ComponentRootAddress(mutableRoots.size)
        if (address.address == rootDependants.size) {
            rootDependants = rootDependants.copyOf(rootDependants.size shl 1)
            rootOwnedResources = rootOwnedResources.copyOf(rootOwnedResources.size shl 1)
        }
        rootDependants[address.address] = 0
        rootOwnedResources[address.address] = 0
        rootProviders.forEach { provider ->
            require(root(provider) is ComponentRootSlot.Live)
        }
        rootProviders.forEach { provider ->
            rootDependants[provider.address] += 1
        }
        mutableRoots += ComponentRootSlot.Initializing(config, state, rootProviders)
        return address
    }

    fun publishRoot(
        address: ComponentRootAddress,
        instance: RuntimeComponentInstance,
    ): Result<Unit, ComponentInstantiationError> = publishRoot(
        address,
        instance,
        ComponentRootSlot.Live(instance),
        ComponentRootState.Live,
    )

    fun retainRoot(
        address: ComponentRootAddress,
        instance: RuntimeComponentInstance,
    ): Result<Unit, ComponentInstantiationError> = publishRoot(
        address,
        instance,
        ComponentRootSlot.Retained(instance),
        ComponentRootState.Retained,
    )

    fun retainLiveRoot(address: ComponentRootAddress): Result<Unit, ComponentInvocationError> {
        val slot = root(address)
            ?: return Err(ComponentInvocationError.RootNotFound(address))
        return when (slot) {
            is ComponentRootSlot.Live -> {
                mutableRoots[address.address] = ComponentRootSlot.Retained(slot.instance)
                Ok(Unit)
            }
            is ComponentRootSlot.Retained -> Ok(Unit)
            is ComponentRootSlot.Initializing -> Err(ComponentInvocationError.RootInitializing(address))
            ComponentRootSlot.Dead -> Err(ComponentInvocationError.RootDead(address))
        }
    }

    private fun publishRoot(
        address: ComponentRootAddress,
        instance: RuntimeComponentInstance,
        published: ComponentRootSlot,
        target: ComponentRootState,
    ): Result<Unit, ComponentInstantiationError> {
        val slot = root(address)
            ?: return Err(ComponentInstantiationError.RootNotFound(address))
        if (slot !is ComponentRootSlot.Initializing) {
            return Err(
                ComponentInstantiationError.InvalidRootTransition(
                    address = address,
                    current = slot.rootState,
                    target = target,
                ),
            )
        }
        if (slot.state !== instance.state) {
            return Err(ComponentInstantiationError.RootRuntimeStateMismatch(address))
        }

        instance.allocation.rootProviders.forEach { provider ->
            if (root(provider) !is ComponentRootSlot.Live) {
                return Err(ComponentInstantiationError.RootProviderUnavailable(provider))
            }
        }

        mutableRoots[address.address] = published
        return Ok(Unit)
    }

    fun markRootDead(address: ComponentRootAddress): Result<Unit, ComponentInstantiationError> {
        val slot = root(address)
            ?: return Err(ComponentInstantiationError.RootNotFound(address))
        if (slot === ComponentRootSlot.Dead) return Ok(Unit)

        when (slot) {
            is ComponentRootSlot.Initializing -> {
                slot.rootProviders.forEach { provider ->
                    check(rootDependants[provider.address] > 0)
                    rootDependants[provider.address] -= 1
                }
                slot.state.deallocated = true
            }
            is ComponentRootSlot.Live -> {
                slot.instance.allocation.rootProviders.forEach { provider ->
                    check(rootDependants[provider.address] > 0)
                    rootDependants[provider.address] -= 1
                }
                slot.instance.state.deallocated = true
            }
            is ComponentRootSlot.Retained -> {
                slot.instance.allocation.rootProviders.forEach { provider ->
                    check(rootDependants[provider.address] > 0)
                    rootDependants[provider.address] -= 1
                }
                slot.instance.state.deallocated = true
            }
            ComponentRootSlot.Dead -> Unit
        }

        mutableRoots[address.address] = ComponentRootSlot.Dead
        return Ok(Unit)
    }

    fun runtimeState(address: ComponentRootAddress): Result<ComponentRuntimeState, ComponentInvocationError> =
        when (val slot = root(address)) {
            null -> Err(ComponentInvocationError.RootNotFound(address))
            is ComponentRootSlot.Initializing -> Ok(slot.state)
            is ComponentRootSlot.Live -> Ok(slot.instance.state)
            is ComponentRootSlot.Retained -> Ok(slot.instance.state)
            ComponentRootSlot.Dead -> Err(ComponentInvocationError.RootDead(address))
        }

    fun runtimeConfig(address: ComponentRootAddress): Result<RuntimeConfig, ComponentInvocationError> =
        when (val slot = root(address)) {
            null -> Err(ComponentInvocationError.RootNotFound(address))
            is ComponentRootSlot.Initializing -> Ok(slot.config)
            is ComponentRootSlot.Live -> Ok(slot.instance.config)
            is ComponentRootSlot.Retained -> Ok(slot.instance.config)
            ComponentRootSlot.Dead -> Err(ComponentInvocationError.RootDead(address))
        }

    fun enterCall(
        caller: RuntimeComponentInstanceIndex? = null,
    ): ComponentCallScope = enterCall(
        root = ComponentRootAddress(ABSENT_ROOT),
        caller = caller,
        callee = null,
    )

    fun enterCall(
        root: ComponentRootAddress,
        caller: RuntimeComponentInstanceIndex? = null,
        callee: RuntimeComponentInstanceIndex? = null,
    ): ComponentCallScope {
        val depth = callDepth++
        return (callScopes.getOrNull(depth) ?: ComponentCallScope().also(callScopes::add)).also { scope ->
            scope.enter(root, caller, callee, takeCallToken(depth))
        }
    }

    fun exitCall() {
        check(callDepth > 0)
        callScopes[callDepth - 1].exit()
        callDepth -= 1
    }

    fun currentCallScope(): ComponentCallScope {
        check(callDepth > 0)
        return callScopes[callDepth - 1]
    }

    fun currentCallScopeOrNull(): ComponentCallScope? = if (callDepth == 0) {
        null
    } else {
        callScopes[callDepth - 1]
    }

    fun isCallActive(token: ComponentCallToken): Boolean {
        return callScope(token) != null
    }

    fun callScope(token: ComponentCallToken): ComponentCallScope? {
        val encodedDepth = token.token and CALL_DEPTH_MASK
        if (encodedDepth == 0uL || encodedDepth > callDepth.toULong()) return null
        return callScopes[encodedDepth.toInt() - 1].takeIf { scope -> scope.callToken == token }
    }

    fun hasActiveCalls(): Boolean = callDepth != 0

    fun isRootActive(address: ComponentRootAddress): Boolean {
        var index = callDepth - 1
        while (index >= 0) {
            if (callScopes[index].root == address) return true
            index -= 1
        }
        return false
    }

    fun hostResourcePayloads(): HostResourcePayloadTable = mutableHostResourcePayloads
        ?: HostResourcePayloadTable().also { table -> mutableHostResourcePayloads = table }

    fun hostResourcePayloadsOrNull(): HostResourcePayloadTable? = mutableHostResourcePayloads

    fun dependantCount(address: ComponentRootAddress): Int = rootDependants.getOrNull(address.address) ?: 0

    fun hasOwnedResources(address: ComponentRootAddress): Boolean =
        rootOwnedResources.getOrNull(address.address)?.let { count -> count != 0 } ?: false

    fun deallocateRoots() {
        mutableRoots.indices.reversed().forEach { index ->
            markRootDead(ComponentRootAddress(index))
        }
    }

    fun liveRoot(address: ComponentRootAddress): Result<RuntimeComponentInstance, ComponentInvocationError> =
        when (val slot = root(address)) {
            null -> Err(ComponentInvocationError.RootNotFound(address))
            is ComponentRootSlot.Initializing -> Err(ComponentInvocationError.RootInitializing(address))
            is ComponentRootSlot.Live -> Ok(slot.instance)
            is ComponentRootSlot.Retained -> Err(ComponentInvocationError.RootDead(address))
            ComponentRootSlot.Dead -> Err(ComponentInvocationError.RootDead(address))
        }

    fun retainedRoot(address: ComponentRootAddress): Result<RuntimeComponentInstance, ComponentInvocationError> =
        when (val slot = root(address)) {
            null -> Err(ComponentInvocationError.RootNotFound(address))
            is ComponentRootSlot.Initializing -> Err(ComponentInvocationError.RootInitializing(address))
            is ComponentRootSlot.Live -> Ok(slot.instance)
            is ComponentRootSlot.Retained -> Ok(slot.instance)
            ComponentRootSlot.Dead -> Err(ComponentInvocationError.RootDead(address))
        }

    private fun takeCallToken(depth: Int): ComponentCallToken {
        require(depth < Int.MAX_VALUE)
        val token = ComponentCallToken(
            (nextCallGeneration.toULong() shl CALL_GENERATION_SHIFT) or (depth + 1).toUInt().toULong(),
        )
        nextCallGeneration += 1u
        if (nextCallGeneration == 0u) nextCallGeneration = FIRST_CALL_GENERATION
        return token
    }

    private fun recordOwnedResource(type: io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress) {
        val root = (resourceTypes[type] as? RuntimeResourceType.Guest)?.root ?: return
        check(root.address in mutableRoots.indices)
        rootOwnedResources[root.address] += 1
    }

    private fun releaseOwnedResource(type: io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress) {
        val root = (resourceTypes[type] as? RuntimeResourceType.Guest)?.root ?: return
        check(rootOwnedResources[root.address] > 0)
        rootOwnedResources[root.address] -= 1
    }
}

private const val ABSENT_ROOT = -1
private const val FIRST_CALL_GENERATION = 1u
private const val CALL_GENERATION_SHIFT = 32
private const val CALL_DEPTH_MASK = 0xffffffffuL
private const val INITIAL_ROOT_CAPACITY = 8
