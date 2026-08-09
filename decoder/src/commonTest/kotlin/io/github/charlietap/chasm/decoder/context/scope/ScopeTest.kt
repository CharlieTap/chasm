package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import kotlin.test.Test
import kotlin.test.assertEquals

class ScopeTest {

    @Test
    fun `name scope restores its previous value after success and error`() {
        val context = decoderContext(nameSectionSize = 1u)

        val success = NameScope(context, 2u) { scopedContext ->
            assertEquals(2u, scopedContext.nameSectionSize)
            Ok(Unit)
        }
        assertEquals(Ok(Unit), success)
        assertEquals(1u, context.nameSectionSize)

        val error = WasmDecodeError.IOError(Exception())
        val failure = NameScope<Unit>(context, 3u) { scopedContext ->
            assertEquals(3u, scopedContext.nameSectionSize)
            Err(error)
        }
        assertEquals(Err(error), failure)
        assertEquals(1u, context.nameSectionSize)
    }

    @Test
    fun `section scope restores its previous values after success and error`() {
        val previous = SectionSize(1u) to SectionType.Type
        val context = decoderContext(
            sectionSize = previous.first,
            sectionType = previous.second,
        )

        val successValue = SectionSize(2u) to SectionType.Import
        val success = SectionScope(context, successValue) { scopedContext ->
            assertEquals(successValue.first, scopedContext.sectionSize)
            assertEquals(successValue.second, scopedContext.sectionType)
            Ok(Unit)
        }
        assertEquals(Ok(Unit), success)
        assertEquals(previous.first, context.sectionSize)
        assertEquals(previous.second, context.sectionType)

        val errorValue = SectionSize(3u) to SectionType.Function
        val error = WasmDecodeError.IOError(Exception())
        val failure = SectionScope<Unit>(context, errorValue) { scopedContext ->
            assertEquals(errorValue.first, scopedContext.sectionSize)
            assertEquals(errorValue.second, scopedContext.sectionType)
            Err(error)
        }
        assertEquals(Err(error), failure)
        assertEquals(previous.first, context.sectionSize)
        assertEquals(previous.second, context.sectionType)
    }

    @Test
    fun `vector scope restores its previous value after success and error`() {
        val context = decoderContext(index = 1)

        val success = VectorScope(context, 2) { scopedContext ->
            assertEquals(2, scopedContext.index)
            Ok(Unit)
        }
        assertEquals(Ok(Unit), success)
        assertEquals(1, context.index)

        val error = WasmDecodeError.IOError(Exception())
        val failure = VectorScope<Unit>(context, 3) { scopedContext ->
            assertEquals(3, scopedContext.index)
            Err(error)
        }
        assertEquals(Err(error), failure)
        assertEquals(1, context.index)
    }
}
