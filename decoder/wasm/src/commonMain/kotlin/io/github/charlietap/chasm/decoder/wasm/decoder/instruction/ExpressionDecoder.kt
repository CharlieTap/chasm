package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.context.scope.BlockScope
import io.github.charlietap.chasm.decoder.wasm.context.scope.Scope
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ExpressionDecoder(
    context: DecoderContext,
): Result<Expression, WasmDecodeError> =
    ExpressionDecoder(
        context,
        ::BlockScope,
        ::InstructionBlockDecoder,
    )

internal fun ExpressionDecoder(
    context: DecoderContext,
    scope: Scope<UByte>,
    instructionBlockDecoder: Decoder<List<Instruction>>,
): Result<Expression, WasmDecodeError> = binding {

    val scopedContext = scope(context, END).bind()
    val instructions = instructionBlockDecoder(scopedContext).bind()

    Expression(instructions)
}
