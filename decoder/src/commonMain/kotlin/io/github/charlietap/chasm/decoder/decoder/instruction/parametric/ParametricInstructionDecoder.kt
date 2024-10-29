package io.github.charlietap.chasm.decoder.decoder.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.DROP
import io.github.charlietap.chasm.decoder.decoder.instruction.SELECT
import io.github.charlietap.chasm.decoder.decoder.instruction.SELECT_W_TYPE
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ParametricInstructionDecoder(
    context: DecoderContext,
): Result<ParametricInstruction, WasmDecodeError> =
    ParametricInstructionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal inline fun ParametricInstructionDecoder(
    context: DecoderContext,
    crossinline vectorDecoder: VectorDecoder<ValueType>,
    noinline valueTypeDecoder: Decoder<ValueType>,
): Result<ParametricInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte().bind()) {
        DROP -> {
            ParametricInstruction.Drop
        }
        SELECT -> {
            ParametricInstruction.Select
        }
        SELECT_W_TYPE -> {
            val valTypes = vectorDecoder(context, valueTypeDecoder).bind()
            ParametricInstruction.SelectWithType(valTypes.vector)
        }

        else -> Err(InstructionDecodeError.InvalidParametricInstruction(opcode)).bind<ParametricInstruction>()
    }
}
