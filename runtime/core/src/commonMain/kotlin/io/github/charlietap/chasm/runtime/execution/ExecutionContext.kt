package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.host.HostExceptions
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostReferences
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.jvm.JvmField

data class ExecutionContext(
    val vstack: ValueStack,
    val store: Store,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
) : HostResources {
    val heap: WasmHeap = store.heap

    @JvmField
    var compiledCallDepth: Int = store.compiledCallDepth

    @JvmField
    var compiledStackLimit: Int = minOf(
        CompiledStack.budget.toLong(),
        store.config.limits.stackBytes?.div(CompiledStack.UNIT_BYTES) ?: Long.MAX_VALUE,
    ).toInt()

    @JvmField
    var pendingTailFunction: FunctionInstance? = null

    @JvmField
    var pendingTailCaller: ModuleInstance? = null

    @JvmField
    var pendingTailArguments: LongArray? = null

    @JvmField
    var pendingTailReferences: Array<Any?>? = null

    @JvmField
    var managedException: Any? = null

    @JvmField
    var resultReferences: Array<Any?> = arrayOfNulls(0)

    @JvmField
    val allocation: AllocationMeter = store.allocation

    @JvmField
    var compiledUnwinding: Boolean = false

    @JvmField
    var nestedInterpreter: NestedInterpreter? = null

    override val references: HostReferences
        get() = store.referenceBridge?.references ?: heap

    override val gc: HostGc
        get() = store.referenceBridge?.gc ?: heap

    override val externs: HostExterns
        get() = store.referenceBridge?.externs ?: heap

    override val exceptions: HostExceptions
        get() = store.referenceBridge?.exceptions ?: heap

    override fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory {
        val address = (module as ModuleInstance).memAddresses[index.index]
        return store.memories[address.address].data
    }

    override fun growMemory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex, pagesToAdd: Int): Int {
        val address = (module as ModuleInstance).memAddresses[index.index]
        return store.memories[address.address].grow(pagesToAdd)
    }

    override fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable {
        val address = (module as ModuleInstance).tableAddresses[index.index]
        val table = store.tables[address.address]
        return store.referenceBridge?.table(table) ?: table
    }

    override fun growTable(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int {
        val address = (module as ModuleInstance).tableAddresses[index.index]
        val table = store.tables[address.address]
        return store.referenceBridge?.growTable(table, elementsToAdd, value) ?: table.grow(elementsToAdd, value)
    }

    override fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal {
        val address = (module as ModuleInstance).globalAddresses[index.index]
        val global = store.globals[address.address]
        return store.referenceBridge?.global(global) ?: global
    }

    override fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag {
        val address = (module as ModuleInstance).tagAddresses[index.index]
        return HostTag(address.address)
    }
}
