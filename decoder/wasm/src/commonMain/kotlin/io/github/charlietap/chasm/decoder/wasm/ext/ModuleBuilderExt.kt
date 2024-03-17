package io.github.charlietap.chasm.decoder.wasm.ext

import io.github.charlietap.chasm.decoder.wasm.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.wasm.section.CodeSection
import io.github.charlietap.chasm.decoder.wasm.section.CustomSection
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection
import io.github.charlietap.chasm.decoder.wasm.section.DataSection
import io.github.charlietap.chasm.decoder.wasm.section.ElementSection
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection
import io.github.charlietap.chasm.decoder.wasm.section.FunctionSection
import io.github.charlietap.chasm.decoder.wasm.section.GlobalSection
import io.github.charlietap.chasm.decoder.wasm.section.ImportSection
import io.github.charlietap.chasm.decoder.wasm.section.MemorySection
import io.github.charlietap.chasm.decoder.wasm.section.Section
import io.github.charlietap.chasm.decoder.wasm.section.StartSection
import io.github.charlietap.chasm.decoder.wasm.section.TableSection
import io.github.charlietap.chasm.decoder.wasm.section.TypeSection

internal fun ModuleBuilder.section(section: Section) = when (section) {
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
