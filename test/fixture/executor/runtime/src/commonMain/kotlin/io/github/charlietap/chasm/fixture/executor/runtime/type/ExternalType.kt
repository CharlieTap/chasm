package io.github.charlietap.chasm.fixture.executor.runtime.type

import io.github.charlietap.chasm.executor.runtime.type.ExternalType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

fun externalType(): ExternalType = functionExternalType()

fun functionExternalType(
    functionType: FunctionType = functionType(),
) = ExternalType.Function(functionType)

fun tableExternalType(
    tableType: TableType = tableType(),
) = ExternalType.Table(tableType)

fun memoryExternalType(
    memoryType: MemoryType = memoryType(),
) = ExternalType.Memory(memoryType)

fun globalExternalType(
    globalType: GlobalType = globalType(),
) = ExternalType.Global(globalType)

fun tagExternalType(
    tagType: TagType = tagType(),
) = ExternalType.Tag(tagType)
