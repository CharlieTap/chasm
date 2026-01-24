package io.github.charlietap.chasm.validator.validator.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal typealias LaneIndexValidator = (Byte, Int) -> Result<Unit, ModuleValidatorError>

internal fun LaneIndexValidator(
    laneIdx: Byte,
    maxLanes: Int,
): Result<Unit, ModuleValidatorError> {
    return if (laneIdx.toInt() and 0xFF >= maxLanes) {
        Err(InstructionValidatorError.InvalidLaneIndex)
    } else {
        Ok(Unit)
    }
}
