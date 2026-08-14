package io.github.charlietap.chasm.validator.context

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.validator.error.FunctionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal class ModuleValidationContext private constructor(
    immutableContext: ImmutableModuleValidationContext,
    definedTypesValidated: Int,
    val elementSegmentContext: ElementSegmentContextImpl,
    val exportContext: ExportContextImpl,
    val expressionContext: ExpressionContextImpl,
    val functionContext: FunctionContextImpl,
    val typeContext: TypeContextImpl,
) : CoreTypeValidationContext,
    ElementSegmentContext by elementSegmentContext,
    ExportContext by exportContext,
    ExpressionContext by expressionContext,
    FunctionContext by functionContext,
    RefsContext {

    var immutableContext: ImmutableModuleValidationContext = immutableContext
        private set

    var config: ModuleConfig = immutableContext.config
        private set

    var module: Module = immutableContext.module
        private set

    var types: List<DefinedType> = immutableContext.types
        private set

    var functions: List<DefinedType> = immutableContext.functions
        private set

    var globals: List<GlobalType> = immutableContext.globals
        private set

    var memories: List<MemoryType> = immutableContext.memories
        private set

    var tables: List<TableType> = immutableContext.tables
        private set

    var tags: List<TagType> = immutableContext.tags
        private set

    var datas: List<Index.DataIndex> = immutableContext.datas
        private set

    var elems: List<ReferenceType> = immutableContext.elems
        private set

    override var refs: Set<Index.FunctionIndex> = immutableContext.refs
        private set

    var importedGlobalCount: Int = immutableContext.importedGlobalCount
        private set

    var definedTypesValidated: Int = definedTypesValidated

    var visibleGlobalCount: Int = immutableContext.globals.size

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

    constructor(
        config: ModuleConfig,
        module: Module,
        elementSegmentContext: ElementSegmentContextImpl = ElementSegmentContextImpl(),
        exportContext: ExportContextImpl = ExportContextImpl(),
        expressionContext: ExpressionContextImpl = ExpressionContextImpl(),
        functionContext: FunctionContextImpl = FunctionContextImpl(),
        typeContext: TypeContextImpl = TypeContextImpl(),
    ) : this(
        immutableContext = ImmutableModuleValidationContext(config, module),
        definedTypesValidated = 0,
        elementSegmentContext = elementSegmentContext,
        exportContext = exportContext,
        expressionContext = expressionContext,
        functionContext = functionContext,
        typeContext = typeContext,
    )

    internal constructor(
        immutableContext: ImmutableModuleValidationContext,
        definedTypesValidated: Int,
    ) : this(
        immutableContext = immutableContext,
        definedTypesValidated = definedTypesValidated,
        elementSegmentContext = ElementSegmentContextImpl(),
        exportContext = ExportContextImpl(),
        expressionContext = ExpressionContextImpl(),
        functionContext = FunctionContextImpl(),
        typeContext = TypeContextImpl(),
    )

    fun reset(
        config: ModuleConfig,
        module: Module,
    ) {
        clearMutableState()
        setImmutableContext(ImmutableModuleValidationContext(config, module))
        visibleGlobalCount = globals.size
        definedTypesValidated = 0
    }

    fun clearLocalState() {
        visibleGlobalCount = 0
        definedTypesValidated = 0

        clearMutableState()
    }

    private fun setImmutableContext(context: ImmutableModuleValidationContext) {
        immutableContext = context
        config = context.config
        module = context.module
        types = context.types
        functions = context.functions
        globals = context.globals
        memories = context.memories
        tables = context.tables
        tags = context.tags
        datas = context.datas
        elems = context.elems
        importedGlobalCount = context.importedGlobalCount
        refs = context.refs
    }

    private fun clearMutableState() {
        elementSegmentContext.elementSegmentType = null
        exportContext.exportNames.clear()
        expressionContext.expressionResultType = null
        functionContext.locals.clear()
        functionContext.localChanges.clear()
        functionContext.labels.clear()
        functionContext.result = null
        functionContext.operands.clear()
        typeContext.limitsMaximum = ULong.MAX_VALUE
    }

    override fun definedType(index: Int): Result<DefinedType, ModuleValidatorError> {
        return types
            .getOrNull(index)
            .takeIf { index < definedTypesValidated }
            .toResultOr { FunctionValidatorError.UnknownType }
    }
}
