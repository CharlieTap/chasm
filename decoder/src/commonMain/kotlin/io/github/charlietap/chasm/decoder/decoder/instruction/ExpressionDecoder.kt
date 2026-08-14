package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.builder.InstructionBlockBuilder
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ExpressionDecoder(
    context: CodeBodyDecoderContext,
): Result<Expression, WasmDecodeError> =
    ExpressionDecoder(
        context,
        ::InstructionDecoder,
    )

internal inline fun ExpressionDecoder(
    context: CodeBodyDecoderContext,
    crossinline instructionDecoder: CodeBodyDecoder<Instruction>,
): Result<Expression, WasmDecodeError> = binding {
    val builder = InstructionBlockBuilder()
    var depth = 0

    while (true) {
        when (context.reader.peekUByte()) {
            END -> {
                context.reader.ubyte()

                if (depth == 0) {
                    break
                }

                depth--
                var endCount = 1

                while (depth > 0 && context.reader.peekUByte() == END) {
                    context.reader.ubyte()
                    depth--
                    endCount++
                }

                builder.appendEnd(endCount)
            }
            ELSE -> {
                context.reader.ubyte()
                builder.append(ControlInstruction.Else)
            }
            else -> {
                val instruction = instructionDecoder(context).bind()
                builder.append(instruction)

                when (instruction) {
                    is ControlInstruction.Block,
                    is ControlInstruction.Loop,
                    is ControlInstruction.If,
                    is ControlInstruction.TryTable,
                    -> depth++

                    else -> Unit
                }
            }
        }
    }

    Expression(builder.build())
}
