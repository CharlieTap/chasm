package io.github.charlietap.chasm.fixture.executor.runtime.type

import io.github.charlietap.chasm.executor.runtime.type.ExternalType
import io.github.charlietap.chasm.fixture.ir.type.functionType
import io.github.charlietap.chasm.fixture.ir.type.globalType
import io.github.charlietap.chasm.fixture.ir.type.memoryType
import io.github.charlietap.chasm.fixture.ir.type.tableType
import io.github.charlietap.chasm.fixture.ir.type.tagType
import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.GlobalType
import io.github.charlietap.chasm.ir.type.MemoryType
import io.github.charlietap.chasm.ir.type.TableType
import io.github.charlietap.chasm.ir.type.TagType

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
