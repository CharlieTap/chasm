package io.github.charlietap.chasm.validator.context

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.validator.error.FunctionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal class ModuleValidationContext(
    config: ModuleConfig,
    module: Module,
    val elementSegmentContext: ElementSegmentContextImpl = ElementSegmentContextImpl(),
    val exportContext: ExportContextImpl = ExportContextImpl(),
    val expressionContext: ExpressionContextImpl = ExpressionContextImpl(),
    val functionContext: FunctionContextImpl = FunctionContextImpl(),
    val refsContext: RefsContextImpl = RefsContextImpl(),
    val typeContext: TypeContextImpl = TypeContextImpl(),
) : CoreTypeValidationContext,
    ElementSegmentContext by elementSegmentContext,
    ExportContext by exportContext,
    ExpressionContext by expressionContext,
    FunctionContext by functionContext,
    RefsContext by refsContext {

    var config: ModuleConfig = config
        private set

    var module: Module = module
        private set

    val types = mutableListOf<DefinedType>()
    var definedTypesValidated: Int = 0

    val functions = mutableListOf<DefinedType>()
    val globals = mutableListOf<GlobalType>()
    val memories = mutableListOf<MemoryType>()
    val tables = mutableListOf<TableType>()
    val tags = mutableListOf<TagType>()
    val datas = mutableListOf<io.github.charlietap.chasm.ast.module.Index.DataIndex>()
    val elems = mutableListOf<io.github.charlietap.chasm.type.ReferenceType>()

    var importedGlobalCount: Int = 0
        private set

    val validTypeIndices: IntRange
        get() = 0 until definedTypesValidated

    override val definedTypeCount: Int
        get() = definedTypesValidated

    override var limitsMaximum: ULong
        get() = typeContext.limitsMaximum
        set(value) {
            typeContext.limitsMaximum = value
        }

    override val lookup: DefinedTypeLookup = { index ->
        types.getOrNull(index)
    }

    init {
        reset(config, module)
    }

    fun reset(
        config: ModuleConfig,
        module: Module,
    ) {
        clear()
        this.config = config
        this.module = module

        types += module.definedTypes

        module.imports.forEach { import ->
            when (val descriptor = import.descriptor) {
                is Import.Descriptor.Function -> types.getOrNull(descriptor.typeIndex.idx.toInt())?.let(functions::add)
                is Import.Descriptor.Global -> globals += descriptor.type
                is Import.Descriptor.Memory -> memories += descriptor.type
                is Import.Descriptor.Table -> tables += descriptor.type
                is Import.Descriptor.Tag -> tags += descriptor.type
            }
        }
        importedGlobalCount = globals.size

        module.functions.mapNotNullTo(functions) { function ->
            types.getOrNull(function.typeIndex.idx.toInt())
        }
        module.globals.mapTo(globals, Global::type)
        module.memories.mapTo(memories, Memory::type)
        module.tables.mapTo(tables, Table::type)
        module.tags.mapTo(tags, Tag::type)
        module.dataSegments.mapTo(datas, DataSegment::idx)
        module.elementSegments.mapTo(elems, ElementSegment::type)
        refsContext.reset(module)
    }

    fun clear() {
        types.clear()
        functions.clear()
        globals.clear()
        memories.clear()
        tables.clear()
        tags.clear()
        datas.clear()
        elems.clear()
        importedGlobalCount = 0
        definedTypesValidated = 0

        elementSegmentContext.elementSegmentType = null
        exportContext.exportNames.clear()
        expressionContext.expressionResultType = null
        functionContext.locals.clear()
        functionContext.localChanges.clear()
        functionContext.labels.clear()
        functionContext.result = null
        functionContext.operands.clear()
        refsContext.clear()
        typeContext.limitsMaximum = ULong.MAX_VALUE
    }

    override fun definedType(index: Int): Result<DefinedType, ModuleValidatorError> {
        return types
            .getOrNull(index)
            .takeIf { index < definedTypesValidated }
            .toResultOr { FunctionValidatorError.UnknownType }
    }
}
