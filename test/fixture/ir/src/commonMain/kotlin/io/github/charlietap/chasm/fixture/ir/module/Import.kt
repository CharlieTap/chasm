package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.value.NameValue
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

fun functionImportDescriptor(
    typeIndex: Index.TypeIndex = typeIndex(),
) = Import.Descriptor.Function(
    typeIndex = typeIndex,
)

fun tableImportDescriptor(
    type: TableType = tableType(),
) = Import.Descriptor.Table(
    type = type,
)

fun tagImportDescriptor(
    tagType: TagType = tagType(),
) = Import.Descriptor.Tag(
    type = tagType,
)

fun memoryImportDescriptor(
    type: MemoryType = memoryType(),
) = Import.Descriptor.Memory(
    type = type,
)

fun globalImportDescriptor(
    type: GlobalType = globalType(),
) = Import.Descriptor.Global(
    type = type,
)

fun import(
    moduleName: NameValue = NameValue(""),
    entityName: NameValue = NameValue(""),
    descriptor: Import.Descriptor = functionImportDescriptor(),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = descriptor,
)

fun functionImport(
    moduleName: NameValue = NameValue(""),
    entityName: NameValue = NameValue(""),
    descriptor: Import.Descriptor = functionImportDescriptor(),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = descriptor,
)

fun globalImport(
    moduleName: NameValue = NameValue(""),
    entityName: NameValue = NameValue(""),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = globalImportDescriptor(),
)

fun memoryImport(
    moduleName: NameValue = NameValue(""),
    entityName: NameValue = NameValue(""),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = memoryImportDescriptor(),
)

fun tableImport(
    moduleName: NameValue = NameValue(""),
    entityName: NameValue = NameValue(""),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = tableImportDescriptor(),
)

fun tagImport(
    moduleName: NameValue = NameValue(""),
    entityName: NameValue = NameValue(""),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = tagImportDescriptor(),
)
