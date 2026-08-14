package io.github.charlietap.chasm.decoder.decoder.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.DROP
import io.github.charlietap.chasm.decoder.decoder.instruction.SELECT
import io.github.charlietap.chasm.decoder.decoder.instruction.SELECT_W_TYPE
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.CodeBodyVectorDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.ValueType

internal fun ParametricInstructionDecoder(
    context: CodeBodyDecoderContext,
): Result<ParametricInstruction, WasmDecodeError> =
    ParametricInstructionDecoder(
        context = context,
        vectorDecoder = ::CodeBodyVectorDecoder,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal inline fun ParametricInstructionDecoder(
    context: CodeBodyDecoderContext,
    crossinline vectorDecoder: CodeBodyVectorDecoder<ValueType>,
    noinline valueTypeDecoder: CodeBodyDecoder<ValueType>,
): Result<ParametricInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte()) {
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
