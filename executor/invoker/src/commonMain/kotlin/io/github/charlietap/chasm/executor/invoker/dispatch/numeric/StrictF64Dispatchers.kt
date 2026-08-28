package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64CopysignExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64DivExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MaxExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MinExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64PromoteF32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64NeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64AbsExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64CeilExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64FloorExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NearestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NegExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64SqrtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64TruncExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64AddDispatcher(
    instruction: NumericInstruction.F64AddIi,
) = dispatchInstruction { vstack, context ->
    F64AddExecutor(vstack, context, instruction)
}

fun F64AddDispatcher(
    instruction: NumericInstruction.F64AddIs,
) = dispatchInstruction { vstack, context ->
    F64AddExecutor(vstack, context, instruction)
}

fun F64AddDispatcher(
    instruction: NumericInstruction.F64AddSi,
) = dispatchInstruction { vstack, context ->
    F64AddExecutor(vstack, context, instruction)
}

fun F64AddDispatcher(
    instruction: NumericInstruction.F64AddSs,
) = dispatchInstruction { vstack, context ->
    F64AddExecutor(vstack, context, instruction)
}

fun F64SubDispatcher(
    instruction: NumericInstruction.F64SubIi,
) = dispatchInstruction { vstack, context ->
    F64SubExecutor(vstack, context, instruction)
}

fun F64SubDispatcher(
    instruction: NumericInstruction.F64SubIs,
) = dispatchInstruction { vstack, context ->
    F64SubExecutor(vstack, context, instruction)
}

fun F64SubDispatcher(
    instruction: NumericInstruction.F64SubSi,
) = dispatchInstruction { vstack, context ->
    F64SubExecutor(vstack, context, instruction)
}

fun F64SubDispatcher(
    instruction: NumericInstruction.F64SubSs,
) = dispatchInstruction { vstack, context ->
    F64SubExecutor(vstack, context, instruction)
}

fun F64MulDispatcher(
    instruction: NumericInstruction.F64MulIi,
) = dispatchInstruction { vstack, context ->
    F64MulExecutor(vstack, context, instruction)
}

fun F64MulDispatcher(
    instruction: NumericInstruction.F64MulIs,
) = dispatchInstruction { vstack, context ->
    F64MulExecutor(vstack, context, instruction)
}

fun F64MulDispatcher(
    instruction: NumericInstruction.F64MulSi,
) = dispatchInstruction { vstack, context ->
    F64MulExecutor(vstack, context, instruction)
}

fun F64MulDispatcher(
    instruction: NumericInstruction.F64MulSs,
) = dispatchInstruction { vstack, context ->
    F64MulExecutor(vstack, context, instruction)
}

fun F64DivDispatcher(
    instruction: NumericInstruction.F64DivIi,
) = dispatchInstruction { vstack, context ->
    F64DivExecutor(vstack, context, instruction)
}

fun F64DivDispatcher(
    instruction: NumericInstruction.F64DivIs,
) = dispatchInstruction { vstack, context ->
    F64DivExecutor(vstack, context, instruction)
}

fun F64DivDispatcher(
    instruction: NumericInstruction.F64DivSi,
) = dispatchInstruction { vstack, context ->
    F64DivExecutor(vstack, context, instruction)
}

fun F64DivDispatcher(
    instruction: NumericInstruction.F64DivSs,
) = dispatchInstruction { vstack, context ->
    F64DivExecutor(vstack, context, instruction)
}

fun F64MinDispatcher(
    instruction: NumericInstruction.F64MinIi,
) = dispatchInstruction { vstack, context ->
    F64MinExecutor(vstack, context, instruction)
}

fun F64MinDispatcher(
    instruction: NumericInstruction.F64MinIs,
) = dispatchInstruction { vstack, context ->
    F64MinExecutor(vstack, context, instruction)
}

fun F64MinDispatcher(
    instruction: NumericInstruction.F64MinSi,
) = dispatchInstruction { vstack, context ->
    F64MinExecutor(vstack, context, instruction)
}

fun F64MinDispatcher(
    instruction: NumericInstruction.F64MinSs,
) = dispatchInstruction { vstack, context ->
    F64MinExecutor(vstack, context, instruction)
}

fun F64MaxDispatcher(
    instruction: NumericInstruction.F64MaxIi,
) = dispatchInstruction { vstack, context ->
    F64MaxExecutor(vstack, context, instruction)
}

fun F64MaxDispatcher(
    instruction: NumericInstruction.F64MaxIs,
) = dispatchInstruction { vstack, context ->
    F64MaxExecutor(vstack, context, instruction)
}

fun F64MaxDispatcher(
    instruction: NumericInstruction.F64MaxSi,
) = dispatchInstruction { vstack, context ->
    F64MaxExecutor(vstack, context, instruction)
}

fun F64MaxDispatcher(
    instruction: NumericInstruction.F64MaxSs,
) = dispatchInstruction { vstack, context ->
    F64MaxExecutor(vstack, context, instruction)
}

fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64CopysignIi,
) = dispatchInstruction { vstack, context ->
    F64CopysignExecutor(vstack, context, instruction)
}

fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64CopysignIs,
) = dispatchInstruction { vstack, context ->
    F64CopysignExecutor(vstack, context, instruction)
}

fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64CopysignSi,
) = dispatchInstruction { vstack, context ->
    F64CopysignExecutor(vstack, context, instruction)
}

fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64CopysignSs,
) = dispatchInstruction { vstack, context ->
    F64CopysignExecutor(vstack, context, instruction)
}

fun F64AbsDispatcher(
    instruction: NumericInstruction.F64AbsI,
) = dispatchInstruction { vstack, context ->
    F64AbsExecutor(vstack, context, instruction)
}

fun F64AbsDispatcher(
    instruction: NumericInstruction.F64AbsS,
) = dispatchInstruction { vstack, context ->
    F64AbsExecutor(vstack, context, instruction)
}

fun F64NegDispatcher(
    instruction: NumericInstruction.F64NegI,
) = dispatchInstruction { vstack, context ->
    F64NegExecutor(vstack, context, instruction)
}

fun F64NegDispatcher(
    instruction: NumericInstruction.F64NegS,
) = dispatchInstruction { vstack, context ->
    F64NegExecutor(vstack, context, instruction)
}

fun F64CeilDispatcher(
    instruction: NumericInstruction.F64CeilI,
) = dispatchInstruction { vstack, context ->
    F64CeilExecutor(vstack, context, instruction)
}

fun F64CeilDispatcher(
    instruction: NumericInstruction.F64CeilS,
) = dispatchInstruction { vstack, context ->
    F64CeilExecutor(vstack, context, instruction)
}

fun F64FloorDispatcher(
    instruction: NumericInstruction.F64FloorI,
) = dispatchInstruction { vstack, context ->
    F64FloorExecutor(vstack, context, instruction)
}

fun F64FloorDispatcher(
    instruction: NumericInstruction.F64FloorS,
) = dispatchInstruction { vstack, context ->
    F64FloorExecutor(vstack, context, instruction)
}

fun F64TruncDispatcher(
    instruction: NumericInstruction.F64TruncI,
) = dispatchInstruction { vstack, context ->
    F64TruncExecutor(vstack, context, instruction)
}

fun F64TruncDispatcher(
    instruction: NumericInstruction.F64TruncS,
) = dispatchInstruction { vstack, context ->
    F64TruncExecutor(vstack, context, instruction)
}

fun F64NearestDispatcher(
    instruction: NumericInstruction.F64NearestI,
) = dispatchInstruction { vstack, context ->
    F64NearestExecutor(vstack, context, instruction)
}

fun F64NearestDispatcher(
    instruction: NumericInstruction.F64NearestS,
) = dispatchInstruction { vstack, context ->
    F64NearestExecutor(vstack, context, instruction)
}

fun F64SqrtDispatcher(
    instruction: NumericInstruction.F64SqrtI,
) = dispatchInstruction { vstack, context ->
    F64SqrtExecutor(vstack, context, instruction)
}

fun F64SqrtDispatcher(
    instruction: NumericInstruction.F64SqrtS,
) = dispatchInstruction { vstack, context ->
    F64SqrtExecutor(vstack, context, instruction)
}

fun F64EqDispatcher(
    instruction: NumericInstruction.F64EqIi,
) = dispatchInstruction { vstack, context ->
    F64EqExecutor(vstack, context, instruction)
}

fun F64EqDispatcher(
    instruction: NumericInstruction.F64EqIs,
) = dispatchInstruction { vstack, context ->
    F64EqExecutor(vstack, context, instruction)
}

fun F64EqDispatcher(
    instruction: NumericInstruction.F64EqSi,
) = dispatchInstruction { vstack, context ->
    F64EqExecutor(vstack, context, instruction)
}

fun F64EqDispatcher(
    instruction: NumericInstruction.F64EqSs,
) = dispatchInstruction { vstack, context ->
    F64EqExecutor(vstack, context, instruction)
}

fun F64NeDispatcher(
    instruction: NumericInstruction.F64NeIi,
) = dispatchInstruction { vstack, context ->
    F64NeExecutor(vstack, context, instruction)
}

fun F64NeDispatcher(
    instruction: NumericInstruction.F64NeIs,
) = dispatchInstruction { vstack, context ->
    F64NeExecutor(vstack, context, instruction)
}

fun F64NeDispatcher(
    instruction: NumericInstruction.F64NeSi,
) = dispatchInstruction { vstack, context ->
    F64NeExecutor(vstack, context, instruction)
}

fun F64NeDispatcher(
    instruction: NumericInstruction.F64NeSs,
) = dispatchInstruction { vstack, context ->
    F64NeExecutor(vstack, context, instruction)
}

fun F64LtDispatcher(
    instruction: NumericInstruction.F64LtIi,
) = dispatchInstruction { vstack, context ->
    F64LtExecutor(vstack, context, instruction)
}

fun F64LtDispatcher(
    instruction: NumericInstruction.F64LtIs,
) = dispatchInstruction { vstack, context ->
    F64LtExecutor(vstack, context, instruction)
}

fun F64LtDispatcher(
    instruction: NumericInstruction.F64LtSi,
) = dispatchInstruction { vstack, context ->
    F64LtExecutor(vstack, context, instruction)
}

fun F64LtDispatcher(
    instruction: NumericInstruction.F64LtSs,
) = dispatchInstruction { vstack, context ->
    F64LtExecutor(vstack, context, instruction)
}

fun F64GtDispatcher(
    instruction: NumericInstruction.F64GtIi,
) = dispatchInstruction { vstack, context ->
    F64GtExecutor(vstack, context, instruction)
}

fun F64GtDispatcher(
    instruction: NumericInstruction.F64GtIs,
) = dispatchInstruction { vstack, context ->
    F64GtExecutor(vstack, context, instruction)
}

fun F64GtDispatcher(
    instruction: NumericInstruction.F64GtSi,
) = dispatchInstruction { vstack, context ->
    F64GtExecutor(vstack, context, instruction)
}

fun F64GtDispatcher(
    instruction: NumericInstruction.F64GtSs,
) = dispatchInstruction { vstack, context ->
    F64GtExecutor(vstack, context, instruction)
}

fun F64LeDispatcher(
    instruction: NumericInstruction.F64LeIi,
) = dispatchInstruction { vstack, context ->
    F64LeExecutor(vstack, context, instruction)
}

fun F64LeDispatcher(
    instruction: NumericInstruction.F64LeIs,
) = dispatchInstruction { vstack, context ->
    F64LeExecutor(vstack, context, instruction)
}

fun F64LeDispatcher(
    instruction: NumericInstruction.F64LeSi,
) = dispatchInstruction { vstack, context ->
    F64LeExecutor(vstack, context, instruction)
}

fun F64LeDispatcher(
    instruction: NumericInstruction.F64LeSs,
) = dispatchInstruction { vstack, context ->
    F64LeExecutor(vstack, context, instruction)
}

fun F64GeDispatcher(
    instruction: NumericInstruction.F64GeIi,
) = dispatchInstruction { vstack, context ->
    F64GeExecutor(vstack, context, instruction)
}

fun F64GeDispatcher(
    instruction: NumericInstruction.F64GeIs,
) = dispatchInstruction { vstack, context ->
    F64GeExecutor(vstack, context, instruction)
}

fun F64GeDispatcher(
    instruction: NumericInstruction.F64GeSi,
) = dispatchInstruction { vstack, context ->
    F64GeExecutor(vstack, context, instruction)
}

fun F64GeDispatcher(
    instruction: NumericInstruction.F64GeSs,
) = dispatchInstruction { vstack, context ->
    F64GeExecutor(vstack, context, instruction)
}

fun F64ConvertI32SDispatcher(
    instruction: NumericInstruction.F64ConvertI32SI,
) = dispatchInstruction { vstack, context ->
    F64ConvertI32SExecutor(vstack, context, instruction)
}

fun F64ConvertI32SDispatcher(
    instruction: NumericInstruction.F64ConvertI32SS,
) = dispatchInstruction { vstack, context ->
    F64ConvertI32SExecutor(vstack, context, instruction)
}

fun F64ConvertI32UDispatcher(
    instruction: NumericInstruction.F64ConvertI32UI,
) = dispatchInstruction { vstack, context ->
    F64ConvertI32UExecutor(vstack, context, instruction)
}

fun F64ConvertI32UDispatcher(
    instruction: NumericInstruction.F64ConvertI32US,
) = dispatchInstruction { vstack, context ->
    F64ConvertI32UExecutor(vstack, context, instruction)
}

fun F64ConvertI64SDispatcher(
    instruction: NumericInstruction.F64ConvertI64SI,
) = dispatchInstruction { vstack, context ->
    F64ConvertI64SExecutor(vstack, context, instruction)
}

fun F64ConvertI64SDispatcher(
    instruction: NumericInstruction.F64ConvertI64SS,
) = dispatchInstruction { vstack, context ->
    F64ConvertI64SExecutor(vstack, context, instruction)
}

fun F64ConvertI64UDispatcher(
    instruction: NumericInstruction.F64ConvertI64UI,
) = dispatchInstruction { vstack, context ->
    F64ConvertI64UExecutor(vstack, context, instruction)
}

fun F64ConvertI64UDispatcher(
    instruction: NumericInstruction.F64ConvertI64US,
) = dispatchInstruction { vstack, context ->
    F64ConvertI64UExecutor(vstack, context, instruction)
}

fun F64PromoteF32Dispatcher(
    instruction: NumericInstruction.F64PromoteF32I,
) = dispatchInstruction { vstack, context ->
    F64PromoteF32Executor(vstack, context, instruction)
}

fun F64PromoteF32Dispatcher(
    instruction: NumericInstruction.F64PromoteF32S,
) = dispatchInstruction { vstack, context ->
    F64PromoteF32Executor(vstack, context, instruction)
}
