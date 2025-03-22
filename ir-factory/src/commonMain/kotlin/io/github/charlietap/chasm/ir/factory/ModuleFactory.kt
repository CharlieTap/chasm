package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ir.module.Custom as IRCustom
import io.github.charlietap.chasm.ir.module.DataSegment as IRDataSegment
import io.github.charlietap.chasm.ir.module.ElementSegment as IRElementSegment
import io.github.charlietap.chasm.ir.module.Export as IRExport
import io.github.charlietap.chasm.ir.module.Function as IRFunction
import io.github.charlietap.chasm.ir.module.Global as IRGlobal
import io.github.charlietap.chasm.ir.module.Import as IRImport
import io.github.charlietap.chasm.ir.module.Memory as IRMemory
import io.github.charlietap.chasm.ir.module.Module as IRModule
import io.github.charlietap.chasm.ir.module.StartFunction as IRStartFunction
import io.github.charlietap.chasm.ir.module.Table as IRTable
import io.github.charlietap.chasm.ir.module.Tag as IRTag
import io.github.charlietap.chasm.ir.module.Type as IRType
import io.github.charlietap.chasm.ir.module.Version as IRVersion

typealias ModuleFactory = (Module) -> IRModule

fun ModuleFactory(
    module: Module,
): IRModule = ModuleFactory(
    module = module,
    versionFactory = ::VersionFactory,
    typeFactory = ::TypeFactory,
    importFactory = ::ImportFactory,
    functionFactory = ::FunctionFactory,
    tableFactory = ::TableFactory,
    memoryFactory = ::MemoryFactory,
    tagFactory = ::TagFactory,
    globalFactory = ::GlobalFactory,
    exportFactory = ::ExportFactory,
    startFunctionFactory = ::StartFunctionFactory,
    elementSegmentFactory = ::ElementSegmentFactory,
    dataSegmentFactory = ::DataSegmentFactory,
    customFactory = ::CustomFactory,
)

internal inline fun ModuleFactory(
    module: Module,
    versionFactory: IRFactory<Version, IRVersion>,
    typeFactory: IRFactory<Type, IRType>,
    importFactory: IRFactory<Import, IRImport>,
    functionFactory: IRFactory<Function, IRFunction>,
    tableFactory: IRFactory<Table, IRTable>,
    memoryFactory: IRFactory<Memory, IRMemory>,
    tagFactory: IRFactory<Tag, IRTag>,
    globalFactory: IRFactory<Global, IRGlobal>,
    exportFactory: IRFactory<Export, IRExport>,
    startFunctionFactory: IRFactory<StartFunction, IRStartFunction?>,
    elementSegmentFactory: IRFactory<ElementSegment, IRElementSegment>,
    dataSegmentFactory: IRFactory<DataSegment, IRDataSegment>,
    customFactory: IRFactory<Custom, IRCustom>,
): IRModule {
    return IRModule(
        version = versionFactory(module.version),
        types = module.types.map(typeFactory),
        definedTypes = module.definedTypes,
        imports = module.imports.map(importFactory),
        functions = module.functions.map(functionFactory),
        tables = module.tables.map(tableFactory),
        memories = module.memories.map(memoryFactory),
        tags = module.tags.map(tagFactory),
        globals = module.globals.map(globalFactory),
        exports = module.exports.map(exportFactory),
        startFunction = module.startFunction?.let(startFunctionFactory),
        elementSegments = module.elementSegments.map(elementSegmentFactory),
        dataSegments = module.dataSegments.map(dataSegmentFactory),
        customs = module.customs.map(customFactory),
    )
}
