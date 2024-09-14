package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TagIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun CatchHandlerDecoder(
    context: DecoderContext,
): Result<ControlInstruction.CatchHandler, WasmDecodeError> = CatchHandlerDecoder(
    context = context,
    labelIndexDecoder = ::LabelIndexDecoder,
    tagIndexDecoder = ::TagIndexDecoder,
)

internal fun CatchHandlerDecoder(
    context: DecoderContext,
    labelIndexDecoder: Decoder<Index.LabelIndex>,
    tagIndexDecoder: Decoder<Index.TagIndex>,
): Result<ControlInstruction.CatchHandler, WasmDecodeError> = binding {

    val handler = context.reader.ubyte().bind()

    when (handler) {
        CATCH_HANDLER_CATCH -> {
            val tagIndex = tagIndexDecoder(context).bind()
            val labelIndex = labelIndexDecoder(context).bind()
            ControlInstruction.CatchHandler.Catch(tagIndex, labelIndex)
        }
        CATCH_HANDLER_CATCH_REF -> {
            val tagIndex = tagIndexDecoder(context).bind()
            val labelIndex = labelIndexDecoder(context).bind()
            ControlInstruction.CatchHandler.CatchRef(tagIndex, labelIndex)
        }
        CATCH_HANDLER_CATCH_ALL -> {
            val labelIndex = labelIndexDecoder(context).bind()
            ControlInstruction.CatchHandler.CatchAll(labelIndex)
        }
        CATCH_HANDLER_CATCH_ALL_REF -> {
            val labelIndex = labelIndexDecoder(context).bind()
            ControlInstruction.CatchHandler.CatchAllRef(labelIndex)
        }
        else -> Err(InstructionDecodeError.InvalidCatchHandler(handler)).bind()
    }
}

internal const val CATCH_HANDLER_CATCH: UByte = 0x00u
internal const val CATCH_HANDLER_CATCH_REF: UByte = 0x01u
internal const val CATCH_HANDLER_CATCH_ALL: UByte = 0x02u
internal const val CATCH_HANDLER_CATCH_ALL_REF: UByte = 0x03u
