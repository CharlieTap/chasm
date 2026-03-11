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
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddIi) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddIs) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddSi) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddSs) = dispatchInstruction(instruction, ::I64AddExecutor)

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubIi) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubIs) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubSi) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubSs) = dispatchInstruction(instruction, ::I64SubExecutor)

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulIi) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulIs) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulSi) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulSs) = dispatchInstruction(instruction, ::I64MulExecutor)

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSIi) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSIs) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSSi) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSSs) = dispatchInstruction(instruction, ::I64DivSExecutor)

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUIi) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUIs) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUSi) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUSs) = dispatchInstruction(instruction, ::I64DivUExecutor)

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSIi) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSIs) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSSi) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSSs) = dispatchInstruction(instruction, ::I64RemSExecutor)

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUIi) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUIs) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUSi) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUSs) = dispatchInstruction(instruction, ::I64RemUExecutor)

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndIi) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndIs) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndSi) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndSs) = dispatchInstruction(instruction, ::I64AndExecutor)

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrIi) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrIs) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrSi) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrSs) = dispatchInstruction(instruction, ::I64OrExecutor)

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorIi) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorIs) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorSi) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorSs) = dispatchInstruction(instruction, ::I64XorExecutor)

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlIi) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlIs) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlSi) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlSs) = dispatchInstruction(instruction, ::I64ShlExecutor)

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSIi) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSIs) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSSi) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSSs) = dispatchInstruction(instruction, ::I64ShrSExecutor)

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUIi) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUIs) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUSi) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUSs) = dispatchInstruction(instruction, ::I64ShrUExecutor)

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlIi) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlIs) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlSi) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlSs) = dispatchInstruction(instruction, ::I64RotlExecutor)

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrIi) = dispatchInstruction(instruction, ::I64RotrExecutor)

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrIs) = dispatchInstruction(instruction, ::I64RotrExecutor)

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrSi) = dispatchInstruction(instruction, ::I64RotrExecutor)

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrSs) = dispatchInstruction(instruction, ::I64RotrExecutor)
