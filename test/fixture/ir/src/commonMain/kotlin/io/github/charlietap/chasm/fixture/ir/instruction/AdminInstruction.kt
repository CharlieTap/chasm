package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.StackAdjustment
import io.github.charlietap.chasm.type.ReferenceType

fun adminInstruction(): AdminInstruction = jumpInstruction()

fun jumpInstruction(
    offset: Int = 0,
) = AdminInstruction.JumpInstruction.Jump(
    offset = offset,
)

fun jumpAdjustingInstruction(
    offset: Int = 0,
    adjustment: StackAdjustment = stackAdjustment(),
) = AdminInstruction.JumpInstruction.JumpAdjusting(
    offset = offset,
    adjustment = adjustment,
)

fun jumpIfInstruction(
    offset: Int = 0,
    adjustment: StackAdjustment = stackAdjustment(),
) = AdminInstruction.JumpInstruction.JumpIf(
    offset = offset,
    adjustment = adjustment,
)

fun jumpIfNotInstruction(
    offset: Int = 0,
) = AdminInstruction.JumpInstruction.JumpIfNot(
    offset = offset,
)

fun jumpTableInstruction(
    offsets: MutableList<Int> = mutableListOf(0),
    adjustments: List<StackAdjustment> = listOf(stackAdjustment()),
    defaultOffset: Int = 0,
    defaultAdjustment: StackAdjustment = stackAdjustment(),
) = AdminInstruction.JumpInstruction.JumpTable(
    offsets = offsets,
    adjustments = adjustments,
    defaultOffset = defaultOffset,
    defaultAdjustment = defaultAdjustment,
)

fun jumpOnNullInstruction(
    offset: Int = 0,
    adjustment: StackAdjustment = stackAdjustment(),
) = AdminInstruction.JumpInstruction.JumpOnNull(
    offset = offset,
    adjustment = adjustment,
)

fun jumpOnNonNullInstruction(
    offset: Int = 0,
    adjustment: StackAdjustment = stackAdjustment(),
) = AdminInstruction.JumpInstruction.JumpOnNonNull(
    offset = offset,
    adjustment = adjustment,
)

fun jumpOnCastInstruction(
    offset: Int = 0,
    adjustment: StackAdjustment = stackAdjustment(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = AdminInstruction.JumpInstruction.JumpOnCast(
    offset = offset,
    adjustment = adjustment,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)

fun jumpOnCastFailInstruction(
    offset: Int = 0,
    adjustment: StackAdjustment = stackAdjustment(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = AdminInstruction.JumpInstruction.JumpOnCastFail(
    offset = offset,
    adjustment = adjustment,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)

fun returnFunctionInstruction(
    adjustment: StackAdjustment = stackAdjustment(),
) = AdminInstruction.ReturnFunction(
    adjustment = adjustment,
)
