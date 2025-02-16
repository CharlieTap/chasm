package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ir.module.Import as IRImport
import io.github.charlietap.chasm.ir.value.NameValue as IRNameValue

internal fun ImportFactory(
    import: Import,
): IRImport = ImportFactory(
    import = import,
    nameValueFactory = ::NameValueFactory,
    descriptorFactory = ::ImportDescriptorFactory,
)

internal inline fun ImportFactory(
    import: Import,
    nameValueFactory: IRFactory<NameValue, IRNameValue>,
    descriptorFactory: IRFactory<Import.Descriptor, IRImport.Descriptor>,
): IRImport {
    return IRImport(
        moduleName = nameValueFactory(import.moduleName),
        entityName = nameValueFactory(import.entityName),
        descriptor = descriptorFactory(import.descriptor),
    )
}

internal fun ImportDescriptorFactory(
    descriptor: Import.Descriptor,
): IRImport.Descriptor {
    return when (descriptor) {
        is Import.Descriptor.Function -> IRImport.Descriptor.Function(
            type = DefinedTypeFactory(descriptor.type),
        )

        is Import.Descriptor.Table -> IRImport.Descriptor.Table(
            type = TableTypeFactory(descriptor.type),
        )

        is Import.Descriptor.Memory -> IRImport.Descriptor.Memory(
            type = MemoryTypeFactory(descriptor.type),
        )

        is Import.Descriptor.Global -> IRImport.Descriptor.Global(
            type = GlobalTypeFactory(descriptor.type),
        )

        is Import.Descriptor.Tag -> IRImport.Descriptor.Tag(
            type = TagTypeFactory(descriptor.type),
        )
    }
}
