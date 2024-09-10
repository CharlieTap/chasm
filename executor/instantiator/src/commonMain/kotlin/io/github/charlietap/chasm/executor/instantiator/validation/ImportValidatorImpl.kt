package io.github.charlietap.chasm.executor.instantiator.validation

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal fun ImportValidatorImpl(
    instance: ModuleInstance,
    import: Import,
    classified: ClassifiedExternalValue,
): Result<Unit, InstantiationError.UnexpectedImport> = binding {
    val matches = when (val descriptor = import.descriptor) {
        is Import.Descriptor.Function -> {
            val type = instance.types[descriptor.typeIndex.idx.toInt()]

            val externType = when (classified.type) {
                is ExternalType.Function -> Ok(classified.type.definedType)
                else -> Err(InstantiationError.UnexpectedImport(import.moduleName.name, import.entityName.name))
            }.bind()

            type == externType
        }
        is Import.Descriptor.Table -> {
            val type = descriptor.type

            val externType = when (classified.type) {
                is ExternalType.Table -> Ok(classified.type.tableType)
                else -> Err(InstantiationError.UnexpectedImport(import.moduleName.name, import.entityName.name))
            }.bind()

            type == externType
        }
        is Import.Descriptor.Memory -> {
            val type = descriptor.type

            val externType = when (classified.type) {
                is ExternalType.Memory -> Ok(classified.type.memoryType)
                else -> Err(InstantiationError.UnexpectedImport(import.moduleName.name, import.entityName.name))
            }.bind()

            type == externType
        }
        is Import.Descriptor.Global -> {
            val type = descriptor.type

            val externType = when (classified.type) {
                is ExternalType.Global -> Ok(classified.type.globalType)
                else -> Err(InstantiationError.UnexpectedImport(import.moduleName.name, import.entityName.name))
            }.bind()

            type == externType
        }
        is Import.Descriptor.Tag -> {
            val type = descriptor.type

            val externType = when (classified.type) {
                is ExternalType.Tag -> Ok(classified.type.tagType)
                else -> Err(InstantiationError.UnexpectedImport(import.moduleName.name, import.entityName.name))
            }.bind()

            type == externType
        }
    }

    if (matches) {
        Ok(Unit)
    } else {
        Err(InstantiationError.UnexpectedImport(import.moduleName.name, import.entityName.name))
    }
}
