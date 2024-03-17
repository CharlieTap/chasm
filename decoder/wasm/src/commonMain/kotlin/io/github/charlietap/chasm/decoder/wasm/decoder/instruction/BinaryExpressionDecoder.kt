package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryExpressionDecoder(
    reader: WasmBinaryReader,
): Result<Expression, WasmDecodeError> =
    BinaryExpressionDecoder(
        reader,
        ::BinaryInstructionBlockDecoder,
    )

internal fun BinaryExpressionDecoder(
    reader: WasmBinaryReader,
    instructionBlockDecoder: InstructionBlockDecoder,
): Result<Expression, WasmDecodeError> = binding {

    val instructions = instructionBlockDecoder(reader, END).bind()

    Expression(instructions)
}
