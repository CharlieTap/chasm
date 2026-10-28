package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32CopysignExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32DivExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MaxExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MinExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32DemoteF64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32NeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32AbsExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32CeilExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32FloorExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NearestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NegExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32SqrtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32TruncExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32AddDispatcher(
    instruction: NumericInstruction.F32AddIi,
) = dispatchInstruction { vstack, context ->
    F32AddExecutor(vstack, context, instruction)
}

fun F32AddDispatcher(
    instruction: NumericInstruction.F32AddIs,
) = dispatchInstruction { vstack, context ->
    F32AddExecutor(vstack, context, instruction)
}

fun F32AddDispatcher(
    instruction: NumericInstruction.F32AddSi,
) = dispatchInstruction { vstack, context ->
    F32AddExecutor(vstack, context, instruction)
}

fun F32AddDispatcher(
    instruction: NumericInstruction.F32AddSs,
) = dispatchInstruction { vstack, context ->
    F32AddExecutor(vstack, context, instruction)
}

fun F32SubDispatcher(
    instruction: NumericInstruction.F32SubIi,
) = dispatchInstruction { vstack, context ->
    F32SubExecutor(vstack, context, instruction)
}

fun F32SubDispatcher(
    instruction: NumericInstruction.F32SubIs,
) = dispatchInstruction { vstack, context ->
    F32SubExecutor(vstack, context, instruction)
}

fun F32SubDispatcher(
    instruction: NumericInstruction.F32SubSi,
) = dispatchInstruction { vstack, context ->
    F32SubExecutor(vstack, context, instruction)
}

fun F32SubDispatcher(
    instruction: NumericInstruction.F32SubSs,
) = dispatchInstruction { vstack, context ->
    F32SubExecutor(vstack, context, instruction)
}

fun F32MulDispatcher(
    instruction: NumericInstruction.F32MulIi,
) = dispatchInstruction { vstack, context ->
    F32MulExecutor(vstack, context, instruction)
}

fun F32MulDispatcher(
    instruction: NumericInstruction.F32MulIs,
) = dispatchInstruction { vstack, context ->
    F32MulExecutor(vstack, context, instruction)
}

fun F32MulDispatcher(
    instruction: NumericInstruction.F32MulSi,
) = dispatchInstruction { vstack, context ->
    F32MulExecutor(vstack, context, instruction)
}

fun F32MulDispatcher(
    instruction: NumericInstruction.F32MulSs,
) = dispatchInstruction { vstack, context ->
    F32MulExecutor(vstack, context, instruction)
}

fun F32DivDispatcher(
    instruction: NumericInstruction.F32DivIi,
) = dispatchInstruction { vstack, context ->
    F32DivExecutor(vstack, context, instruction)
}

fun F32DivDispatcher(
    instruction: NumericInstruction.F32DivIs,
) = dispatchInstruction { vstack, context ->
    F32DivExecutor(vstack, context, instruction)
}

fun F32DivDispatcher(
    instruction: NumericInstruction.F32DivSi,
) = dispatchInstruction { vstack, context ->
    F32DivExecutor(vstack, context, instruction)
}

fun F32DivDispatcher(
    instruction: NumericInstruction.F32DivSs,
) = dispatchInstruction { vstack, context ->
    F32DivExecutor(vstack, context, instruction)
}

fun F32MinDispatcher(
    instruction: NumericInstruction.F32MinIi,
) = dispatchInstruction { vstack, context ->
    F32MinExecutor(vstack, context, instruction)
}

fun F32MinDispatcher(
    instruction: NumericInstruction.F32MinIs,
) = dispatchInstruction { vstack, context ->
    F32MinExecutor(vstack, context, instruction)
}

fun F32MinDispatcher(
    instruction: NumericInstruction.F32MinSi,
) = dispatchInstruction { vstack, context ->
    F32MinExecutor(vstack, context, instruction)
}

fun F32MinDispatcher(
    instruction: NumericInstruction.F32MinSs,
) = dispatchInstruction { vstack, context ->
    F32MinExecutor(vstack, context, instruction)
}

fun F32MaxDispatcher(
    instruction: NumericInstruction.F32MaxIi,
) = dispatchInstruction { vstack, context ->
    F32MaxExecutor(vstack, context, instruction)
}

fun F32MaxDispatcher(
    instruction: NumericInstruction.F32MaxIs,
) = dispatchInstruction { vstack, context ->
    F32MaxExecutor(vstack, context, instruction)
}

fun F32MaxDispatcher(
    instruction: NumericInstruction.F32MaxSi,
) = dispatchInstruction { vstack, context ->
    F32MaxExecutor(vstack, context, instruction)
}

fun F32MaxDispatcher(
    instruction: NumericInstruction.F32MaxSs,
) = dispatchInstruction { vstack, context ->
    F32MaxExecutor(vstack, context, instruction)
}

fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32CopysignIi,
) = dispatchInstruction { vstack, context ->
    F32CopysignExecutor(vstack, context, instruction)
}

fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32CopysignIs,
) = dispatchInstruction { vstack, context ->
    F32CopysignExecutor(vstack, context, instruction)
}

fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32CopysignSi,
) = dispatchInstruction { vstack, context ->
    F32CopysignExecutor(vstack, context, instruction)
}

fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32CopysignSs,
) = dispatchInstruction { vstack, context ->
    F32CopysignExecutor(vstack, context, instruction)
}

fun F32AbsDispatcher(
    instruction: NumericInstruction.F32AbsI,
) = dispatchInstruction { vstack, context ->
    F32AbsExecutor(vstack, context, instruction)
}

fun F32AbsDispatcher(
    instruction: NumericInstruction.F32AbsS,
) = dispatchInstruction { vstack, context ->
    F32AbsExecutor(vstack, context, instruction)
}

fun F32NegDispatcher(
    instruction: NumericInstruction.F32NegI,
) = dispatchInstruction { vstack, context ->
    F32NegExecutor(vstack, context, instruction)
}

fun F32NegDispatcher(
    instruction: NumericInstruction.F32NegS,
) = dispatchInstruction { vstack, context ->
    F32NegExecutor(vstack, context, instruction)
}

fun F32CeilDispatcher(
    instruction: NumericInstruction.F32CeilI,
) = dispatchInstruction { vstack, context ->
    F32CeilExecutor(vstack, context, instruction)
}

fun F32CeilDispatcher(
    instruction: NumericInstruction.F32CeilS,
) = dispatchInstruction { vstack, context ->
    F32CeilExecutor(vstack, context, instruction)
}

fun F32FloorDispatcher(
    instruction: NumericInstruction.F32FloorI,
) = dispatchInstruction { vstack, context ->
    F32FloorExecutor(vstack, context, instruction)
}

fun F32FloorDispatcher(
    instruction: NumericInstruction.F32FloorS,
) = dispatchInstruction { vstack, context ->
    F32FloorExecutor(vstack, context, instruction)
}

fun F32TruncDispatcher(
    instruction: NumericInstruction.F32TruncI,
) = dispatchInstruction { vstack, context ->
    F32TruncExecutor(vstack, context, instruction)
}

fun F32TruncDispatcher(
    instruction: NumericInstruction.F32TruncS,
) = dispatchInstruction { vstack, context ->
    F32TruncExecutor(vstack, context, instruction)
}

fun F32NearestDispatcher(
    instruction: NumericInstruction.F32NearestI,
) = dispatchInstruction { vstack, context ->
    F32NearestExecutor(vstack, context, instruction)
}

fun F32NearestDispatcher(
    instruction: NumericInstruction.F32NearestS,
) = dispatchInstruction { vstack, context ->
    F32NearestExecutor(vstack, context, instruction)
}

fun F32SqrtDispatcher(
    instruction: NumericInstruction.F32SqrtI,
) = dispatchInstruction { vstack, context ->
    F32SqrtExecutor(vstack, context, instruction)
}

fun F32SqrtDispatcher(
    instruction: NumericInstruction.F32SqrtS,
) = dispatchInstruction { vstack, context ->
    F32SqrtExecutor(vstack, context, instruction)
}

fun F32EqDispatcher(
    instruction: NumericInstruction.F32EqIi,
) = dispatchInstruction { vstack, context ->
    F32EqExecutor(vstack, context, instruction)
}

fun F32EqDispatcher(
    instruction: NumericInstruction.F32EqIs,
) = dispatchInstruction { vstack, context ->
    F32EqExecutor(vstack, context, instruction)
}

fun F32EqDispatcher(
    instruction: NumericInstruction.F32EqSi,
) = dispatchInstruction { vstack, context ->
    F32EqExecutor(vstack, context, instruction)
}

fun F32EqDispatcher(
    instruction: NumericInstruction.F32EqSs,
) = dispatchInstruction { vstack, context ->
    F32EqExecutor(vstack, context, instruction)
}

fun F32NeDispatcher(
    instruction: NumericInstruction.F32NeIi,
) = dispatchInstruction { vstack, context ->
    F32NeExecutor(vstack, context, instruction)
}

fun F32NeDispatcher(
    instruction: NumericInstruction.F32NeIs,
) = dispatchInstruction { vstack, context ->
    F32NeExecutor(vstack, context, instruction)
}

fun F32NeDispatcher(
    instruction: NumericInstruction.F32NeSi,
) = dispatchInstruction { vstack, context ->
    F32NeExecutor(vstack, context, instruction)
}

fun F32NeDispatcher(
    instruction: NumericInstruction.F32NeSs,
) = dispatchInstruction { vstack, context ->
    F32NeExecutor(vstack, context, instruction)
}

fun F32LtDispatcher(
    instruction: NumericInstruction.F32LtIi,
) = dispatchInstruction { vstack, context ->
    F32LtExecutor(vstack, context, instruction)
}

fun F32LtDispatcher(
    instruction: NumericInstruction.F32LtIs,
) = dispatchInstruction { vstack, context ->
    F32LtExecutor(vstack, context, instruction)
}

fun F32LtDispatcher(
    instruction: NumericInstruction.F32LtSi,
) = dispatchInstruction { vstack, context ->
    F32LtExecutor(vstack, context, instruction)
}

fun F32LtDispatcher(
    instruction: NumericInstruction.F32LtSs,
) = dispatchInstruction { vstack, context ->
    F32LtExecutor(vstack, context, instruction)
}

fun F32GtDispatcher(
    instruction: NumericInstruction.F32GtIi,
) = dispatchInstruction { vstack, context ->
    F32GtExecutor(vstack, context, instruction)
}

fun F32GtDispatcher(
    instruction: NumericInstruction.F32GtIs,
) = dispatchInstruction { vstack, context ->
    F32GtExecutor(vstack, context, instruction)
}

fun F32GtDispatcher(
    instruction: NumericInstruction.F32GtSi,
) = dispatchInstruction { vstack, context ->
    F32GtExecutor(vstack, context, instruction)
}

fun F32GtDispatcher(
    instruction: NumericInstruction.F32GtSs,
) = dispatchInstruction { vstack, context ->
    F32GtExecutor(vstack, context, instruction)
}

fun F32LeDispatcher(
    instruction: NumericInstruction.F32LeIi,
) = dispatchInstruction { vstack, context ->
    F32LeExecutor(vstack, context, instruction)
}

fun F32LeDispatcher(
    instruction: NumericInstruction.F32LeIs,
) = dispatchInstruction { vstack, context ->
    F32LeExecutor(vstack, context, instruction)
}

fun F32LeDispatcher(
    instruction: NumericInstruction.F32LeSi,
) = dispatchInstruction { vstack, context ->
    F32LeExecutor(vstack, context, instruction)
}

fun F32LeDispatcher(
    instruction: NumericInstruction.F32LeSs,
) = dispatchInstruction { vstack, context ->
    F32LeExecutor(vstack, context, instruction)
}

fun F32GeDispatcher(
    instruction: NumericInstruction.F32GeIi,
) = dispatchInstruction { vstack, context ->
    F32GeExecutor(vstack, context, instruction)
}

fun F32GeDispatcher(
    instruction: NumericInstruction.F32GeIs,
) = dispatchInstruction { vstack, context ->
    F32GeExecutor(vstack, context, instruction)
}

fun F32GeDispatcher(
    instruction: NumericInstruction.F32GeSi,
) = dispatchInstruction { vstack, context ->
    F32GeExecutor(vstack, context, instruction)
}

fun F32GeDispatcher(
    instruction: NumericInstruction.F32GeSs,
) = dispatchInstruction { vstack, context ->
    F32GeExecutor(vstack, context, instruction)
}

fun F32ConvertI32SDispatcher(
    instruction: NumericInstruction.F32ConvertI32SI,
) = dispatchInstruction { vstack, context ->
    F32ConvertI32SExecutor(vstack, context, instruction)
}

fun F32ConvertI32SDispatcher(
    instruction: NumericInstruction.F32ConvertI32SS,
) = dispatchInstruction { vstack, context ->
    F32ConvertI32SExecutor(vstack, context, instruction)
}

fun F32ConvertI32UDispatcher(
    instruction: NumericInstruction.F32ConvertI32UI,
) = dispatchInstruction { vstack, context ->
    F32ConvertI32UExecutor(vstack, context, instruction)
}

fun F32ConvertI32UDispatcher(
    instruction: NumericInstruction.F32ConvertI32US,
) = dispatchInstruction { vstack, context ->
    F32ConvertI32UExecutor(vstack, context, instruction)
}

fun F32ConvertI64SDispatcher(
    instruction: NumericInstruction.F32ConvertI64SI,
) = dispatchInstruction { vstack, context ->
    F32ConvertI64SExecutor(vstack, context, instruction)
}

fun F32ConvertI64SDispatcher(
    instruction: NumericInstruction.F32ConvertI64SS,
) = dispatchInstruction { vstack, context ->
    F32ConvertI64SExecutor(vstack, context, instruction)
}

fun F32ConvertI64UDispatcher(
    instruction: NumericInstruction.F32ConvertI64UI,
) = dispatchInstruction { vstack, context ->
    F32ConvertI64UExecutor(vstack, context, instruction)
}

fun F32ConvertI64UDispatcher(
    instruction: NumericInstruction.F32ConvertI64US,
) = dispatchInstruction { vstack, context ->
    F32ConvertI64UExecutor(vstack, context, instruction)
}

fun F32DemoteF64Dispatcher(
    instruction: NumericInstruction.F32DemoteF64I,
) = dispatchInstruction { vstack, context ->
    F32DemoteF64Executor(vstack, context, instruction)
}

fun F32DemoteF64Dispatcher(
    instruction: NumericInstruction.F32DemoteF64S,
) = dispatchInstruction { vstack, context ->
    F32DemoteF64Executor(vstack, context, instruction)
}
