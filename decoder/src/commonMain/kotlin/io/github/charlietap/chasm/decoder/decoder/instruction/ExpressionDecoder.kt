package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.scope.BlockScope
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ExpressionDecoder(
    context: DecoderContext,
): Result<Expression, WasmDecodeError> =
    ExpressionDecoder(
        context,
        ::BlockScope,
        ::InstructionBlockDecoder,
    )

internal inline fun ExpressionDecoder(
    context: DecoderContext,
    crossinline scope: Scope<UByte>,
    crossinline instructionBlockDecoder: Decoder<List<Instruction>>,
): Result<Expression, WasmDecodeError> = binding {

    val scopedContext = scope(context, END).bind()
    val instructions = instructionBlockDecoder(scopedContext).bind()

    Expression(instructions)
}
