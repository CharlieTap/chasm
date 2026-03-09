package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32AndExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32DivSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32DivUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32OrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RemSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RemUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RotlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RotrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShrSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShrUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32XorExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32AddDispatcher(instruction: FusedNumericInstruction.I32AddIi) = dispatchInstruction(instruction, ::I32AddExecutor)

fun I32AddDispatcher(instruction: FusedNumericInstruction.I32AddIs) = dispatchInstruction(instruction, ::I32AddExecutor)

fun I32AddDispatcher(instruction: FusedNumericInstruction.I32AddSi) = dispatchInstruction(instruction, ::I32AddExecutor)

fun I32AddDispatcher(instruction: FusedNumericInstruction.I32AddSs) = dispatchInstruction(instruction, ::I32AddExecutor)

fun I32SubDispatcher(instruction: FusedNumericInstruction.I32SubIi) = dispatchInstruction(instruction, ::I32SubExecutor)

fun I32SubDispatcher(instruction: FusedNumericInstruction.I32SubIs) = dispatchInstruction(instruction, ::I32SubExecutor)

fun I32SubDispatcher(instruction: FusedNumericInstruction.I32SubSi) = dispatchInstruction(instruction, ::I32SubExecutor)

fun I32SubDispatcher(instruction: FusedNumericInstruction.I32SubSs) = dispatchInstruction(instruction, ::I32SubExecutor)

fun I32MulDispatcher(instruction: FusedNumericInstruction.I32MulIi) = dispatchInstruction(instruction, ::I32MulExecutor)

fun I32MulDispatcher(instruction: FusedNumericInstruction.I32MulIs) = dispatchInstruction(instruction, ::I32MulExecutor)

fun I32MulDispatcher(instruction: FusedNumericInstruction.I32MulSi) = dispatchInstruction(instruction, ::I32MulExecutor)

fun I32MulDispatcher(instruction: FusedNumericInstruction.I32MulSs) = dispatchInstruction(instruction, ::I32MulExecutor)

fun I32DivSDispatcher(instruction: FusedNumericInstruction.I32DivSIi) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivSDispatcher(instruction: FusedNumericInstruction.I32DivSIs) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivSDispatcher(instruction: FusedNumericInstruction.I32DivSSi) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivSDispatcher(instruction: FusedNumericInstruction.I32DivSSs) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivUDispatcher(instruction: FusedNumericInstruction.I32DivUIi) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32DivUDispatcher(instruction: FusedNumericInstruction.I32DivUIs) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32DivUDispatcher(instruction: FusedNumericInstruction.I32DivUSi) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32DivUDispatcher(instruction: FusedNumericInstruction.I32DivUSs) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32RemSDispatcher(instruction: FusedNumericInstruction.I32RemSIi) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemSDispatcher(instruction: FusedNumericInstruction.I32RemSIs) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemSDispatcher(instruction: FusedNumericInstruction.I32RemSSi) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemSDispatcher(instruction: FusedNumericInstruction.I32RemSSs) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemUDispatcher(instruction: FusedNumericInstruction.I32RemUIi) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32RemUDispatcher(instruction: FusedNumericInstruction.I32RemUIs) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32RemUDispatcher(instruction: FusedNumericInstruction.I32RemUSi) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32RemUDispatcher(instruction: FusedNumericInstruction.I32RemUSs) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32AndDispatcher(instruction: FusedNumericInstruction.I32AndIi) = dispatchInstruction(instruction, ::I32AndExecutor)

fun I32AndDispatcher(instruction: FusedNumericInstruction.I32AndIs) = dispatchInstruction(instruction, ::I32AndExecutor)

fun I32AndDispatcher(instruction: FusedNumericInstruction.I32AndSi) = dispatchInstruction(instruction, ::I32AndExecutor)

fun I32AndDispatcher(instruction: FusedNumericInstruction.I32AndSs) = dispatchInstruction(instruction, ::I32AndExecutor)

fun I32OrDispatcher(instruction: FusedNumericInstruction.I32OrIi) = dispatchInstruction(instruction, ::I32OrExecutor)

fun I32OrDispatcher(instruction: FusedNumericInstruction.I32OrIs) = dispatchInstruction(instruction, ::I32OrExecutor)

fun I32OrDispatcher(instruction: FusedNumericInstruction.I32OrSi) = dispatchInstruction(instruction, ::I32OrExecutor)

fun I32OrDispatcher(instruction: FusedNumericInstruction.I32OrSs) = dispatchInstruction(instruction, ::I32OrExecutor)

fun I32XorDispatcher(instruction: FusedNumericInstruction.I32XorIi) = dispatchInstruction(instruction, ::I32XorExecutor)

fun I32XorDispatcher(instruction: FusedNumericInstruction.I32XorIs) = dispatchInstruction(instruction, ::I32XorExecutor)

fun I32XorDispatcher(instruction: FusedNumericInstruction.I32XorSi) = dispatchInstruction(instruction, ::I32XorExecutor)

fun I32XorDispatcher(instruction: FusedNumericInstruction.I32XorSs) = dispatchInstruction(instruction, ::I32XorExecutor)

fun I32ShlDispatcher(instruction: FusedNumericInstruction.I32ShlIi) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShlDispatcher(instruction: FusedNumericInstruction.I32ShlIs) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShlDispatcher(instruction: FusedNumericInstruction.I32ShlSi) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShlDispatcher(instruction: FusedNumericInstruction.I32ShlSs) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShrSDispatcher(instruction: FusedNumericInstruction.I32ShrSIi) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrSDispatcher(instruction: FusedNumericInstruction.I32ShrSIs) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrSDispatcher(instruction: FusedNumericInstruction.I32ShrSSi) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrSDispatcher(instruction: FusedNumericInstruction.I32ShrSSs) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrUDispatcher(instruction: FusedNumericInstruction.I32ShrUIi) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32ShrUDispatcher(instruction: FusedNumericInstruction.I32ShrUIs) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32ShrUDispatcher(instruction: FusedNumericInstruction.I32ShrUSi) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32ShrUDispatcher(instruction: FusedNumericInstruction.I32ShrUSs) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32RotlDispatcher(instruction: FusedNumericInstruction.I32RotlIi) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotlDispatcher(instruction: FusedNumericInstruction.I32RotlIs) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotlDispatcher(instruction: FusedNumericInstruction.I32RotlSi) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotlDispatcher(instruction: FusedNumericInstruction.I32RotlSs) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotrDispatcher(instruction: FusedNumericInstruction.I32RotrIi) = dispatchInstruction(instruction, ::I32RotrExecutor)

fun I32RotrDispatcher(instruction: FusedNumericInstruction.I32RotrIs) = dispatchInstruction(instruction, ::I32RotrExecutor)

fun I32RotrDispatcher(instruction: FusedNumericInstruction.I32RotrSi) = dispatchInstruction(instruction, ::I32RotrExecutor)

fun I32RotrDispatcher(instruction: FusedNumericInstruction.I32RotrSs) = dispatchInstruction(instruction, ::I32RotrExecutor)
