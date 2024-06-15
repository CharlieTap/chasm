package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module

internal data class ValidationContext(
    val module: Module,
    val exportContext: ExportContext = ExportContextImpl(),
) : ExportContext by exportContext {

    val validFunctionIndices by lazy {
        val importedFunctions = module.imports.filter { it.descriptor is Import.Descriptor.Function }.size
        val moduleFunctions = module.functions.size
        val totalFunctions = importedFunctions + moduleFunctions

        0..<totalFunctions
    }

    val validGlobalIndices by lazy {
        val importedGlobals = module.imports.filter { it.descriptor is Import.Descriptor.Global }.size
        val moduleGlobals = module.globals.size
        val totalGlobals = importedGlobals + moduleGlobals

        0..<totalGlobals
    }

    val validMemoryIndices by lazy {
        val importedMemories = module.imports.filter { it.descriptor is Import.Descriptor.Memory }.size
        val moduleMemories = module.memories.size
        val totalMemories = importedMemories + moduleMemories

        0..<totalMemories
    }

    val validTableIndices by lazy {
        val importedTables = module.imports.filter { it.descriptor is Import.Descriptor.Table }.size
        val moduleTables = module.tables.size
        val totalTables = importedTables + moduleTables

        0..<totalTables
    }
}
