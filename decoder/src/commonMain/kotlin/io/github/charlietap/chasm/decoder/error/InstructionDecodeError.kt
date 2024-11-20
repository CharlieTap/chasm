package io.github.charlietap.chasm.decoder.error

import kotlin.jvm.JvmInline

sealed interface InstructionDecodeError : WasmDecodeError {

    @JvmInline
    value class UnknownInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidAtomicMemoryInstruction(val opcode: UInt) : InstructionDecodeError

    @JvmInline
    value class InvalidNumericInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidReferenceInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidParametricInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidVariableInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidTableInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidMemoryInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidControlInstruction(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidAlignment(val alignment: UInt) : InstructionDecodeError

    @JvmInline
    value class InvalidCastFlags(val byte: UByte) : InstructionDecodeError

    @JvmInline
    value class InvalidCatchHandler(val byte: UByte) : InstructionDecodeError

    data class InvalidPrefixInstruction(val prefix: UByte, val opcode: UInt) : InstructionDecodeError

    data object ReservedByteNotZero : InstructionDecodeError
}
