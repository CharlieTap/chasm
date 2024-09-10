package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version

fun module(
    version: Version = Version.One,
    types: List<Type> = emptyList(),
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
