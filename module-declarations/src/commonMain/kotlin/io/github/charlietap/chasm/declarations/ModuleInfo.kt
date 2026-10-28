package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.declarations.transform.ExportMapper
import io.github.charlietap.chasm.declarations.transform.ImportMapper
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.host.ModuleIndex

fun moduleInfo(
    module: ModuleDeclarations,
): ModuleInfo = moduleInfo(
    module = module,
    importMapper = ::ImportMapper,
    exportMapper = ::ExportMapper,
)

internal fun moduleInfo(
    module: ModuleDeclarations,
    importMapper: ImportMapper,
    exportMapper: ExportMapper,
): ModuleInfo {

    var functionIndex = 0
    var tableIndex = 0
    var memoryIndex = 0
    var globalIndex = 0
    var tagIndex = 0
    val imports = module.imports.map { import ->
        val index = when (import.descriptor) {
            is Import.Descriptor.Function -> ModuleIndex.FunctionIndex(functionIndex++)
            is Import.Descriptor.Table -> ModuleIndex.TableIndex(tableIndex++)
            is Import.Descriptor.Memory -> ModuleIndex.MemoryIndex(memoryIndex++)
            is Import.Descriptor.Global -> ModuleIndex.GlobalIndex(globalIndex++)
            is Import.Descriptor.Tag -> ModuleIndex.TagIndex(tagIndex++)
        }
        importMapper(module, import, index)
    }
    val exports = module.exports.map { export -> exportMapper(module, export) }

    return ModuleInfo(imports, exports)
}
