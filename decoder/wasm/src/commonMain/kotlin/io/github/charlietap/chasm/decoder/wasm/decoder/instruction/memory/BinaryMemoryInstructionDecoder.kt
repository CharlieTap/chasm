package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_LOAD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_STORE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_LOAD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_STORE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LOAD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LOAD16_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LOAD16_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LOAD8_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LOAD8_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_STORE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_STORE16
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_STORE8
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD16_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD16_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD32_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD8_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LOAD8_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_STORE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_STORE16
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_STORE32
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_STORE8
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.MEMORY_GROW
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.MEMORY_SIZE
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryMemoryInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryMemoryInstructionDecoder(
        reader = reader,
        opcode = opcode,
        memArgDecoder = ::BinaryMemArgDecoder,
    )

internal fun BinaryMemoryInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    memArgDecoder: MemArgDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        I32_LOAD -> MemoryInstruction.I32Load(memArgDecoder(reader).bind())
        I64_LOAD -> MemoryInstruction.I64Load(memArgDecoder(reader).bind())
        F32_LOAD -> MemoryInstruction.F32Load(memArgDecoder(reader).bind())
        F64_LOAD -> MemoryInstruction.F64Load(memArgDecoder(reader).bind())
        I32_LOAD8_S -> MemoryInstruction.I32Load8S(memArgDecoder(reader).bind())
        I32_LOAD8_U -> MemoryInstruction.I32Load8U(memArgDecoder(reader).bind())
        I32_LOAD16_S -> MemoryInstruction.I32Load16S(memArgDecoder(reader).bind())
        I32_LOAD16_U -> MemoryInstruction.I32Load16U(memArgDecoder(reader).bind())
        I64_LOAD8_S -> MemoryInstruction.I64Load8S(memArgDecoder(reader).bind())
        I64_LOAD8_U -> MemoryInstruction.I64Load8U(memArgDecoder(reader).bind())
        I64_LOAD16_S -> MemoryInstruction.I64Load16S(memArgDecoder(reader).bind())
        I64_LOAD16_U -> MemoryInstruction.I64Load16U(memArgDecoder(reader).bind())
        I64_LOAD32_S -> MemoryInstruction.I64Load32S(memArgDecoder(reader).bind())
        I64_LOAD32_U -> MemoryInstruction.I64Load32U(memArgDecoder(reader).bind())
        I32_STORE -> MemoryInstruction.I32Store(memArgDecoder(reader).bind())
        I64_STORE -> MemoryInstruction.I64Store(memArgDecoder(reader).bind())
        F32_STORE -> MemoryInstruction.F32Store(memArgDecoder(reader).bind())
        F64_STORE -> MemoryInstruction.F64Store(memArgDecoder(reader).bind())
        I32_STORE8 -> MemoryInstruction.I32Store8(memArgDecoder(reader).bind())
        I32_STORE16 -> MemoryInstruction.I32Store16(memArgDecoder(reader).bind())
        I64_STORE8 -> MemoryInstruction.I64Store8(memArgDecoder(reader).bind())
        I64_STORE16 -> MemoryInstruction.I64Store16(memArgDecoder(reader).bind())
        I64_STORE32 -> MemoryInstruction.I64Store32(memArgDecoder(reader).bind())
        MEMORY_SIZE -> {
            val byte = reader.byte().bind()
            if (byte != 0.toByte()) {
                Err(InstructionDecodeError.ReservedByteNotZero).bind<Unit>()
            }
            MemoryInstruction.MemorySize
        }
        MEMORY_GROW -> {
            val byte = reader.byte().bind()
            if (byte != 0.toByte()) {
                Err(InstructionDecodeError.ReservedByteNotZero).bind<Unit>()
            }
            MemoryInstruction.MemoryGrow
        }

        else -> Err(InstructionDecodeError.InvalidMemoryInstruction(opcode)).bind<Instruction>()
    }
}
