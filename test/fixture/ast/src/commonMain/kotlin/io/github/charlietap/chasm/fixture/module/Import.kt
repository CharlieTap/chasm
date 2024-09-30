package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.tableType

fun functionImportDescriptor(
    type: FunctionType = functionType(),
) = Import.Descriptor.Function(
    type = type,
)

fun tableImportDescriptor(
    type: TableType = tableType(),
) = Import.Descriptor.Table(
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
