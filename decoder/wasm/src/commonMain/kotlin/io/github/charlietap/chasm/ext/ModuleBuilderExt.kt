package io.github.charlietap.chasm.ext

import io.github.charlietap.chasm.builder.ModuleBuilder
import io.github.charlietap.chasm.section.CodeSection
import io.github.charlietap.chasm.section.CustomSection
import io.github.charlietap.chasm.section.DataCountSection
import io.github.charlietap.chasm.section.DataSection
import io.github.charlietap.chasm.section.ElementSection
import io.github.charlietap.chasm.section.ExportSection
import io.github.charlietap.chasm.section.FunctionSection
import io.github.charlietap.chasm.section.GlobalSection
import io.github.charlietap.chasm.section.ImportSection
import io.github.charlietap.chasm.section.MemorySection
import io.github.charlietap.chasm.section.Section
import io.github.charlietap.chasm.section.StartSection
import io.github.charlietap.chasm.section.TableSection
import io.github.charlietap.chasm.section.TypeSection

fun ModuleBuilder.section(section: Section) = when (section) {
    is CustomSection -> custom(section.custom)
    is DataSection -> dataSegments(section.segments)
    is TypeSection -> types(section.types)
    is ImportSection -> imports(section.imports)
    is ExportSection -> exports(section.exports)
    is FunctionSection -> functionHeaders(section.headers)
    is CodeSection -> functionBodies(section.bodies)
    is ElementSection -> elementSegments(section.segments)
    is TableSection -> tables(section.tables)
    is MemorySection -> memories(section.memories)
    is GlobalSection -> globals(section.globals)
    is StartSection -> start(section.startFunction)
    is DataCountSection -> this
}
