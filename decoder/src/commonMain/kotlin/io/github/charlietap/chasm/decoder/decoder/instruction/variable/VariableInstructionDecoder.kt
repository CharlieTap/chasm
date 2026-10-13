package io.github.charlietap.chasm.decoder.decoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
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
    context: CodeBodyDecoderContext,
): Result<VariableInstruction, WasmDecodeError> =
    VariableInstructionDecoder(
        context = context,
        localIndexDecoder = ::LocalIndexDecoder,
        globalIndexDecoder = ::GlobalIndexDecoder,
    )

internal inline fun VariableInstructionDecoder(
    context: CodeBodyDecoderContext,
    crossinline localIndexDecoder: CodeBodyDecoder<Index.LocalIndex>,
    crossinline globalIndexDecoder: CodeBodyDecoder<Index.GlobalIndex>,
): Result<VariableInstruction, WasmDecodeError> =
    when (val opcode = context.reader.ubyte()) {
        LOCAL_GET -> localIndexDecoder(context).map(VariableInstruction::LocalGet)
        LOCAL_SET -> localIndexDecoder(context).map(VariableInstruction::LocalSet)
        LOCAL_TEE -> localIndexDecoder(context).map(VariableInstruction::LocalTee)
        GLOBAL_GET -> globalIndexDecoder(context).map(VariableInstruction::GlobalGet)
        GLOBAL_SET -> globalIndexDecoder(context).map(VariableInstruction::GlobalSet)

        else -> Err(InstructionDecodeError.InvalidVariableInstruction(opcode))
    }
