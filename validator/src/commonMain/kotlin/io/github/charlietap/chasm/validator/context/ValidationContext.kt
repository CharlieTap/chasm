package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.ext.functionType

internal data class ValidationContext(
    val module: Module,
    val exportContext: ExportContext = ExportContextImpl(),
    val functionContext: FunctionContext = FunctionContextImpl(),
) : ExportContext by exportContext,
    FunctionContext by functionContext {

    val functions by lazy {
        val importedFunctions = module.imports.fold(mutableListOf<ResultType>()) { acc, import ->
            val descriptor = import.descriptor
            if (descriptor is Import.Descriptor.Function) {
                val functionType = module.types[descriptor.typeIndex.idx.toInt()].recursiveType.definedType().functionType()
                functionType?.results?.let {
                    acc += functionType.results
                }
            }
            acc
        }
        val moduleFunctions = module.functions.mapNotNull { function ->
            val functionType = module.types[function.typeIndex.idx.toInt()].recursiveType.definedType().functionType()
            functionType?.results
        }
        importedFunctions + moduleFunctions
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
