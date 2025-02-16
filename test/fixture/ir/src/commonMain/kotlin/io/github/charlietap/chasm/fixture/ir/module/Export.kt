package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.ir.module.Export
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.value.NameValue

fun functionExportDescriptor(
    functionIndex: Index.FunctionIndex = functionIndex(),
) = Export.Descriptor.Function(
    functionIndex = functionIndex,
)

fun tableExportDescriptor(
    tableIndex: Index.TableIndex = tableIndex(),
) = Export.Descriptor.Table(
    tableIndex = tableIndex,
)

fun memoryExportDescriptor(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
) = Export.Descriptor.Memory(
    memoryIndex = memoryIndex,
)

fun globalExportDescriptor(
    globalIndex: Index.GlobalIndex = globalIndex(),
) = Export.Descriptor.Global(
    globalIndex = globalIndex,
)

fun tagExportDescriptor(
    tagIndex: Index.TagIndex = tagIndex(),
) = Export.Descriptor.Tag(
    tagIndex = tagIndex,
)

fun export(
    name: NameValue = NameValue(""),
    descriptor: Export.Descriptor = functionExportDescriptor(),
) = Export(
    name = name,
    descriptor = descriptor,
)
