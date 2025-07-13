package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.transform.ExportMapper
import io.github.charlietap.chasm.embedding.transform.ImportMapper
import io.github.charlietap.chasm.embedding.transform.Mapper

fun moduleInfo(
    module: Module,
): ModuleInfo = moduleInfo(
    module = module,
    importMapper = ImportMapper.instance,
    exportMapper = ExportMapper(module.module),
)

internal fun moduleInfo(
    module: Module,
    importMapper: Mapper<Import, ImportDefinition>,
    exportMapper: Mapper<Export, ExportDefinition>,
): ModuleInfo {

    val internalModule = module.module

    val imports = internalModule.imports.map(importMapper::map)
    val exports = internalModule.exports.map(exportMapper::map)

    return ModuleInfo(imports, exports)
}
