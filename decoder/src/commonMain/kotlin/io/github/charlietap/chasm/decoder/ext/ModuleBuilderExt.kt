package io.github.charlietap.chasm.decoder.ext

import io.github.charlietap.chasm.decoder.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.section.CodeSection
import io.github.charlietap.chasm.decoder.section.CustomSection
import io.github.charlietap.chasm.decoder.section.DataCountSection
import io.github.charlietap.chasm.decoder.section.DataSection
import io.github.charlietap.chasm.decoder.section.ElementSection
import io.github.charlietap.chasm.decoder.section.ExportSection
import io.github.charlietap.chasm.decoder.section.FunctionSection
import io.github.charlietap.chasm.decoder.section.GlobalSection
import io.github.charlietap.chasm.decoder.section.ImportSection
import io.github.charlietap.chasm.decoder.section.MemorySection
import io.github.charlietap.chasm.decoder.section.Section
import io.github.charlietap.chasm.decoder.section.StartSection
import io.github.charlietap.chasm.decoder.section.TableSection
import io.github.charlietap.chasm.decoder.section.TagSection
import io.github.charlietap.chasm.decoder.section.TypeSection

internal fun ModuleBuilder.section(section: Section) = when (section) {
    is CustomSection -> custom(section.custom)
    is DataSection -> dataSegments(section.segments)
    is TypeSection -> types(section.types, section.definedTypes)
    is ImportSection -> imports(section.imports)
    is ExportSection -> exports(section.exports)
    is FunctionSection -> functionHeaders(section.headers)
    is CodeSection -> functionBodies(section.bodies)
    is ElementSection -> elementSegments(section.segments)
    is TableSection -> tables(section.tables)
    is MemorySection -> memories(section.memories)
    is GlobalSection -> globals(section.globals)
    is StartSection -> start(section.startFunction)
    is DataCountSection -> dataCount(section.dataCount)
    is TagSection -> tags(section.tags)
}
