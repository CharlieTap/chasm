package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.atomic.AtomicMemoryInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.vector.VectorInstructionDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun PrefixedInstructionDecoder(
    context: CodeBodyDecoderContext,
): Result<Instruction, WasmDecodeError> =
    PrefixedInstructionDecoder(
        context = context,
        prefixFCInstructionDecoder = ::PrefixFCInstructionDecoder,
        atomicMemoryInstructionDecoder = ::AtomicMemoryInstructionDecoder,
        gcInstructionDecoder = ::GCInstructionDecoder,
        vectorInstructionDecoder = ::VectorInstructionDecoder,
    )

internal inline fun PrefixedInstructionDecoder(
    context: CodeBodyDecoderContext,
    crossinline prefixFCInstructionDecoder: CodeBodyDecoder<Instruction>,
    crossinline atomicMemoryInstructionDecoder: CodeBodyDecoder<AtomicMemoryInstruction>,
    crossinline gcInstructionDecoder: CodeBodyDecoder<Instruction>,
    crossinline vectorInstructionDecoder: CodeBodyDecoder<VectorInstruction>,
): Result<Instruction, WasmDecodeError> = binding {

    val prefix = context.reader.ubyte()
    val opcode = context.reader
        .peekUInt()

    when (prefix) {
        PREFIX_FB -> gcInstructionDecoder(context).bind()
        PREFIX_FC -> prefixFCInstructionDecoder(context).bind()
        PREFIX_FD -> vectorInstructionDecoder(context).bind()
        PREFIX_FE -> atomicMemoryInstructionDecoder(context).bind()
        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode)).bind<Instruction>()
    }
}

internal const val PREFIX_FB: UByte = 0xFBu
internal const val PREFIX_FC: UByte = 0xFCu
internal const val PREFIX_FD: UByte = 0xFDu
internal const val PREFIX_FE: UByte = 0xFEu
