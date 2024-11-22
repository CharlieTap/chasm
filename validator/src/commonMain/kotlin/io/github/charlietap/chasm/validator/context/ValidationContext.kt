package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.DefinedTypeSubstitutor

internal data class ValidationContext(
    val module: Module,
    val elementSegmentContext: ElementSegmentContext = ElementSegmentContextImpl(),
    val exportContext: ExportContext = ExportContextImpl(),
    val expressionContext: ExpressionContext = ExpressionContextImpl(),
    val functionContext: FunctionContext = FunctionContextImpl(),
    val instructionContext: InstructionContext = InstructionContextImpl(),
    val refsContext: RefsContext = RefsContextImpl(module),
    val typeContext: TypeContext = TypeContextImpl(),
) : ElementSegmentContext by elementSegmentContext,
    ExportContext by exportContext,
    ExpressionContext by expressionContext,
    FunctionContext by functionContext,
    InstructionContext by instructionContext,
    RefsContext by refsContext,
    TypeContext by typeContext,
    TypeMatcherContext {

    val types by lazy {
        try {
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
                        DefinedTypeRoller(size, recursiveType).map { definedType ->
                            DefinedTypeSubstitutor(definedType, substitutor)
                        },
                    )
                }
            }
        } catch (_: IndexOutOfBoundsException) {
            emptyList()
        }
    }

    val functions by lazy {
        val importedFunctions = module.imports.asSequence()
            .map(Import::descriptor)
            .filterIsInstance<Import.Descriptor.Function>()
            .map(Import.Descriptor.Function::type)
            .toList()
        val moduleFunctions = module.functions.mapNotNull { function ->
            types.getOrNull(function.typeIndex.idx.toInt())
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

    val tags by lazy {
        val importedTags = module.imports.fold(mutableListOf<TagType>()) { acc, import ->
            val descriptor = import.descriptor
            if (descriptor is Import.Descriptor.Tag) {
                acc += descriptor.type
            }
            acc
        }
        val moduleTags = module.tags.map(Tag::type)
        importedTags + moduleTags
    }

    val datas by lazy {
        module.dataSegments.map(DataSegment::idx)
    }

    val elems by lazy {
        module.elementSegments.map(ElementSegment::type)
    }

    override fun lookup(): DefinedTypeLookup {
        return { index ->
            types.getOrNull(index.idx.toInt())
        }
    }

    override fun substitution(): ConcreteHeapTypeSubstitutor {
        val substitution: ConcreteHeapTypeSubstitutor = { concreteHeapType ->
            when (concreteHeapType) {
                is ConcreteHeapType.TypeIndex -> if (concreteHeapType.index.idx.toInt() < types.size) {
                    ConcreteHeapType.Defined(types[concreteHeapType.index.idx.toInt()])
                } else {
                    concreteHeapType
                }
                else -> concreteHeapType
            }
        }
        return substitution
    }
}
