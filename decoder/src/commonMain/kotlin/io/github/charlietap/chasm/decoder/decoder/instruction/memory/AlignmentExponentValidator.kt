@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias AlignmentExponentValidator = (UInt) -> Result<Unit, WasmDecodeError>

internal inline fun AlignmentExponentValidator(
    exponent: UInt,
): Result<Unit, WasmDecodeError> = if (exponent >= EXPONENT_MAX) {
    Err(InstructionDecodeError.InvalidAlignment(exponent))
} else {
    Ok(Unit)
}

private const val EXPONENT_MAX: UInt = 32u
