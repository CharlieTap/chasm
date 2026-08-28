package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64AndExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64DivSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64DivUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64OrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RemSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RemUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RotlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RotrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShrSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShrUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64XorExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64AddDispatcher(
    instruction: NumericInstruction.I64AddIi,
) = dispatchInstruction { vstack, context ->
    I64AddExecutor(vstack, context, instruction)
}

fun I64AddDispatcher(
    instruction: NumericInstruction.I64AddIs,
) = dispatchInstruction { vstack, context ->
    I64AddExecutor(vstack, context, instruction)
}

fun I64AddDispatcher(
    instruction: NumericInstruction.I64AddSi,
) = dispatchInstruction { vstack, context ->
    I64AddExecutor(vstack, context, instruction)
}

fun I64AddDispatcher(
    instruction: NumericInstruction.I64AddSs,
) = dispatchInstruction { vstack, context ->
    I64AddExecutor(vstack, context, instruction)
}

fun I64SubDispatcher(
    instruction: NumericInstruction.I64SubIi,
) = dispatchInstruction { vstack, context ->
    I64SubExecutor(vstack, context, instruction)
}

fun I64SubDispatcher(
    instruction: NumericInstruction.I64SubIs,
) = dispatchInstruction { vstack, context ->
    I64SubExecutor(vstack, context, instruction)
}

fun I64SubDispatcher(
    instruction: NumericInstruction.I64SubSi,
) = dispatchInstruction { vstack, context ->
    I64SubExecutor(vstack, context, instruction)
}

fun I64SubDispatcher(
    instruction: NumericInstruction.I64SubSs,
) = dispatchInstruction { vstack, context ->
    I64SubExecutor(vstack, context, instruction)
}

fun I64MulDispatcher(
    instruction: NumericInstruction.I64MulIi,
) = dispatchInstruction { vstack, context ->
    I64MulExecutor(vstack, context, instruction)
}

fun I64MulDispatcher(
    instruction: NumericInstruction.I64MulIs,
) = dispatchInstruction { vstack, context ->
    I64MulExecutor(vstack, context, instruction)
}

fun I64MulDispatcher(
    instruction: NumericInstruction.I64MulSi,
) = dispatchInstruction { vstack, context ->
    I64MulExecutor(vstack, context, instruction)
}

fun I64MulDispatcher(
    instruction: NumericInstruction.I64MulSs,
) = dispatchInstruction { vstack, context ->
    I64MulExecutor(vstack, context, instruction)
}

fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivSIi,
) = dispatchInstruction { vstack, context ->
    I64DivSExecutor(vstack, context, instruction)
}

fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivSIs,
) = dispatchInstruction { vstack, context ->
    I64DivSExecutor(vstack, context, instruction)
}

fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivSSi,
) = dispatchInstruction { vstack, context ->
    I64DivSExecutor(vstack, context, instruction)
}

fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivSSs,
) = dispatchInstruction { vstack, context ->
    I64DivSExecutor(vstack, context, instruction)
}

fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivUIi,
) = dispatchInstruction { vstack, context ->
    I64DivUExecutor(vstack, context, instruction)
}

fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivUIs,
) = dispatchInstruction { vstack, context ->
    I64DivUExecutor(vstack, context, instruction)
}

fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivUSi,
) = dispatchInstruction { vstack, context ->
    I64DivUExecutor(vstack, context, instruction)
}

fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivUSs,
) = dispatchInstruction { vstack, context ->
    I64DivUExecutor(vstack, context, instruction)
}

fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemSIi,
) = dispatchInstruction { vstack, context ->
    I64RemSExecutor(vstack, context, instruction)
}

fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemSIs,
) = dispatchInstruction { vstack, context ->
    I64RemSExecutor(vstack, context, instruction)
}

fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemSSi,
) = dispatchInstruction { vstack, context ->
    I64RemSExecutor(vstack, context, instruction)
}

fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemSSs,
) = dispatchInstruction { vstack, context ->
    I64RemSExecutor(vstack, context, instruction)
}

fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemUIi,
) = dispatchInstruction { vstack, context ->
    I64RemUExecutor(vstack, context, instruction)
}

fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemUIs,
) = dispatchInstruction { vstack, context ->
    I64RemUExecutor(vstack, context, instruction)
}

fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemUSi,
) = dispatchInstruction { vstack, context ->
    I64RemUExecutor(vstack, context, instruction)
}

fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemUSs,
) = dispatchInstruction { vstack, context ->
    I64RemUExecutor(vstack, context, instruction)
}

fun I64AndDispatcher(
    instruction: NumericInstruction.I64AndIi,
) = dispatchInstruction { vstack, context ->
    I64AndExecutor(vstack, context, instruction)
}

fun I64AndDispatcher(
    instruction: NumericInstruction.I64AndIs,
) = dispatchInstruction { vstack, context ->
    I64AndExecutor(vstack, context, instruction)
}

fun I64AndDispatcher(
    instruction: NumericInstruction.I64AndSi,
) = dispatchInstruction { vstack, context ->
    I64AndExecutor(vstack, context, instruction)
}

fun I64AndDispatcher(
    instruction: NumericInstruction.I64AndSs,
) = dispatchInstruction { vstack, context ->
    I64AndExecutor(vstack, context, instruction)
}

fun I64OrDispatcher(
    instruction: NumericInstruction.I64OrIi,
) = dispatchInstruction { vstack, context ->
    I64OrExecutor(vstack, context, instruction)
}

fun I64OrDispatcher(
    instruction: NumericInstruction.I64OrIs,
) = dispatchInstruction { vstack, context ->
    I64OrExecutor(vstack, context, instruction)
}

fun I64OrDispatcher(
    instruction: NumericInstruction.I64OrSi,
) = dispatchInstruction { vstack, context ->
    I64OrExecutor(vstack, context, instruction)
}

fun I64OrDispatcher(
    instruction: NumericInstruction.I64OrSs,
) = dispatchInstruction { vstack, context ->
    I64OrExecutor(vstack, context, instruction)
}

fun I64XorDispatcher(
    instruction: NumericInstruction.I64XorIi,
) = dispatchInstruction { vstack, context ->
    I64XorExecutor(vstack, context, instruction)
}

fun I64XorDispatcher(
    instruction: NumericInstruction.I64XorIs,
) = dispatchInstruction { vstack, context ->
    I64XorExecutor(vstack, context, instruction)
}

fun I64XorDispatcher(
    instruction: NumericInstruction.I64XorSi,
) = dispatchInstruction { vstack, context ->
    I64XorExecutor(vstack, context, instruction)
}

fun I64XorDispatcher(
    instruction: NumericInstruction.I64XorSs,
) = dispatchInstruction { vstack, context ->
    I64XorExecutor(vstack, context, instruction)
}

fun I64ShlDispatcher(
    instruction: NumericInstruction.I64ShlIi,
) = dispatchInstruction { vstack, context ->
    I64ShlExecutor(vstack, context, instruction)
}

fun I64ShlDispatcher(
    instruction: NumericInstruction.I64ShlIs,
) = dispatchInstruction { vstack, context ->
    I64ShlExecutor(vstack, context, instruction)
}

fun I64ShlDispatcher(
    instruction: NumericInstruction.I64ShlSi,
) = dispatchInstruction { vstack, context ->
    I64ShlExecutor(vstack, context, instruction)
}

fun I64ShlDispatcher(
    instruction: NumericInstruction.I64ShlSs,
) = dispatchInstruction { vstack, context ->
    I64ShlExecutor(vstack, context, instruction)
}

fun I64ShrSDispatcher(
    instruction: NumericInstruction.I64ShrSIi,
) = dispatchInstruction { vstack, context ->
    I64ShrSExecutor(vstack, context, instruction)
}

fun I64ShrSDispatcher(
    instruction: NumericInstruction.I64ShrSIs,
) = dispatchInstruction { vstack, context ->
    I64ShrSExecutor(vstack, context, instruction)
}

fun I64ShrSDispatcher(
    instruction: NumericInstruction.I64ShrSSi,
) = dispatchInstruction { vstack, context ->
    I64ShrSExecutor(vstack, context, instruction)
}

fun I64ShrSDispatcher(
    instruction: NumericInstruction.I64ShrSSs,
) = dispatchInstruction { vstack, context ->
    I64ShrSExecutor(vstack, context, instruction)
}

fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrUIi,
) = dispatchInstruction { vstack, context ->
    I64ShrUExecutor(vstack, context, instruction)
}

fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrUIs,
) = dispatchInstruction { vstack, context ->
    I64ShrUExecutor(vstack, context, instruction)
}

fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrUSi,
) = dispatchInstruction { vstack, context ->
    I64ShrUExecutor(vstack, context, instruction)
}

fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrUSs,
) = dispatchInstruction { vstack, context ->
    I64ShrUExecutor(vstack, context, instruction)
}

fun I64RotlDispatcher(
    instruction: NumericInstruction.I64RotlIi,
) = dispatchInstruction { vstack, context ->
    I64RotlExecutor(vstack, context, instruction)
}

fun I64RotlDispatcher(
    instruction: NumericInstruction.I64RotlIs,
) = dispatchInstruction { vstack, context ->
    I64RotlExecutor(vstack, context, instruction)
}

fun I64RotlDispatcher(
    instruction: NumericInstruction.I64RotlSi,
) = dispatchInstruction { vstack, context ->
    I64RotlExecutor(vstack, context, instruction)
}

fun I64RotlDispatcher(
    instruction: NumericInstruction.I64RotlSs,
) = dispatchInstruction { vstack, context ->
    I64RotlExecutor(vstack, context, instruction)
}

fun I64RotrDispatcher(
    instruction: NumericInstruction.I64RotrIi,
) = dispatchInstruction { vstack, context ->
    I64RotrExecutor(vstack, context, instruction)
}

fun I64RotrDispatcher(
    instruction: NumericInstruction.I64RotrIs,
) = dispatchInstruction { vstack, context ->
    I64RotrExecutor(vstack, context, instruction)
}

fun I64RotrDispatcher(
    instruction: NumericInstruction.I64RotrSi,
) = dispatchInstruction { vstack, context ->
    I64RotrExecutor(vstack, context, instruction)
}

fun I64RotrDispatcher(
    instruction: NumericInstruction.I64RotrSs,
) = dispatchInstruction { vstack, context ->
    I64RotrExecutor(vstack, context, instruction)
}
