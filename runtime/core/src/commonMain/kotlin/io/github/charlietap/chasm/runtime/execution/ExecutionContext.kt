package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostReferences
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

data class ExecutionContext(
    val cstack: ControlStack,
    val vstack: ValueStack,
    val store: Store,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
) : HostResources {
    val heap: WasmHeap = store.heap

    override val references: HostReferences
        get() = heap

    override val gc: HostGc
        get() = heap

    override val externs: HostExterns
        get() = heap

    override fun memory(module: HostModuleInstance, index: Int): HostMemory {
        val address = (module as ModuleInstance).memAddresses[index]
        return store.memories[address.address].data
    }

    override fun table(module: HostModuleInstance, index: Int): HostTable {
        val address = (module as ModuleInstance).tableAddresses[index]
        return store.tables[address.address]
    }

    override fun global(module: HostModuleInstance, index: Int): HostGlobal {
        val address = (module as ModuleInstance).globalAddresses[index]
        return store.globals[address.address]
    }

    override fun tag(module: HostModuleInstance, index: Int): HostTag {
        val address = (module as ModuleInstance).tagAddresses[index]
        return HostTag(address.address)
    }
}
