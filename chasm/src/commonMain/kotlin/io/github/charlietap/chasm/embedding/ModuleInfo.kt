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
import io.github.charlietap.chasm.host.ModuleIndex

fun moduleInfo(
    module: Module,
): ModuleInfo = moduleInfo(
    module = module,
    importMapper = ::ImportMapper,
    exportMapper = ExportMapper(module.module),
)

internal fun moduleInfo(
    module: Module,
    importMapper: ImportMapper,
    exportMapper: Mapper<Export, ExportDefinition>,
): ModuleInfo {

    val internalModule = module.module

    var functionIndex = 0
    var tableIndex = 0
    var memoryIndex = 0
    var globalIndex = 0
    var tagIndex = 0
    val imports = internalModule.imports.map { import ->
        val index = when (import.descriptor) {
            is Import.Descriptor.Function -> ModuleIndex.FunctionIndex(functionIndex++)
            is Import.Descriptor.Table -> ModuleIndex.TableIndex(tableIndex++)
            is Import.Descriptor.Memory -> ModuleIndex.MemoryIndex(memoryIndex++)
            is Import.Descriptor.Global -> ModuleIndex.GlobalIndex(globalIndex++)
            is Import.Descriptor.Tag -> ModuleIndex.TagIndex(tagIndex++)
        }
        importMapper(internalModule, import, index)
    }
    val exports = internalModule.exports.map(exportMapper::map)

    return ModuleInfo(imports, exports)
}
