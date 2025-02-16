package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ir.module.Export as IRExport
import io.github.charlietap.chasm.ir.value.NameValue as IRNameValue

internal fun ExportFactory(
    export: Export,
): IRExport = ExportFactory(
    export = export,
    nameValueFactory = ::NameValueFactory,
    descriptorFactory = ::ExportDescriptorFactory,
)

internal inline fun ExportFactory(
    export: Export,
    nameValueFactory: IRFactory<NameValue, IRNameValue>,
    descriptorFactory: IRFactory<Export.Descriptor, IRExport.Descriptor>,
): IRExport {
    return IRExport(
        name = nameValueFactory(export.name),
        descriptor = descriptorFactory(export.descriptor),
    )
}

internal fun ExportDescriptorFactory(
    descriptor: Export.Descriptor,
): IRExport.Descriptor {
    return when (descriptor) {
        is Export.Descriptor.Function -> IRExport.Descriptor.Function(
            functionIndex = FunctionIndexFactory(descriptor.functionIndex),
        )

        is Export.Descriptor.Table -> IRExport.Descriptor.Table(
            tableIndex = TableIndexFactory(descriptor.tableIndex),
        )

        is Export.Descriptor.Memory -> IRExport.Descriptor.Memory(
            memoryIndex = MemoryIndexFactory(descriptor.memoryIndex),
        )

        is Export.Descriptor.Global -> IRExport.Descriptor.Global(
            globalIndex = GlobalIndexFactory(descriptor.globalIndex),
        )

        is Export.Descriptor.Tag -> IRExport.Descriptor.Tag(
            tagIndex = TagIndexFactory(descriptor.tagIndex),
        )
    }
}
