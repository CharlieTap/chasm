package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CatchHandlerDecoderTest {

    @Test
    fun `can decode a catch handler`() {

        val reader = FakeUByteReader {
            Ok(CATCH_HANDLER_CATCH)
        }
        val context = decoderContext(reader)

        val labelIndex = labelIndex()
        val labelIndexDecoder: Decoder<Index.LabelIndex> = {
            Ok(labelIndex)
        }

        val tagIndex = tagIndex()
        val tagIndexDecoder: Decoder<Index.TagIndex> = {
            Ok(tagIndex)
        }

        val expected = Ok(ControlInstruction.CatchHandler.Catch(tagIndex, labelIndex))

        val actual = CatchHandlerDecoder(
            context = context,
            labelIndexDecoder = labelIndexDecoder,
            tagIndexDecoder = tagIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a catch ref handler`() {

        val reader = FakeUByteReader {
            Ok(CATCH_HANDLER_CATCH_REF)
        }
        val context = decoderContext(reader)

        val labelIndex = labelIndex()
        val labelIndexDecoder: Decoder<Index.LabelIndex> = {
            Ok(labelIndex)
        }

        val tagIndex = tagIndex()
        val tagIndexDecoder: Decoder<Index.TagIndex> = {
            Ok(tagIndex)
        }

        val expected = Ok(ControlInstruction.CatchHandler.CatchRef(tagIndex, labelIndex))

        val actual = CatchHandlerDecoder(
            context = context,
            labelIndexDecoder = labelIndexDecoder,
            tagIndexDecoder = tagIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a catch all handler`() {

        val reader = FakeUByteReader {
            Ok(CATCH_HANDLER_CATCH_ALL)
        }
        val context = decoderContext(reader)

        val labelIndex = labelIndex()
        val labelIndexDecoder: Decoder<Index.LabelIndex> = {
            Ok(labelIndex)
        }

        val expected = Ok(ControlInstruction.CatchHandler.CatchAll(labelIndex))

        val actual = CatchHandlerDecoder(
            context = context,
            labelIndexDecoder = labelIndexDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a catch all ref handler`() {

        val reader = FakeUByteReader {
            Ok(CATCH_HANDLER_CATCH_ALL_REF)
        }
        val context = decoderContext(reader)

        val labelIndex = labelIndex()
        val labelIndexDecoder: Decoder<Index.LabelIndex> = {
            Ok(labelIndex)
        }

        val expected = Ok(ControlInstruction.CatchHandler.CatchAllRef(labelIndex))

        val actual = CatchHandlerDecoder(
            context = context,
            labelIndexDecoder = labelIndexDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error if catch handler is unknown`() {

        val reader = FakeUByteReader {
            Ok(0xFFu)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidCatchHandler(0xFFu))

        val actual = CatchHandlerDecoder(
            context = context,
            labelIndexDecoder = neverLabelIndexDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    private companion object {
        val neverLabelIndexDecoder: Decoder<Index.LabelIndex> = {
            fail("label index decoder should not be called in this scenario.")
        }
        val neverTagIndexDecoder: Decoder<Index.TagIndex> = {
            fail("tag index decoder should not be called in this scenario.")
        }
    }
}
