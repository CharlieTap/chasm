package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64NeExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64EqDispatcher(
    instruction: NumericInstruction.I64EqIi,
) = dispatchInstruction { vstack, context ->
    I64EqExecutor(vstack, context, instruction)
}

fun I64EqDispatcher(
    instruction: NumericInstruction.I64EqIs,
) = dispatchInstruction { vstack, context ->
    I64EqExecutor(vstack, context, instruction)
}

fun I64EqDispatcher(
    instruction: NumericInstruction.I64EqSi,
) = dispatchInstruction { vstack, context ->
    I64EqExecutor(vstack, context, instruction)
}

fun I64EqDispatcher(
    instruction: NumericInstruction.I64EqSs,
) = dispatchInstruction { vstack, context ->
    I64EqExecutor(vstack, context, instruction)
}

fun I64NeDispatcher(
    instruction: NumericInstruction.I64NeIi,
) = dispatchInstruction { vstack, context ->
    I64NeExecutor(vstack, context, instruction)
}

fun I64NeDispatcher(
    instruction: NumericInstruction.I64NeIs,
) = dispatchInstruction { vstack, context ->
    I64NeExecutor(vstack, context, instruction)
}

fun I64NeDispatcher(
    instruction: NumericInstruction.I64NeSi,
) = dispatchInstruction { vstack, context ->
    I64NeExecutor(vstack, context, instruction)
}

fun I64NeDispatcher(
    instruction: NumericInstruction.I64NeSs,
) = dispatchInstruction { vstack, context ->
    I64NeExecutor(vstack, context, instruction)
}

fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtSIi,
) = dispatchInstruction { vstack, context ->
    I64LtSExecutor(vstack, context, instruction)
}

fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtSIs,
) = dispatchInstruction { vstack, context ->
    I64LtSExecutor(vstack, context, instruction)
}

fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtSSi,
) = dispatchInstruction { vstack, context ->
    I64LtSExecutor(vstack, context, instruction)
}

fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtSSs,
) = dispatchInstruction { vstack, context ->
    I64LtSExecutor(vstack, context, instruction)
}

fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtUIi,
) = dispatchInstruction { vstack, context ->
    I64LtUExecutor(vstack, context, instruction)
}

fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtUIs,
) = dispatchInstruction { vstack, context ->
    I64LtUExecutor(vstack, context, instruction)
}

fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtUSi,
) = dispatchInstruction { vstack, context ->
    I64LtUExecutor(vstack, context, instruction)
}

fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtUSs,
) = dispatchInstruction { vstack, context ->
    I64LtUExecutor(vstack, context, instruction)
}

fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtSIi,
) = dispatchInstruction { vstack, context ->
    I64GtSExecutor(vstack, context, instruction)
}

fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtSIs,
) = dispatchInstruction { vstack, context ->
    I64GtSExecutor(vstack, context, instruction)
}

fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtSSi,
) = dispatchInstruction { vstack, context ->
    I64GtSExecutor(vstack, context, instruction)
}

fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtSSs,
) = dispatchInstruction { vstack, context ->
    I64GtSExecutor(vstack, context, instruction)
}

fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtUIi,
) = dispatchInstruction { vstack, context ->
    I64GtUExecutor(vstack, context, instruction)
}

fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtUIs,
) = dispatchInstruction { vstack, context ->
    I64GtUExecutor(vstack, context, instruction)
}

fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtUSi,
) = dispatchInstruction { vstack, context ->
    I64GtUExecutor(vstack, context, instruction)
}

fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtUSs,
) = dispatchInstruction { vstack, context ->
    I64GtUExecutor(vstack, context, instruction)
}

fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeSIi,
) = dispatchInstruction { vstack, context ->
    I64LeSExecutor(vstack, context, instruction)
}

fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeSIs,
) = dispatchInstruction { vstack, context ->
    I64LeSExecutor(vstack, context, instruction)
}

fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeSSi,
) = dispatchInstruction { vstack, context ->
    I64LeSExecutor(vstack, context, instruction)
}

fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeSSs,
) = dispatchInstruction { vstack, context ->
    I64LeSExecutor(vstack, context, instruction)
}

fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeUIi,
) = dispatchInstruction { vstack, context ->
    I64LeUExecutor(vstack, context, instruction)
}

fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeUIs,
) = dispatchInstruction { vstack, context ->
    I64LeUExecutor(vstack, context, instruction)
}

fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeUSi,
) = dispatchInstruction { vstack, context ->
    I64LeUExecutor(vstack, context, instruction)
}

fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeUSs,
) = dispatchInstruction { vstack, context ->
    I64LeUExecutor(vstack, context, instruction)
}

fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeSIi,
) = dispatchInstruction { vstack, context ->
    I64GeSExecutor(vstack, context, instruction)
}

fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeSIs,
) = dispatchInstruction { vstack, context ->
    I64GeSExecutor(vstack, context, instruction)
}

fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeSSi,
) = dispatchInstruction { vstack, context ->
    I64GeSExecutor(vstack, context, instruction)
}

fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeSSs,
) = dispatchInstruction { vstack, context ->
    I64GeSExecutor(vstack, context, instruction)
}

fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeUIi,
) = dispatchInstruction { vstack, context ->
    I64GeUExecutor(vstack, context, instruction)
}

fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeUIs,
) = dispatchInstruction { vstack, context ->
    I64GeUExecutor(vstack, context, instruction)
}

fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeUSi,
) = dispatchInstruction { vstack, context ->
    I64GeUExecutor(vstack, context, instruction)
}

fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeUSs,
) = dispatchInstruction { vstack, context ->
    I64GeUExecutor(vstack, context, instruction)
}
