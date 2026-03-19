package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.rolling.substitution.GlobalTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TableTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TagTypeSubstitutor
import io.github.charlietap.chasm.ir.module.Import as IRImport
import io.github.charlietap.chasm.ir.value.NameValue as IRNameValue

internal typealias ImportFactory = (Import, Substitution) -> IRImport

internal fun ImportFactory(
    import: Import,
    substitution: Substitution,
): IRImport = ImportFactory(
    import = import,
    substitution = substitution,
    nameValueFactory = ::NameValueFactory,
    descriptorFactory = ::ImportDescriptorFactory,
)

internal inline fun ImportFactory(
    import: Import,
    substitution: Substitution,
    nameValueFactory: IRFactory<NameValue, IRNameValue>,
    descriptorFactory: ImportDescriptorFactory,
): IRImport {
    return IRImport(
        moduleName = nameValueFactory(import.moduleName),
        entityName = nameValueFactory(import.entityName),
        descriptor = descriptorFactory(import.descriptor, substitution),
    )
}

internal typealias ImportDescriptorFactory = (Import.Descriptor, Substitution) -> IRImport.Descriptor

internal fun ImportDescriptorFactory(
    descriptor: Import.Descriptor,
    substitution: Substitution,
): IRImport.Descriptor {
    return when (descriptor) {
        is Import.Descriptor.Function -> IRImport.Descriptor.Function(
            typeIndex = Index.TypeIndex(descriptor.typeIndex.idx.toInt()),
        )

        is Import.Descriptor.Table -> IRImport.Descriptor.Table(
            type = TableTypeSubstitutor(descriptor.type, substitution),
        )

        is Import.Descriptor.Memory -> IRImport.Descriptor.Memory(
            type = descriptor.type,
        )

        is Import.Descriptor.Global -> IRImport.Descriptor.Global(
            type = GlobalTypeSubstitutor(descriptor.type, substitution),
        )

        is Import.Descriptor.Tag -> IRImport.Descriptor.Tag(
            type = TagTypeSubstitutor(descriptor.type, substitution),
        )
    }
}
