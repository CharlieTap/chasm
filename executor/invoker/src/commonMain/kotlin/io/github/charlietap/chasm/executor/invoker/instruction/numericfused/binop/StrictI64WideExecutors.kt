package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Iiii,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Iiis,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Iisi,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Iiss,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Isii,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Isis,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Issi,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Isss,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Siii,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Siis,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Sisi,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Siss,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Ssii,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Ssis,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Sssi,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Add128Ssss,
) = executeI64Add128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Iiii,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Iiis,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Iisi,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Iiss,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Isii,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Isis,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Issi,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Isss,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    instruction.leftLow,
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Siii,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Siis,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Sisi,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Siss,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    instruction.leftHigh,
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Ssii,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Ssis,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    instruction.rightLow,
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Sssi,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    instruction.rightHigh,
)

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64Sub128Ssss,
) = executeI64Sub128(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftLowSlot),
    vstack.getFrameSlot(instruction.leftHighSlot),
    vstack.getFrameSlot(instruction.rightLowSlot),
    vstack.getFrameSlot(instruction.rightHighSlot),
)

internal inline fun I64MulWideSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideSIi,
) = executeI64MulWideSigned(vstack, instruction.destinationLowSlot, instruction.destinationHighSlot, instruction.left, instruction.right)

internal inline fun I64MulWideSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideSIs,
) = executeI64MulWideSigned(vstack, instruction.destinationLowSlot, instruction.destinationHighSlot, instruction.left, vstack.getFrameSlot(instruction.rightSlot))

internal inline fun I64MulWideSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideSSi,
) = executeI64MulWideSigned(vstack, instruction.destinationLowSlot, instruction.destinationHighSlot, vstack.getFrameSlot(instruction.leftSlot), instruction.right)

internal inline fun I64MulWideSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideSSs,
) = executeI64MulWideSigned(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftSlot),
    vstack.getFrameSlot(instruction.rightSlot),
)

internal inline fun I64MulWideUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideUIi,
) = executeI64MulWideUnsigned(vstack, instruction.destinationLowSlot, instruction.destinationHighSlot, instruction.left, instruction.right)

internal inline fun I64MulWideUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideUIs,
) = executeI64MulWideUnsigned(vstack, instruction.destinationLowSlot, instruction.destinationHighSlot, instruction.left, vstack.getFrameSlot(instruction.rightSlot))

internal inline fun I64MulWideUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideUSi,
) = executeI64MulWideUnsigned(vstack, instruction.destinationLowSlot, instruction.destinationHighSlot, vstack.getFrameSlot(instruction.leftSlot), instruction.right)

internal inline fun I64MulWideUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64MulWideUSs,
) = executeI64MulWideUnsigned(
    vstack,
    instruction.destinationLowSlot,
    instruction.destinationHighSlot,
    vstack.getFrameSlot(instruction.leftSlot),
    vstack.getFrameSlot(instruction.rightSlot),
)
