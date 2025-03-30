package io.github.charlietap.chasm.predecoder.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpAdjustingDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfNotDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastFailDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNonNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpTableDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.ReturnFunctionDispatcher
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.Jump
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpAdjusting
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpIf
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpIfNot
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpOnCast
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpOnCastFail
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpOnNonNull
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpOnNull
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.JumpTable
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction.ReturnFunction

internal fun AdminInstructionPredecoder(
    context: PredecodingContext,
    instruction: AdminInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    AdminInstructionPredecoder(
        context = context,
        instruction = instruction,
        jumpDispatcher = ::JumpDispatcher,
        jumpAdjustingDispatcher = ::JumpAdjustingDispatcher,
        jumpIfDispatcher = ::JumpIfDispatcher,
        jumpIfNotDispatcher = ::JumpIfNotDispatcher,
        jumpTableDispatcher = ::JumpTableDispatcher,
        jumpOnNullDispatcher = ::JumpOnNullDispatcher,
        jumpOnNonNullDispatcher = ::JumpOnNonNullDispatcher,
        jumpOnCastDispatcher = ::JumpOnCastDispatcher,
        jumpOnCastFailDispatcher = ::JumpOnCastFailDispatcher,
        returnFunctionDispatcher = ::ReturnFunctionDispatcher,
    )

internal inline fun AdminInstructionPredecoder(
    context: PredecodingContext,
    instruction: AdminInstruction,
    crossinline jumpDispatcher: Dispatcher<Jump>,
    crossinline jumpAdjustingDispatcher: Dispatcher<JumpAdjusting>,
    crossinline jumpIfDispatcher: Dispatcher<JumpIf>,
    crossinline jumpIfNotDispatcher: Dispatcher<JumpIfNot>,
    crossinline jumpTableDispatcher: Dispatcher<JumpTable>,
    crossinline jumpOnNullDispatcher: Dispatcher<JumpOnNull>,
    crossinline jumpOnNonNullDispatcher: Dispatcher<JumpOnNonNull>,
    crossinline jumpOnCastDispatcher: Dispatcher<JumpOnCast>,
    crossinline jumpOnCastFailDispatcher: Dispatcher<JumpOnCastFail>,
    crossinline returnFunctionDispatcher: Dispatcher<ReturnFunction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AdminInstruction.JumpInstruction.Jump -> {
            jumpDispatcher(Jump(instruction.offset))
        }
        is AdminInstruction.JumpInstruction.JumpAdjusting -> {
            jumpAdjustingDispatcher(JumpAdjusting(instruction.offset, instruction.adjustment))
        }
        is AdminInstruction.JumpInstruction.JumpIf -> {
            jumpIfDispatcher(JumpIf(instruction.offset, instruction.adjustment))
        }
        is AdminInstruction.JumpInstruction.JumpIfNot -> {
            jumpIfNotDispatcher(JumpIfNot(instruction.offset))
        }
        is AdminInstruction.JumpInstruction.JumpTable -> {
            jumpTableDispatcher(JumpTable(instruction.offsets, instruction.adjustments, instruction.defaultOffset, instruction.defaultAdjustment))
        }
        is AdminInstruction.JumpInstruction.JumpOnNull -> {
            jumpOnNullDispatcher(JumpOnNull(instruction.offset, instruction.adjustment))
        }
        is AdminInstruction.JumpInstruction.JumpOnNonNull -> {
            jumpOnNonNullDispatcher(JumpOnNonNull(instruction.offset, instruction.adjustment))
        }
        is AdminInstruction.JumpInstruction.JumpOnCast -> {
            jumpOnCastDispatcher(JumpOnCast(instruction.offset, instruction.adjustment, instruction.srcReferenceType, instruction.dstReferenceType))
        }
        is AdminInstruction.JumpInstruction.JumpOnCastFail -> {
            jumpOnCastFailDispatcher(JumpOnCastFail(instruction.offset, instruction.adjustment, instruction.srcReferenceType, instruction.dstReferenceType))
        }
        is AdminInstruction.ReturnFunction -> {
            returnFunctionDispatcher(ReturnFunction(instruction.adjustment))
        }
    }
}
