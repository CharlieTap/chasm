package io.github.charlietap.chasm.runtime.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.RuntimeInstanceId
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleAllocation
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

class InstanceLifetimeRegistry {

    private val entries = mutableListOf<Entry>()
    private val owners = mutableMapOf<Address, RuntimeInstanceId>()
    private var nextProviderMarker = 0L

    fun begin(
        instance: ModuleInstance,
        imports: List<ExternalValue>,
    ): Result<RuntimeInstanceId, ModuleTrapError> {
        val id = RuntimeInstanceId(entries.size)
        val providers = if (imports.isEmpty()) {
            emptyList()
        } else {
            val providerMarker = nextProviderMarker++
            val providers = mutableListOf<RuntimeInstanceId>()
            imports.forEach { import ->
                val provider = owners[import.address()] ?: return@forEach
                val entry = entries.getOrNull(provider.id)
                if (entry == null || !entry.state.importable) {
                    return Err(InstantiationError.ImportedFromDeallocatedInstance)
                }
                if (entry.lastProviderMarker != providerMarker) {
                    entry.lastProviderMarker = providerMarker
                    providers += provider
                }
            }
            providers
        }

        entries += Entry(
            instance = instance,
            providers = providers,
        )
        providers.forEach { provider ->
            entries[provider.id].dependants++
        }
        instance.runtimeInstanceId = id

        return Ok(id)
    }

    fun register(
        instance: ModuleInstance,
        allocation: ModuleAllocation,
    ) {
        val id = requireNotNull(instance.runtimeInstanceId)
        val entry = entries[id.id]

        entry.allocation = allocation
        instance.allocation = allocation
        allocation.functionAddresses.forEach { address -> owners[address] = id }
        allocation.tableAddresses.forEach { address -> owners[address] = id }
        allocation.memoryAddresses.forEach { address -> owners[address] = id }
        allocation.tagAddresses.forEach { address -> owners[address] = id }
        allocation.globalAddresses.forEach { address -> owners[address] = id }
    }

    fun publish(
        instance: ModuleInstance,
        allocationMayHaveEscaped: Boolean = false,
    ) {
        val id = requireNotNull(instance.runtimeInstanceId)
        val entry = entries[id.id]
        entry.allocationMayHaveEscaped = allocationMayHaveEscaped
        entry.state = State.Live
    }

    fun abandon(instance: ModuleInstance) {
        val id = requireNotNull(instance.runtimeInstanceId)
        entries[id.id].state = State.Orphaned
    }

    fun prepareDrop(instance: ModuleInstance): Result<ModuleAllocation?, ModuleTrapError> = prepareRelease(instance, Release.Drop)

    fun prepareRollback(instance: ModuleInstance): Result<ModuleAllocation?, ModuleTrapError> = prepareRelease(instance, Release.Rollback)

    fun prepareTeardown(instance: ModuleInstance): Result<ModuleAllocation?, ModuleTrapError> = prepareRelease(instance, Release.Teardown)

    private fun prepareRelease(
        instance: ModuleInstance,
        release: Release,
    ): Result<ModuleAllocation?, ModuleTrapError> {
        val id = instance.runtimeInstanceId
            ?: return Err(InvocationError.InstanceNotOwnedByStore)
        val entry = entries.getOrNull(id.id)
            ?: return Err(InvocationError.InstanceNotOwnedByStore)

        if (entry.instance !== instance) {
            return Err(InvocationError.InstanceNotOwnedByStore)
        }
        if (instance.deallocated || entry.state == State.Dead) return Ok(null)
        if (entry.state == State.Initializing && release != Release.Rollback) {
            return Err(InvocationError.InvocationOfADeinstantiatedInstance)
        }
        if (entry.state == State.Orphaned && release != Release.Teardown) {
            return Err(InvocationError.InvocationOfADeinstantiatedInstance)
        }
        if (release == Release.Rollback && entry.state == State.Live && entry.allocationMayHaveEscaped) {
            entry.state = State.Orphaned
            return Ok(null)
        }
        if (entry.dependants != 0) {
            return Err(InvocationError.InstanceHasDependants)
        }

        return Ok(requireNotNull(entry.allocation))
    }

    fun completeDrop(instance: ModuleInstance) {
        val id = requireNotNull(instance.runtimeInstanceId)
        val entry = entries[id.id]

        if (entry.state == State.Dead) return

        entry.providers.forEach { provider ->
            entries[provider.id].dependants--
        }
        entry.providers = emptyList()
        entry.allocation = null
        entry.state = State.Dead
        instance.allocation = null
        instance.deallocated = true
    }

    fun providers(instance: ModuleInstance): List<RuntimeInstanceId> {
        val id = requireNotNull(instance.runtimeInstanceId)
        return entries[id.id].providers
    }

    fun allocatedInstancesNewestFirst(): List<ModuleInstance> = entries.asReversed()
        .filter { entry -> entry.state == State.Live || entry.state == State.Orphaned }
        .map(Entry::instance)

    fun owns(address: Address): Boolean = address in owners

    fun clear() {
        entries.clear()
        owners.clear()
    }

    private data class Entry(
        val instance: ModuleInstance,
        var providers: List<RuntimeInstanceId>,
        var allocation: ModuleAllocation? = null,
        var dependants: Int = 0,
        var lastProviderMarker: Long = -1,
        var allocationMayHaveEscaped: Boolean = false,
        var state: State = State.Initializing,
    )

    private enum class State(
        val importable: Boolean,
    ) {
        Initializing(false),
        Live(true),
        Orphaned(true),
        Dead(false),
    }

    private enum class Release {
        Drop,
        Rollback,
        Teardown,
    }
}

private fun ExternalValue.address(): Address = when (this) {
    is ExternalValue.Function -> address
    is ExternalValue.Table -> address
    is ExternalValue.Memory -> address
    is ExternalValue.Tag -> address
    is ExternalValue.Global -> address
}
