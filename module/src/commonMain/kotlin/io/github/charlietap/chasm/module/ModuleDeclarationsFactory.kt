package io.github.charlietap.chasm.module

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.LocalNameSubsection
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.ModuleNameSubsection
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.NameSubsection
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.ExportDeclarations
import io.github.charlietap.chasm.declarations.GlobalDeclaration
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.declarations.TableDeclaration

typealias ModuleDeclarationsFactory = (Module) -> ModuleDeclarations

@OptIn(ExperimentalUnsignedTypes::class)
fun ModuleDeclarationsFactory(
    module: Module,
): ModuleDeclarations = ModuleDeclarations(
    definedTypes = module.definedTypes,
    imports = module.imports,
    functionTypeIndices = IntArray(module.functions.size) { index -> module.functions[index].typeIndex.position() },
    tables = module.tables.map { table -> TableDeclaration(table.type, constantExpression(table.initExpression)) },
    memories = module.memories.map { memory -> memory.type },
    tags = module.tags.map { tag -> tag.type },
    globals = module.globals.map { global -> GlobalDeclaration(global.type, constantExpression(global.initExpression)) },
    exports = exports(module.exports),
    startFunction = module.startFunction?.idx?.position(),
    elementSegments = module.elementSegments.map(::elementSegment),
    dataSegments = module.dataSegments.map(::dataSegment),
    names = names(module),
)

private fun exports(
    exports: List<Export>,
): ExportDeclarations {
    val kinds = ByteArray(exports.size)
    val indices = IntArray(exports.size)
    exports.forEachIndexed { position, export ->
        when (val descriptor = export.descriptor) {
            is Export.Descriptor.Function -> {
                kinds[position] = ExportDeclarations.FUNCTION
                indices[position] = descriptor.functionIndex.position()
            }
            is Export.Descriptor.Table -> {
                kinds[position] = ExportDeclarations.TABLE
                indices[position] = descriptor.tableIndex.position()
            }
            is Export.Descriptor.Memory -> {
                kinds[position] = ExportDeclarations.MEMORY
                indices[position] = descriptor.memoryIndex.position()
            }
            is Export.Descriptor.Global -> {
                kinds[position] = ExportDeclarations.GLOBAL
                indices[position] = descriptor.globalIndex.position()
            }
            is Export.Descriptor.Tag -> {
                kinds[position] = ExportDeclarations.TAG
                indices[position] = descriptor.tagIndex.position()
            }
        }
    }
    return ExportDeclarations(Array(exports.size) { position -> exports[position].name.name }, kinds, indices)
}

private fun elementSegment(
    segment: ElementSegment,
): ElementSegmentDeclaration = ElementSegmentDeclaration(
    type = segment.type,
    initializers = segment.initExpressions.map(::constantExpression),
    mode = when (val mode = segment.mode) {
        ElementSegment.Mode.Passive -> ElementSegmentDeclaration.Mode.Passive
        ElementSegment.Mode.Declarative -> ElementSegmentDeclaration.Mode.Declarative
        is ElementSegment.Mode.Active -> ElementSegmentDeclaration.Mode.Active(
            tableIndex = mode.tableIndex.position(),
            offset = constantExpression(mode.offsetExpr),
        )
    },
)

@OptIn(ExperimentalUnsignedTypes::class)
private fun dataSegment(
    segment: DataSegment,
): DataSegmentDeclaration = DataSegmentDeclaration(
    payload = DataPayload.Inline(segment.initData.asByteArray()),
    mode = when (val mode = segment.mode) {
        DataSegment.Mode.Passive -> DataSegmentDeclaration.Mode.Passive
        is DataSegment.Mode.Active -> DataSegmentDeclaration.Mode.Active(
            memoryIndex = mode.memoryIndex.position(),
            offset = constantExpression(mode.offset),
        )
    },
)

private fun constantExpression(
    expression: Expression,
): ConstantExpression = ConstantExpression(
    expression.instructions.mapNotNull { instruction ->
        when (instruction) {
            is NumericInstruction.I32Const -> ConstantInstruction.I32Const(instruction.value)
            is NumericInstruction.I64Const -> ConstantInstruction.I64Const(instruction.value)
            is NumericInstruction.F32Const -> ConstantInstruction.F32Const(instruction.bits)
            is NumericInstruction.F64Const -> ConstantInstruction.F64Const(instruction.bits)
            is NumericInstruction.I32Add -> ConstantInstruction.I32Add
            is NumericInstruction.I32Sub -> ConstantInstruction.I32Sub
            is NumericInstruction.I32Mul -> ConstantInstruction.I32Mul
            is NumericInstruction.I64Add -> ConstantInstruction.I64Add
            is NumericInstruction.I64Sub -> ConstantInstruction.I64Sub
            is NumericInstruction.I64Mul -> ConstantInstruction.I64Mul
            is VectorInstruction.V128Const -> v128Const(instruction.bytes)
            is VariableInstruction.GlobalGet -> ConstantInstruction.GlobalGet(instruction.globalIdx.position())
            is ReferenceInstruction.RefNull -> ConstantInstruction.RefNull(instruction.type)
            is ReferenceInstruction.RefFunc -> ConstantInstruction.RefFunc(instruction.funcIdx.position())
            is AggregateInstruction.StructNew -> ConstantInstruction.StructNew(instruction.typeIndex.position())
            is AggregateInstruction.StructNewDefault -> ConstantInstruction.StructNewDefault(instruction.typeIndex.position())
            is AggregateInstruction.ArrayNew -> ConstantInstruction.ArrayNew(instruction.typeIndex.position())
            is AggregateInstruction.ArrayNewDefault -> ConstantInstruction.ArrayNewDefault(instruction.typeIndex.position())
            is AggregateInstruction.ArrayNewFixed -> ConstantInstruction.ArrayNewFixed(
                typeIndex = instruction.typeIndex.position(),
                size = instruction.size.toInt(),
            )
            is AggregateInstruction.RefI31 -> ConstantInstruction.RefI31
            is AggregateInstruction.AnyConvertExtern -> ConstantInstruction.AnyConvertExtern
            is AggregateInstruction.ExternConvertAny -> ConstantInstruction.ExternConvertAny
            else -> null
        }
    },
)

private fun v128Const(
    bytes: ByteArray,
): ConstantInstruction.V128Const {
    fun littleEndian(
        from: Int,
    ): Long {
        var value = 0L
        for (offset in 7 downTo 0) {
            value = (value shl 8) or (bytes[from + offset].toLong() and 0xFF)
        }
        return value
    }
    return ConstantInstruction.V128Const(low = littleEndian(0), high = littleEndian(8))
}

private fun names(
    module: Module,
): ModuleNames {
    val sections = module.customs.filterIsInstance<NameData>()
    val subsections = sections.firstOrNull()?.subsections ?: return ModuleNames.NONE
    val allSubsections = sections.flatMap { section -> section.subsections }
    val moduleName = allSubsections.first<ModuleNameSubsection>()?.name?.name

    val functionNames = mutableMapOf<Int, String>()
    subsections.first<FunctionNameSubsection>()?.nameMap?.forEach { association ->
        functionNames.getOrPut(association.idx.toInt()) { association.name.name }
    }
    val namedFunctions = functionNames.keys.sorted()

    val exportedFunctions = module.exports.mapNotNullTo(mutableSetOf()) { export ->
        (export.descriptor as? Export.Descriptor.Function)?.functionIndex?.position()
    }
    val exportedLocalNames = buildMap {
        subsections.first<LocalNameSubsection>()?.indirectNameMap?.forEach { association ->
            val functionIndex = association.idx.toInt()
            if (functionIndex in exportedFunctions && functionIndex !in this) {
                put(functionIndex, association.nameMap.map { local -> local.name.name })
            }
        }
    }

    val trapNames = mutableMapOf<Int, String>()
    allSubsections.filterIsInstance<FunctionNameSubsection>().forEach { subsection ->
        subsection.nameMap.forEach { association -> trapNames[association.idx.toInt()] = association.name.name }
    }
    val trapOverrides = trapNames.keys.filter { index -> trapNames[index] != functionNames[index] }.sorted()

    if (moduleName == null && functionNames.isEmpty() && exportedLocalNames.isEmpty() && trapOverrides.isEmpty()) {
        return ModuleNames.NONE
    }
    return ModuleNames(
        moduleName = moduleName,
        functionNameIndices = namedFunctions.toIntArray(),
        functionNames = Array(namedFunctions.size) { position -> functionNames.getValue(namedFunctions[position]) },
        exportedLocalNames = exportedLocalNames,
        trapNameIndices = trapOverrides.toIntArray(),
        trapNames = Array(trapOverrides.size) { position -> trapNames.getValue(trapOverrides[position]) },
    )
}

private inline fun <reified T : NameSubsection> List<NameSubsection>.first(): T? = firstNotNullOfOrNull { it as? T }

private fun Index.position(): Int = idx.toInt()
