package io.github.charlietap.chasm.executor.instantiator.allocation

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.address.RuntimeInstanceId
import io.github.charlietap.chasm.runtime.instance.ModuleAllocation
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class ModuleAllocationJournal(
    private val instance: ModuleInstance,
) {
    private var importedFunctions = 0
    private var importedTables = 0
    private var importedMemories = 0
    private var importedTags = 0
    private var importedGlobals = 0

    fun markImports() {
        importedFunctions = instance.functionAddresses.size
        importedTables = instance.tableAddresses.size
        importedMemories = instance.memAddresses.size
        importedTags = instance.tagAddresses.size
        importedGlobals = instance.globalAddresses.size
    }

    fun isImported(index: Index.TableIndex): Boolean = index.idx < importedTables

    fun allocation(providers: List<RuntimeInstanceId>): ModuleAllocation {
        val functions = instance.functionAddresses.drop(importedFunctions)

        return ModuleAllocation(
            functionAddresses = functions,
            instructionAddresses = functions,
            tableAddresses = instance.tableAddresses.drop(importedTables),
            memoryAddresses = instance.memAddresses.drop(importedMemories),
            tagAddresses = instance.tagAddresses.drop(importedTags),
            globalAddresses = instance.globalAddresses.drop(importedGlobals),
            elementAddresses = instance.elemAddresses.toList(),
            dataAddresses = instance.dataAddresses.toList(),
            providers = providers,
        )
    }
}
