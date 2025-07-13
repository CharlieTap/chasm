package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.ext.functionType

internal class ImportMapper : Mapper<Import, ImportDefinition> {

    override fun map(input: Import): ImportDefinition {
        return when (val descriptor = input.descriptor) {
            is Import.Descriptor.Function -> {
                ImportDefinition(
                    moduleName = input.moduleName.name,
                    entityName = input.entityName.name,
                    type = ExternalType.Function(descriptor.type.functionType()!!),
                )
            }
            is Import.Descriptor.Global -> {
                ImportDefinition(
                    moduleName = input.moduleName.name,
                    entityName = input.entityName.name,
                    type = ExternalType.Global(descriptor.type),
                )
            }
            is Import.Descriptor.Memory -> {
                ImportDefinition(
                    moduleName = input.moduleName.name,
                    entityName = input.entityName.name,
                    type = ExternalType.Memory(descriptor.type),
                )
            }
            is Import.Descriptor.Table -> {
                ImportDefinition(
                    moduleName = input.moduleName.name,
                    entityName = input.entityName.name,
                    type = ExternalType.Table(descriptor.type),
                )
            }
            is Import.Descriptor.Tag -> {
                ImportDefinition(
                    moduleName = input.moduleName.name,
                    entityName = input.entityName.name,
                    type = ExternalType.Tag(descriptor.type),
                )
            }
        }
    }

    companion object {
        val instance = ImportMapper()
    }
}
