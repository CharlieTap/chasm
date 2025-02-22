package io.github.charlietap.chasm.decoder.decoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.GLOBAL_GET
import io.github.charlietap.chasm.decoder.decoder.instruction.GLOBAL_SET
import io.github.charlietap.chasm.decoder.decoder.instruction.LOCAL_GET
import io.github.charlietap.chasm.decoder.decoder.instruction.LOCAL_SET
import io.github.charlietap.chasm.decoder.decoder.instruction.LOCAL_TEE
import io.github.charlietap.chasm.decoder.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.LocalIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun VariableInstructionDecoder(
    context: DecoderContext,
): Result<VariableInstruction, WasmDecodeError> =
    VariableInstructionDecoder(
        context = context,
        localIndexDecoder = ::LocalIndexDecoder,
        globalIndexDecoder = ::GlobalIndexDecoder,
    )

internal inline fun VariableInstructionDecoder(
    context: DecoderContext,
    crossinline localIndexDecoder: Decoder<Index.LocalIndex>,
    crossinline globalIndexDecoder: Decoder<Index.GlobalIndex>,
): Result<VariableInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte().bind()) {
        LOCAL_GET -> {
            VariableInstruction.LocalGet(localIndexDecoder(context).bind())
        }
        LOCAL_SET -> {
            VariableInstruction.LocalSet(localIndexDecoder(context).bind())
        }
        LOCAL_TEE -> {
            VariableInstruction.LocalTee(localIndexDecoder(context).bind())
        }
        GLOBAL_GET -> {
            VariableInstruction.GlobalGet(globalIndexDecoder(context).bind())
        }
        GLOBAL_SET -> {
            VariableInstruction.GlobalSet(globalIndexDecoder(context).bind())
        }

        else -> Err(InstructionDecodeError.InvalidVariableInstruction(opcode)).bind<VariableInstruction>()
    }
}
