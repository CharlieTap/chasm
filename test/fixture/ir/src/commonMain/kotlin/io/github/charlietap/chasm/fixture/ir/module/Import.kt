package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.ir.type.definedType
import io.github.charlietap.chasm.fixture.ir.type.globalType
import io.github.charlietap.chasm.fixture.ir.type.memoryType
import io.github.charlietap.chasm.fixture.ir.type.tableType
import io.github.charlietap.chasm.fixture.ir.type.tagType
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.GlobalType
import io.github.charlietap.chasm.ir.type.MemoryType
import io.github.charlietap.chasm.ir.type.TableType
import io.github.charlietap.chasm.ir.type.TagType
import io.github.charlietap.chasm.ir.value.NameValue

fun functionImportDescriptor(
    type: DefinedType = definedType(),
) = Import.Descriptor.Function(
    type = type,
)

fun tableImportDescriptor(
    type: TableType = tableType(),
) = Import.Descriptor.Table(
    type = type,
)

fun tagImportDescriptor(
    type: TagType = tagType(),
) = Import.Descriptor.Tag(
    type = type,
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
