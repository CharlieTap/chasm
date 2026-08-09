package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.type.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.ast.module.import as moduleImport

class ModuleDecoderContextTest {

    @Test
    fun `reset clears all mutable module decode state`() {
        val context = decoderContext(
            imports = listOf(moduleImport()),
            requiresDataCount = true,
            nameSectionSize = 2u,
            sectionSize = SectionSize(3u),
            sectionType = SectionType.Type,
            types = mutableListOf(type()),
            definedTypes = mutableListOf(definedType()),
            index = 4,
        )

        context.reset()

        assertEquals(emptyList(), context.imports)
        assertEquals(false, context.requiresDataCount)
        assertEquals(0u, context.nameSectionSize)
        assertEquals(SectionSize(0u), context.sectionSize)
        assertEquals(SectionType.Custom, context.sectionType)
        assertEquals(emptyList(), context.types)
        assertEquals(emptyList(), context.definedTypes)
        assertEquals(0, context.index)
    }
}
