package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRollerImpl
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.DefinedTypeSubstitutorImpl

internal data class ValidationContext(
    val module: Module,
    val exportContext: ExportContext = ExportContextImpl(),
    val functionContext: FunctionContext = FunctionContextImpl(),
    val globalContext: GlobalContext = GlobalContextImpl(),
    val typeContext: TypeContext = TypeContextImpl(),
) : ExportContext by exportContext,
    FunctionContext by functionContext,
    GlobalContext by globalContext,
    TypeContext by typeContext {

    val types by lazy {
        module.types.map(Type::recursiveType).fold(mutableListOf<DefinedType>()) { acc, recursiveType ->
            val size = acc.size

            val substitutor: ConcreteHeapTypeSubstitutor = { heapType ->
                when (heapType) {
                    is ConcreteHeapType.TypeIndex -> {
                        ConcreteHeapType.Defined(acc[heapType.index.idx.toInt()])
                    }
                    else -> heapType
                }
            }

            acc.apply {
                addAll(
                    DefinedTypeRollerImpl(size, recursiveType).map { definedType ->
                        DefinedTypeSubstitutorImpl(definedType, substitutor)
                    },
                )
            }
        }
    }

    val functions by lazy {
        val importedFunctions = module.imports.fold(mutableListOf<FunctionType>()) { acc, import ->
            val descriptor = import.descriptor
            if (descriptor is Import.Descriptor.Function) {
                val functionType = types[descriptor.typeIndex.idx.toInt()].functionType()
                functionType?.results?.let {
                    acc += functionType
                }
            }
            acc
        }
        val moduleFunctions = module.functions.mapNotNull { function ->
            types[function.typeIndex.idx.toInt()].functionType()
        }
        importedFunctions + moduleFunctions
    }

    val globals by lazy {
        val importedGlobals = module.imports.fold(mutableListOf<GlobalType>()) { acc, import ->
            val descriptor = import.descriptor
            if (descriptor is Import.Descriptor.Global) {
                acc += descriptor.type
            }
            acc
        }
        val moduleGlobals = module.globals.map(Global::type)
        importedGlobals + moduleGlobals
    }

    val memories by lazy {
        val importedMemories = module.imports.fold(mutableListOf<MemoryType>()) { acc, import ->
            val descriptor = import.descriptor
            if (descriptor is Import.Descriptor.Memory) {
                acc += descriptor.type
            }
            acc
        }
        val moduleMemories = module.memories.map(Memory::type)
        importedMemories + moduleMemories
    }

    val tables by lazy {
        val importedTables = module.imports.fold(mutableListOf<TableType>()) { acc, import ->
            val descriptor = import.descriptor
            if (descriptor is Import.Descriptor.Table) {
                acc += descriptor.type
            }
            acc
        }
        val moduleTables = module.tables.map(Table::type)
        importedTables + moduleTables
    }
}
