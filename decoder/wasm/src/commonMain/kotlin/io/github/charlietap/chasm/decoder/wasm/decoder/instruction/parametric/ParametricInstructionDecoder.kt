package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.DROP
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.SELECT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.SELECT_W_TYPE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ParametricInstructionDecoder(
    context: DecoderContext,
): Result<ParametricInstruction, WasmDecodeError> =
    ParametricInstructionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal fun ParametricInstructionDecoder(
    context: DecoderContext,
    vectorDecoder: VectorDecoder<ValueType>,
    valueTypeDecoder: Decoder<ValueType>,
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
