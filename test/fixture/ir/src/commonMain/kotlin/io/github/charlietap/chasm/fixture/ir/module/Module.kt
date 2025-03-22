package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.ir.module.Custom
import io.github.charlietap.chasm.ir.module.DataSegment
import io.github.charlietap.chasm.ir.module.ElementSegment
import io.github.charlietap.chasm.ir.module.Export
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Global
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Memory
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.ir.module.StartFunction
import io.github.charlietap.chasm.ir.module.Table
import io.github.charlietap.chasm.ir.module.Tag
import io.github.charlietap.chasm.ir.module.Type
import io.github.charlietap.chasm.ir.module.Version
import io.github.charlietap.chasm.type.DefinedType

fun module(
    version: Version = Version.One,
    types: List<Type> = emptyList(),
    definedTypes: List<DefinedType> = emptyList(),
    imports: List<Import> = emptyList(),
    functions: List<Function> = emptyList(),
    tables: List<Table> = emptyList(),
    memories: List<Memory> = emptyList(),
    tags: List<Tag> = emptyList(),
    globals: List<Global> = emptyList(),
    exports: List<Export> = emptyList(),
    startFunction: StartFunction? = null,
    elementSegments: List<ElementSegment> = emptyList(),
    dataSegments: List<DataSegment> = emptyList(),
    customs: List<Custom> = emptyList(),
) = Module(
    version = version,
    types = types,
    definedTypes = definedTypes,
    imports = imports,
    functions = functions,
    tables = tables,
    memories = memories,
    tags = tags,
    globals = globals,
    exports = exports,
    startFunction = startFunction,
    elementSegments = elementSegments,
    dataSegments = dataSegments,
    customs = customs,
)
