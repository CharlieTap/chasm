package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

fun externalType(): ExternalType = functionExternalType()

fun functionExternalType(
    definedType: DefinedType = definedType(),
) = ExternalType.Function(definedType)

fun tableExternalType(
    tableType: TableType = tableType(),
) = ExternalType.Table(tableType)

fun memoryExternalType(
    memoryType: MemoryType = memoryType(),
) = ExternalType.Memory(memoryType)

fun globalExternalType(
    globalType: GlobalType = globalType(),
) = ExternalType.Global(globalType)
