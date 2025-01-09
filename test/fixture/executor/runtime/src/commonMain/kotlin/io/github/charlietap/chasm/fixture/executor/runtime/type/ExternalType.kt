package io.github.charlietap.chasm.fixture.executor.runtime.type

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.executor.runtime.type.ExternalType
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.globalType
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.ast.type.tableType
import io.github.charlietap.chasm.fixture.ast.type.tagType

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
