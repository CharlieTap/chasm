package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64AndExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64DivSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64DivUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64OrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RemSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RemUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RotlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RotrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64ShlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64ShrSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64ShrUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64XorExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64AddDispatcher(instruction: FusedNumericInstruction.I64AddIi) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64AddDispatcher(instruction: FusedNumericInstruction.I64AddIs) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64AddDispatcher(instruction: FusedNumericInstruction.I64AddSi) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64AddDispatcher(instruction: FusedNumericInstruction.I64AddSs) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64SubDispatcher(instruction: FusedNumericInstruction.I64SubIi) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64SubDispatcher(instruction: FusedNumericInstruction.I64SubIs) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64SubDispatcher(instruction: FusedNumericInstruction.I64SubSi) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64SubDispatcher(instruction: FusedNumericInstruction.I64SubSs) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64MulDispatcher(instruction: FusedNumericInstruction.I64MulIi) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64MulDispatcher(instruction: FusedNumericInstruction.I64MulIs) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64MulDispatcher(instruction: FusedNumericInstruction.I64MulSi) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64MulDispatcher(instruction: FusedNumericInstruction.I64MulSs) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64DivSDispatcher(instruction: FusedNumericInstruction.I64DivSIi) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivSDispatcher(instruction: FusedNumericInstruction.I64DivSIs) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivSDispatcher(instruction: FusedNumericInstruction.I64DivSSi) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivSDispatcher(instruction: FusedNumericInstruction.I64DivSSs) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivUDispatcher(instruction: FusedNumericInstruction.I64DivUIi) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64DivUDispatcher(instruction: FusedNumericInstruction.I64DivUIs) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64DivUDispatcher(instruction: FusedNumericInstruction.I64DivUSi) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64DivUDispatcher(instruction: FusedNumericInstruction.I64DivUSs) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64RemSDispatcher(instruction: FusedNumericInstruction.I64RemSIi) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemSDispatcher(instruction: FusedNumericInstruction.I64RemSIs) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemSDispatcher(instruction: FusedNumericInstruction.I64RemSSi) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemSDispatcher(instruction: FusedNumericInstruction.I64RemSSs) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemUDispatcher(instruction: FusedNumericInstruction.I64RemUIi) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64RemUDispatcher(instruction: FusedNumericInstruction.I64RemUIs) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64RemUDispatcher(instruction: FusedNumericInstruction.I64RemUSi) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64RemUDispatcher(instruction: FusedNumericInstruction.I64RemUSs) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64AndDispatcher(instruction: FusedNumericInstruction.I64AndIi) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64AndDispatcher(instruction: FusedNumericInstruction.I64AndIs) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64AndDispatcher(instruction: FusedNumericInstruction.I64AndSi) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64AndDispatcher(instruction: FusedNumericInstruction.I64AndSs) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64OrDispatcher(instruction: FusedNumericInstruction.I64OrIi) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64OrDispatcher(instruction: FusedNumericInstruction.I64OrIs) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64OrDispatcher(instruction: FusedNumericInstruction.I64OrSi) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64OrDispatcher(instruction: FusedNumericInstruction.I64OrSs) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64XorDispatcher(instruction: FusedNumericInstruction.I64XorIi) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64XorDispatcher(instruction: FusedNumericInstruction.I64XorIs) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64XorDispatcher(instruction: FusedNumericInstruction.I64XorSi) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64XorDispatcher(instruction: FusedNumericInstruction.I64XorSs) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64ShlDispatcher(instruction: FusedNumericInstruction.I64ShlIi) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShlDispatcher(instruction: FusedNumericInstruction.I64ShlIs) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShlDispatcher(instruction: FusedNumericInstruction.I64ShlSi) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShlDispatcher(instruction: FusedNumericInstruction.I64ShlSs) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShrSDispatcher(instruction: FusedNumericInstruction.I64ShrSIi) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrSDispatcher(instruction: FusedNumericInstruction.I64ShrSIs) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrSDispatcher(instruction: FusedNumericInstruction.I64ShrSSi) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrSDispatcher(instruction: FusedNumericInstruction.I64ShrSSs) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrUDispatcher(instruction: FusedNumericInstruction.I64ShrUIi) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64ShrUDispatcher(instruction: FusedNumericInstruction.I64ShrUIs) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64ShrUDispatcher(instruction: FusedNumericInstruction.I64ShrUSi) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64ShrUDispatcher(instruction: FusedNumericInstruction.I64ShrUSs) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64RotlDispatcher(instruction: FusedNumericInstruction.I64RotlIi) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotlDispatcher(instruction: FusedNumericInstruction.I64RotlIs) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotlDispatcher(instruction: FusedNumericInstruction.I64RotlSi) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotlDispatcher(instruction: FusedNumericInstruction.I64RotlSs) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotrDispatcher(instruction: FusedNumericInstruction.I64RotrIi) = dispatchInstruction(instruction, ::I64RotrExecutor)

fun I64RotrDispatcher(instruction: FusedNumericInstruction.I64RotrIs) = dispatchInstruction(instruction, ::I64RotrExecutor)

fun I64RotrDispatcher(instruction: FusedNumericInstruction.I64RotrSi) = dispatchInstruction(instruction, ::I64RotrExecutor)

fun I64RotrDispatcher(instruction: FusedNumericInstruction.I64RotrSs) = dispatchInstruction(instruction, ::I64RotrExecutor)
