package io.github.charlietap.chasm.decoder.decoder.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.get
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun VectorInstructionDecoder(
    context: DecoderContext,
): Result<VectorInstruction, WasmDecodeError> =
    Err(InstructionDecodeError.UnknownInstruction(context.reader.ubyte().get() ?: 0u))
