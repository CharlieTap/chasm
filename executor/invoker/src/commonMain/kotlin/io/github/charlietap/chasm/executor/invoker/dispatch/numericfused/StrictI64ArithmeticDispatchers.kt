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

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddIi) = dispatchInstruction { vstack, context -> I64AddExecutor(vstack, context, instruction) }

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddIs) = dispatchInstruction { vstack, context -> I64AddExecutor(vstack, context, instruction) }

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddSi) = dispatchInstruction { vstack, context -> I64AddExecutor(vstack, context, instruction) }

fun I64AddDispatcher(instruction: NumericSuperInstruction.I64AddSs) = dispatchInstruction { vstack, context -> I64AddExecutor(vstack, context, instruction) }

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubIi) = dispatchInstruction { vstack, context -> I64SubExecutor(vstack, context, instruction) }

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubIs) = dispatchInstruction { vstack, context -> I64SubExecutor(vstack, context, instruction) }

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubSi) = dispatchInstruction { vstack, context -> I64SubExecutor(vstack, context, instruction) }

fun I64SubDispatcher(instruction: NumericSuperInstruction.I64SubSs) = dispatchInstruction { vstack, context -> I64SubExecutor(vstack, context, instruction) }

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulIi) = dispatchInstruction { vstack, context -> I64MulExecutor(vstack, context, instruction) }

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulIs) = dispatchInstruction { vstack, context -> I64MulExecutor(vstack, context, instruction) }

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulSi) = dispatchInstruction { vstack, context -> I64MulExecutor(vstack, context, instruction) }

fun I64MulDispatcher(instruction: NumericSuperInstruction.I64MulSs) = dispatchInstruction { vstack, context -> I64MulExecutor(vstack, context, instruction) }

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSIi) = dispatchInstruction { vstack, context -> I64DivSExecutor(vstack, context, instruction) }

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSIs) = dispatchInstruction { vstack, context -> I64DivSExecutor(vstack, context, instruction) }

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSSi) = dispatchInstruction { vstack, context -> I64DivSExecutor(vstack, context, instruction) }

fun I64DivSDispatcher(instruction: NumericSuperInstruction.I64DivSSs) = dispatchInstruction { vstack, context -> I64DivSExecutor(vstack, context, instruction) }

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUIi) = dispatchInstruction { vstack, context -> I64DivUExecutor(vstack, context, instruction) }

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUIs) = dispatchInstruction { vstack, context -> I64DivUExecutor(vstack, context, instruction) }

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUSi) = dispatchInstruction { vstack, context -> I64DivUExecutor(vstack, context, instruction) }

fun I64DivUDispatcher(instruction: NumericSuperInstruction.I64DivUSs) = dispatchInstruction { vstack, context -> I64DivUExecutor(vstack, context, instruction) }

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSIi) = dispatchInstruction { vstack, context -> I64RemSExecutor(vstack, context, instruction) }

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSIs) = dispatchInstruction { vstack, context -> I64RemSExecutor(vstack, context, instruction) }

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSSi) = dispatchInstruction { vstack, context -> I64RemSExecutor(vstack, context, instruction) }

fun I64RemSDispatcher(instruction: NumericSuperInstruction.I64RemSSs) = dispatchInstruction { vstack, context -> I64RemSExecutor(vstack, context, instruction) }

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUIi) = dispatchInstruction { vstack, context -> I64RemUExecutor(vstack, context, instruction) }

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUIs) = dispatchInstruction { vstack, context -> I64RemUExecutor(vstack, context, instruction) }

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUSi) = dispatchInstruction { vstack, context -> I64RemUExecutor(vstack, context, instruction) }

fun I64RemUDispatcher(instruction: NumericSuperInstruction.I64RemUSs) = dispatchInstruction { vstack, context -> I64RemUExecutor(vstack, context, instruction) }

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndIi) = dispatchInstruction { vstack, context -> I64AndExecutor(vstack, context, instruction) }

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndIs) = dispatchInstruction { vstack, context -> I64AndExecutor(vstack, context, instruction) }

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndSi) = dispatchInstruction { vstack, context -> I64AndExecutor(vstack, context, instruction) }

fun I64AndDispatcher(instruction: NumericSuperInstruction.I64AndSs) = dispatchInstruction { vstack, context -> I64AndExecutor(vstack, context, instruction) }

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrIi) = dispatchInstruction { vstack, context -> I64OrExecutor(vstack, context, instruction) }

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrIs) = dispatchInstruction { vstack, context -> I64OrExecutor(vstack, context, instruction) }

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrSi) = dispatchInstruction { vstack, context -> I64OrExecutor(vstack, context, instruction) }

fun I64OrDispatcher(instruction: NumericSuperInstruction.I64OrSs) = dispatchInstruction { vstack, context -> I64OrExecutor(vstack, context, instruction) }

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorIi) = dispatchInstruction { vstack, context -> I64XorExecutor(vstack, context, instruction) }

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorIs) = dispatchInstruction { vstack, context -> I64XorExecutor(vstack, context, instruction) }

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorSi) = dispatchInstruction { vstack, context -> I64XorExecutor(vstack, context, instruction) }

fun I64XorDispatcher(instruction: NumericSuperInstruction.I64XorSs) = dispatchInstruction { vstack, context -> I64XorExecutor(vstack, context, instruction) }

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlIi) = dispatchInstruction { vstack, context -> I64ShlExecutor(vstack, context, instruction) }

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlIs) = dispatchInstruction { vstack, context -> I64ShlExecutor(vstack, context, instruction) }

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlSi) = dispatchInstruction { vstack, context -> I64ShlExecutor(vstack, context, instruction) }

fun I64ShlDispatcher(instruction: NumericSuperInstruction.I64ShlSs) = dispatchInstruction { vstack, context -> I64ShlExecutor(vstack, context, instruction) }

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSIi) = dispatchInstruction { vstack, context -> I64ShrSExecutor(vstack, context, instruction) }

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSIs) = dispatchInstruction { vstack, context -> I64ShrSExecutor(vstack, context, instruction) }

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSSi) = dispatchInstruction { vstack, context -> I64ShrSExecutor(vstack, context, instruction) }

fun I64ShrSDispatcher(instruction: NumericSuperInstruction.I64ShrSSs) = dispatchInstruction { vstack, context -> I64ShrSExecutor(vstack, context, instruction) }

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUIi) = dispatchInstruction { vstack, context -> I64ShrUExecutor(vstack, context, instruction) }

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUIs) = dispatchInstruction { vstack, context -> I64ShrUExecutor(vstack, context, instruction) }

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUSi) = dispatchInstruction { vstack, context -> I64ShrUExecutor(vstack, context, instruction) }

fun I64ShrUDispatcher(instruction: NumericSuperInstruction.I64ShrUSs) = dispatchInstruction { vstack, context -> I64ShrUExecutor(vstack, context, instruction) }

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlIi) = dispatchInstruction { vstack, context -> I64RotlExecutor(vstack, context, instruction) }

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlIs) = dispatchInstruction { vstack, context -> I64RotlExecutor(vstack, context, instruction) }

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlSi) = dispatchInstruction { vstack, context -> I64RotlExecutor(vstack, context, instruction) }

fun I64RotlDispatcher(instruction: NumericSuperInstruction.I64RotlSs) = dispatchInstruction { vstack, context -> I64RotlExecutor(vstack, context, instruction) }

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrIi) = dispatchInstruction { vstack, context -> I64RotrExecutor(vstack, context, instruction) }

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrIs) = dispatchInstruction { vstack, context -> I64RotrExecutor(vstack, context, instruction) }

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrSi) = dispatchInstruction { vstack, context -> I64RotrExecutor(vstack, context, instruction) }

fun I64RotrDispatcher(instruction: NumericSuperInstruction.I64RotrSs) = dispatchInstruction { vstack, context -> I64RotrExecutor(vstack, context, instruction) }
