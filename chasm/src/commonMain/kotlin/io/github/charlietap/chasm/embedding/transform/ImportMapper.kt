package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.ext.functionType

internal typealias ImportMapper = (Module, Import, ModuleIndex) -> ImportDefinition

internal fun ImportMapper(
    module: Module,
    input: Import,
    index: ModuleIndex,
): ImportDefinition {
    return when (val descriptor = input.descriptor) {
        is Import.Descriptor.Function -> {

            val type = module.definedTypes[descriptor.typeIndex.idx.toInt()]
            val functionType = type.functionType()!!

            ImportDefinition(
                moduleName = input.moduleName.name,
                entityName = input.entityName.name,
                index = index,
                type = ExternalType.Function(functionType),
            )
        }
        is Import.Descriptor.Global -> {
            ImportDefinition(
                moduleName = input.moduleName.name,
                entityName = input.entityName.name,
                index = index,
                type = ExternalType.Global(descriptor.type),
            )
        }
        is Import.Descriptor.Memory -> {
            ImportDefinition(
                moduleName = input.moduleName.name,
                entityName = input.entityName.name,
                index = index,
                type = ExternalType.Memory(descriptor.type),
            )
        }
        is Import.Descriptor.Table -> {
            ImportDefinition(
                moduleName = input.moduleName.name,
                entityName = input.entityName.name,
                index = index,
                type = ExternalType.Table(descriptor.type),
            )
        }
        is Import.Descriptor.Tag -> {
            ImportDefinition(
                moduleName = input.moduleName.name,
                entityName = input.entityName.name,
                index = index,
                type = ExternalType.Tag(descriptor.type),
            )
        }
    }
}
