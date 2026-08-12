package io.github.charlietap.chasm.validator.validator.instruction.vector

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.popF32OrThrow
import io.github.charlietap.chasm.validator.ext.popF64OrThrow
import io.github.charlietap.chasm.validator.ext.popI32OrThrow
import io.github.charlietap.chasm.validator.ext.popI64OrThrow
import io.github.charlietap.chasm.validator.ext.popMemoryAddressOrThrow
import io.github.charlietap.chasm.validator.ext.popV128OrThrow
import io.github.charlietap.chasm.validator.ext.pushF32
import io.github.charlietap.chasm.validator.ext.pushF64
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.pushI64
import io.github.charlietap.chasm.validator.ext.pushV128
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun VectorInstructionValidator(
    context: ModuleValidationContext,
    instruction: VectorInstruction,
): Result<Unit, ModuleValidatorError> {
    when (instruction) {
        is VectorInstruction.Operator -> return VectorOperatorValidator(context, instruction.opcode)
        is VectorInstruction.V128Load -> {
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Load8x8S,
        is VectorInstruction.V128Load8x8U,
        is VectorInstruction.V128Load16x4S,
        is VectorInstruction.V128Load16x4U,
        is VectorInstruction.V128Load32x2S,
        is VectorInstruction.V128Load32x2U,
        -> {
            val memoryIndex = instruction.memoryIndex
            val memArg = instruction.memArg
            MemArgValidator(context, memArg, memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popMemoryAddressOrThrow(memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Load8Splat,
        is VectorInstruction.V128Load16Splat,
        is VectorInstruction.V128Load32Splat,
        is VectorInstruction.V128Load64Splat,
        -> {
            val memoryIndex = instruction.memoryIndex
            val memArg = instruction.memArg
            MemArgValidator(context, memArg, memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popMemoryAddressOrThrow(memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Load32Zero,
        is VectorInstruction.V128Load64Zero,
        -> {
            val memoryIndex = instruction.memoryIndex
            val memArg = instruction.memArg
            MemArgValidator(context, memArg, memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popMemoryAddressOrThrow(memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Store -> {
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
        }
        is VectorInstruction.V128Load8Lane -> {
            LaneIndexValidator(instruction.laneIdx, 16).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Load16Lane -> {
            LaneIndexValidator(instruction.laneIdx, 8).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Load32Lane -> {
            LaneIndexValidator(instruction.laneIdx, 4).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Load64Lane -> {
            LaneIndexValidator(instruction.laneIdx, 2).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
            context.pushV128()
        }
        is VectorInstruction.V128Store8Lane -> {
            LaneIndexValidator(instruction.laneIdx, 16).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
        }
        is VectorInstruction.V128Store16Lane -> {
            LaneIndexValidator(instruction.laneIdx, 8).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
        }
        is VectorInstruction.V128Store32Lane -> {
            LaneIndexValidator(instruction.laneIdx, 4).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
        }
        is VectorInstruction.V128Store64Lane -> {
            LaneIndexValidator(instruction.laneIdx, 2).getOrThrowValidation()
            MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()
            context.popV128OrThrow()
            context.popMemoryAddressOrThrow(instruction.memoryIndex)
        }
        is VectorInstruction.V128Const -> {
            context.pushV128()
        }
        is VectorInstruction.I8x16Shuffle -> {
            instruction.laneIndices.forEach { laneIdx ->
                LaneIndexValidator(laneIdx, 32).getOrThrowValidation()
            }
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        is VectorInstruction.I8x16ExtractLaneS -> {
            LaneIndexValidator(instruction.laneIdx, 16).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushI32()
        }
        is VectorInstruction.I8x16ExtractLaneU -> {
            LaneIndexValidator(instruction.laneIdx, 16).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushI32()
        }
        is VectorInstruction.I16x8ExtractLaneS -> {
            LaneIndexValidator(instruction.laneIdx, 8).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushI32()
        }
        is VectorInstruction.I16x8ExtractLaneU -> {
            LaneIndexValidator(instruction.laneIdx, 8).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushI32()
        }
        is VectorInstruction.I32x4ExtractLane -> {
            LaneIndexValidator(instruction.laneIdx, 4).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushI32()
        }
        is VectorInstruction.I64x2ExtractLane -> {
            LaneIndexValidator(instruction.laneIdx, 2).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushI64()
        }
        is VectorInstruction.F32x4ExtractLane -> {
            LaneIndexValidator(instruction.laneIdx, 4).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushF32()
        }
        is VectorInstruction.F64x2ExtractLane -> {
            LaneIndexValidator(instruction.laneIdx, 2).getOrThrowValidation()
            context.popV128OrThrow()
            context.pushF64()
        }
        is VectorInstruction.I8x16ReplaceLane -> {
            LaneIndexValidator(instruction.laneIdx, 16).getOrThrowValidation()
            context.popI32OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        is VectorInstruction.I16x8ReplaceLane -> {
            LaneIndexValidator(instruction.laneIdx, 8).getOrThrowValidation()
            context.popI32OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        is VectorInstruction.I32x4ReplaceLane -> {
            LaneIndexValidator(instruction.laneIdx, 4).getOrThrowValidation()
            context.popI32OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        is VectorInstruction.I64x2ReplaceLane -> {
            LaneIndexValidator(instruction.laneIdx, 2).getOrThrowValidation()
            context.popI64OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        is VectorInstruction.F32x4ReplaceLane -> {
            LaneIndexValidator(instruction.laneIdx, 4).getOrThrowValidation()
            context.popF32OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        is VectorInstruction.F64x2ReplaceLane -> {
            LaneIndexValidator(instruction.laneIdx, 2).getOrThrowValidation()
            context.popF64OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
    }
    return Ok(Unit)
}
