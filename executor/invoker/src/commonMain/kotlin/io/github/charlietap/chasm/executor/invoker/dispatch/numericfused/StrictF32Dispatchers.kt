package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32CopysignExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32DivExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32MaxExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32MinExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32DemoteF64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ReinterpretI32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32GeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32GtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32LeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32LtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32NeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32AbsExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32CeilExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32FloorExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32NearestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32NegExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32SqrtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32TruncExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun F32AddDispatcher(instruction: NumericSuperInstruction.F32AddIi) = dispatchInstruction { vstack, context -> F32AddExecutor(vstack, context, instruction) }

fun F32AddDispatcher(instruction: NumericSuperInstruction.F32AddIs) = dispatchInstruction { vstack, context -> F32AddExecutor(vstack, context, instruction) }

fun F32AddDispatcher(instruction: NumericSuperInstruction.F32AddSi) = dispatchInstruction { vstack, context -> F32AddExecutor(vstack, context, instruction) }

fun F32AddDispatcher(instruction: NumericSuperInstruction.F32AddSs) = dispatchInstruction { vstack, context -> F32AddExecutor(vstack, context, instruction) }

fun F32SubDispatcher(instruction: NumericSuperInstruction.F32SubIi) = dispatchInstruction { vstack, context -> F32SubExecutor(vstack, context, instruction) }

fun F32SubDispatcher(instruction: NumericSuperInstruction.F32SubIs) = dispatchInstruction { vstack, context -> F32SubExecutor(vstack, context, instruction) }

fun F32SubDispatcher(instruction: NumericSuperInstruction.F32SubSi) = dispatchInstruction { vstack, context -> F32SubExecutor(vstack, context, instruction) }

fun F32SubDispatcher(instruction: NumericSuperInstruction.F32SubSs) = dispatchInstruction { vstack, context -> F32SubExecutor(vstack, context, instruction) }

fun F32MulDispatcher(instruction: NumericSuperInstruction.F32MulIi) = dispatchInstruction { vstack, context -> F32MulExecutor(vstack, context, instruction) }

fun F32MulDispatcher(instruction: NumericSuperInstruction.F32MulIs) = dispatchInstruction { vstack, context -> F32MulExecutor(vstack, context, instruction) }

fun F32MulDispatcher(instruction: NumericSuperInstruction.F32MulSi) = dispatchInstruction { vstack, context -> F32MulExecutor(vstack, context, instruction) }

fun F32MulDispatcher(instruction: NumericSuperInstruction.F32MulSs) = dispatchInstruction { vstack, context -> F32MulExecutor(vstack, context, instruction) }

fun F32DivDispatcher(instruction: NumericSuperInstruction.F32DivIi) = dispatchInstruction { vstack, context -> F32DivExecutor(vstack, context, instruction) }

fun F32DivDispatcher(instruction: NumericSuperInstruction.F32DivIs) = dispatchInstruction { vstack, context -> F32DivExecutor(vstack, context, instruction) }

fun F32DivDispatcher(instruction: NumericSuperInstruction.F32DivSi) = dispatchInstruction { vstack, context -> F32DivExecutor(vstack, context, instruction) }

fun F32DivDispatcher(instruction: NumericSuperInstruction.F32DivSs) = dispatchInstruction { vstack, context -> F32DivExecutor(vstack, context, instruction) }

fun F32MinDispatcher(instruction: NumericSuperInstruction.F32MinIi) = dispatchInstruction { vstack, context -> F32MinExecutor(vstack, context, instruction) }

fun F32MinDispatcher(instruction: NumericSuperInstruction.F32MinIs) = dispatchInstruction { vstack, context -> F32MinExecutor(vstack, context, instruction) }

fun F32MinDispatcher(instruction: NumericSuperInstruction.F32MinSi) = dispatchInstruction { vstack, context -> F32MinExecutor(vstack, context, instruction) }

fun F32MinDispatcher(instruction: NumericSuperInstruction.F32MinSs) = dispatchInstruction { vstack, context -> F32MinExecutor(vstack, context, instruction) }

fun F32MaxDispatcher(instruction: NumericSuperInstruction.F32MaxIi) = dispatchInstruction { vstack, context -> F32MaxExecutor(vstack, context, instruction) }

fun F32MaxDispatcher(instruction: NumericSuperInstruction.F32MaxIs) = dispatchInstruction { vstack, context -> F32MaxExecutor(vstack, context, instruction) }

fun F32MaxDispatcher(instruction: NumericSuperInstruction.F32MaxSi) = dispatchInstruction { vstack, context -> F32MaxExecutor(vstack, context, instruction) }

fun F32MaxDispatcher(instruction: NumericSuperInstruction.F32MaxSs) = dispatchInstruction { vstack, context -> F32MaxExecutor(vstack, context, instruction) }

fun F32CopysignDispatcher(instruction: NumericSuperInstruction.F32CopysignIi) = dispatchInstruction { vstack, context -> F32CopysignExecutor(vstack, context, instruction) }

fun F32CopysignDispatcher(instruction: NumericSuperInstruction.F32CopysignIs) = dispatchInstruction { vstack, context -> F32CopysignExecutor(vstack, context, instruction) }

fun F32CopysignDispatcher(instruction: NumericSuperInstruction.F32CopysignSi) = dispatchInstruction { vstack, context -> F32CopysignExecutor(vstack, context, instruction) }

fun F32CopysignDispatcher(instruction: NumericSuperInstruction.F32CopysignSs) = dispatchInstruction { vstack, context -> F32CopysignExecutor(vstack, context, instruction) }

fun F32AbsDispatcher(instruction: NumericSuperInstruction.F32AbsI) = dispatchInstruction { vstack, context -> F32AbsExecutor(vstack, context, instruction) }

fun F32AbsDispatcher(instruction: NumericSuperInstruction.F32AbsS) = dispatchInstruction { vstack, context -> F32AbsExecutor(vstack, context, instruction) }

fun F32NegDispatcher(instruction: NumericSuperInstruction.F32NegI) = dispatchInstruction { vstack, context -> F32NegExecutor(vstack, context, instruction) }

fun F32NegDispatcher(instruction: NumericSuperInstruction.F32NegS) = dispatchInstruction { vstack, context -> F32NegExecutor(vstack, context, instruction) }

fun F32CeilDispatcher(instruction: NumericSuperInstruction.F32CeilI) = dispatchInstruction { vstack, context -> F32CeilExecutor(vstack, context, instruction) }

fun F32CeilDispatcher(instruction: NumericSuperInstruction.F32CeilS) = dispatchInstruction { vstack, context -> F32CeilExecutor(vstack, context, instruction) }

fun F32FloorDispatcher(instruction: NumericSuperInstruction.F32FloorI) = dispatchInstruction { vstack, context -> F32FloorExecutor(vstack, context, instruction) }

fun F32FloorDispatcher(instruction: NumericSuperInstruction.F32FloorS) = dispatchInstruction { vstack, context -> F32FloorExecutor(vstack, context, instruction) }

fun F32TruncDispatcher(instruction: NumericSuperInstruction.F32TruncI) = dispatchInstruction { vstack, context -> F32TruncExecutor(vstack, context, instruction) }

fun F32TruncDispatcher(instruction: NumericSuperInstruction.F32TruncS) = dispatchInstruction { vstack, context -> F32TruncExecutor(vstack, context, instruction) }

fun F32NearestDispatcher(instruction: NumericSuperInstruction.F32NearestI) = dispatchInstruction { vstack, context -> F32NearestExecutor(vstack, context, instruction) }

fun F32NearestDispatcher(instruction: NumericSuperInstruction.F32NearestS) = dispatchInstruction { vstack, context -> F32NearestExecutor(vstack, context, instruction) }

fun F32SqrtDispatcher(instruction: NumericSuperInstruction.F32SqrtI) = dispatchInstruction { vstack, context -> F32SqrtExecutor(vstack, context, instruction) }

fun F32SqrtDispatcher(instruction: NumericSuperInstruction.F32SqrtS) = dispatchInstruction { vstack, context -> F32SqrtExecutor(vstack, context, instruction) }

fun F32EqDispatcher(instruction: NumericSuperInstruction.F32EqIi) = dispatchInstruction { vstack, context -> F32EqExecutor(vstack, context, instruction) }

fun F32EqDispatcher(instruction: NumericSuperInstruction.F32EqIs) = dispatchInstruction { vstack, context -> F32EqExecutor(vstack, context, instruction) }

fun F32EqDispatcher(instruction: NumericSuperInstruction.F32EqSi) = dispatchInstruction { vstack, context -> F32EqExecutor(vstack, context, instruction) }

fun F32EqDispatcher(instruction: NumericSuperInstruction.F32EqSs) = dispatchInstruction { vstack, context -> F32EqExecutor(vstack, context, instruction) }

fun F32NeDispatcher(instruction: NumericSuperInstruction.F32NeIi) = dispatchInstruction { vstack, context -> F32NeExecutor(vstack, context, instruction) }

fun F32NeDispatcher(instruction: NumericSuperInstruction.F32NeIs) = dispatchInstruction { vstack, context -> F32NeExecutor(vstack, context, instruction) }

fun F32NeDispatcher(instruction: NumericSuperInstruction.F32NeSi) = dispatchInstruction { vstack, context -> F32NeExecutor(vstack, context, instruction) }

fun F32NeDispatcher(instruction: NumericSuperInstruction.F32NeSs) = dispatchInstruction { vstack, context -> F32NeExecutor(vstack, context, instruction) }

fun F32LtDispatcher(instruction: NumericSuperInstruction.F32LtIi) = dispatchInstruction { vstack, context -> F32LtExecutor(vstack, context, instruction) }

fun F32LtDispatcher(instruction: NumericSuperInstruction.F32LtIs) = dispatchInstruction { vstack, context -> F32LtExecutor(vstack, context, instruction) }

fun F32LtDispatcher(instruction: NumericSuperInstruction.F32LtSi) = dispatchInstruction { vstack, context -> F32LtExecutor(vstack, context, instruction) }

fun F32LtDispatcher(instruction: NumericSuperInstruction.F32LtSs) = dispatchInstruction { vstack, context -> F32LtExecutor(vstack, context, instruction) }

fun F32GtDispatcher(instruction: NumericSuperInstruction.F32GtIi) = dispatchInstruction { vstack, context -> F32GtExecutor(vstack, context, instruction) }

fun F32GtDispatcher(instruction: NumericSuperInstruction.F32GtIs) = dispatchInstruction { vstack, context -> F32GtExecutor(vstack, context, instruction) }

fun F32GtDispatcher(instruction: NumericSuperInstruction.F32GtSi) = dispatchInstruction { vstack, context -> F32GtExecutor(vstack, context, instruction) }

fun F32GtDispatcher(instruction: NumericSuperInstruction.F32GtSs) = dispatchInstruction { vstack, context -> F32GtExecutor(vstack, context, instruction) }

fun F32LeDispatcher(instruction: NumericSuperInstruction.F32LeIi) = dispatchInstruction { vstack, context -> F32LeExecutor(vstack, context, instruction) }

fun F32LeDispatcher(instruction: NumericSuperInstruction.F32LeIs) = dispatchInstruction { vstack, context -> F32LeExecutor(vstack, context, instruction) }

fun F32LeDispatcher(instruction: NumericSuperInstruction.F32LeSi) = dispatchInstruction { vstack, context -> F32LeExecutor(vstack, context, instruction) }

fun F32LeDispatcher(instruction: NumericSuperInstruction.F32LeSs) = dispatchInstruction { vstack, context -> F32LeExecutor(vstack, context, instruction) }

fun F32GeDispatcher(instruction: NumericSuperInstruction.F32GeIi) = dispatchInstruction { vstack, context -> F32GeExecutor(vstack, context, instruction) }

fun F32GeDispatcher(instruction: NumericSuperInstruction.F32GeIs) = dispatchInstruction { vstack, context -> F32GeExecutor(vstack, context, instruction) }

fun F32GeDispatcher(instruction: NumericSuperInstruction.F32GeSi) = dispatchInstruction { vstack, context -> F32GeExecutor(vstack, context, instruction) }

fun F32GeDispatcher(instruction: NumericSuperInstruction.F32GeSs) = dispatchInstruction { vstack, context -> F32GeExecutor(vstack, context, instruction) }

fun F32ConvertI32SDispatcher(instruction: NumericSuperInstruction.F32ConvertI32SI) = dispatchInstruction { vstack, context -> F32ConvertI32SExecutor(vstack, context, instruction) }

fun F32ConvertI32SDispatcher(instruction: NumericSuperInstruction.F32ConvertI32SS) = dispatchInstruction { vstack, context -> F32ConvertI32SExecutor(vstack, context, instruction) }

fun F32ConvertI32UDispatcher(instruction: NumericSuperInstruction.F32ConvertI32UI) = dispatchInstruction { vstack, context -> F32ConvertI32UExecutor(vstack, context, instruction) }

fun F32ConvertI32UDispatcher(instruction: NumericSuperInstruction.F32ConvertI32US) = dispatchInstruction { vstack, context -> F32ConvertI32UExecutor(vstack, context, instruction) }

fun F32ConvertI64SDispatcher(instruction: NumericSuperInstruction.F32ConvertI64SI) = dispatchInstruction { vstack, context -> F32ConvertI64SExecutor(vstack, context, instruction) }

fun F32ConvertI64SDispatcher(instruction: NumericSuperInstruction.F32ConvertI64SS) = dispatchInstruction { vstack, context -> F32ConvertI64SExecutor(vstack, context, instruction) }

fun F32ConvertI64UDispatcher(instruction: NumericSuperInstruction.F32ConvertI64UI) = dispatchInstruction { vstack, context -> F32ConvertI64UExecutor(vstack, context, instruction) }

fun F32ConvertI64UDispatcher(instruction: NumericSuperInstruction.F32ConvertI64US) = dispatchInstruction { vstack, context -> F32ConvertI64UExecutor(vstack, context, instruction) }

fun F32DemoteF64Dispatcher(instruction: NumericSuperInstruction.F32DemoteF64I) = dispatchInstruction { vstack, context -> F32DemoteF64Executor(vstack, context, instruction) }

fun F32DemoteF64Dispatcher(instruction: NumericSuperInstruction.F32DemoteF64S) = dispatchInstruction { vstack, context -> F32DemoteF64Executor(vstack, context, instruction) }

fun F32ReinterpretI32Dispatcher(instruction: NumericSuperInstruction.F32ReinterpretI32I) = dispatchInstruction { vstack, context -> F32ReinterpretI32Executor(vstack, context, instruction) }

fun F32ReinterpretI32Dispatcher(instruction: NumericSuperInstruction.F32ReinterpretI32S) = dispatchInstruction { vstack, context -> F32ReinterpretI32Executor(vstack, context, instruction) }
