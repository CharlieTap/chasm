package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.F32_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.F32_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.F64_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.F64_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD16_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD16_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD8_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD8_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_STORE16
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_STORE8
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD16_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD16_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD32_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD32_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD8_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD8_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE16
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE32
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE8
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_GROW
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_SIZE
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun MemoryInstructionDecoder(
    context: DecoderContext,
): Result<MemoryInstruction, WasmDecodeError> =
    MemoryInstructionDecoder(
        context = context,
        memArgWithIndexDecoder = ::MemArgWithIndexDecoder,
        memoryGrowDecoder = ::MemoryGrowInstructionDecoder,
        memorySizeDecoder = ::MemorySizeInstructionDecoder,
    )

internal inline fun MemoryInstructionDecoder(
    context: DecoderContext,
    crossinline memArgWithIndexDecoder: Decoder<MemArgWithIndex>,
    crossinline memoryGrowDecoder: Decoder<MemoryInstruction.MemoryGrow>,
    crossinline memorySizeDecoder: Decoder<MemoryInstruction.MemorySize>,
): Result<MemoryInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte().bind()) {
        I32_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        F32_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.F32Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        F64_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.F64Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_LOAD8_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Load8S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_LOAD8_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Load8U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_LOAD16_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Load16S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_LOAD16_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Load16U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD8_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load8S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD8_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load8U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD16_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load16S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD16_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load16U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD32_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load32S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_LOAD32_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Load32U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        F32_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.F32Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        F64_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.F64Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_STORE8 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Store8(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_STORE16 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I32Store16(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_STORE8 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Store8(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_STORE16 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Store16(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_STORE32 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            MemoryInstruction.I64Store32(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        MEMORY_SIZE -> memorySizeDecoder(context).bind()
        MEMORY_GROW -> memoryGrowDecoder(context).bind()

        else -> Err(InstructionDecodeError.InvalidMemoryInstruction(opcode)).bind<MemoryInstruction>()
    }
}
