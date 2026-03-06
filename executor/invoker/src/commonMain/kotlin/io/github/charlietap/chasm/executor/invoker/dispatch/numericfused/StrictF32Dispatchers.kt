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
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32AddDispatcher(instruction: FusedNumericInstruction.F32AddIi) = dispatchInstruction(instruction, ::F32AddExecutor)

fun F32AddDispatcher(instruction: FusedNumericInstruction.F32AddIs) = dispatchInstruction(instruction, ::F32AddExecutor)

fun F32AddDispatcher(instruction: FusedNumericInstruction.F32AddSi) = dispatchInstruction(instruction, ::F32AddExecutor)

fun F32AddDispatcher(instruction: FusedNumericInstruction.F32AddSs) = dispatchInstruction(instruction, ::F32AddExecutor)

fun F32SubDispatcher(instruction: FusedNumericInstruction.F32SubIi) = dispatchInstruction(instruction, ::F32SubExecutor)

fun F32SubDispatcher(instruction: FusedNumericInstruction.F32SubIs) = dispatchInstruction(instruction, ::F32SubExecutor)

fun F32SubDispatcher(instruction: FusedNumericInstruction.F32SubSi) = dispatchInstruction(instruction, ::F32SubExecutor)

fun F32SubDispatcher(instruction: FusedNumericInstruction.F32SubSs) = dispatchInstruction(instruction, ::F32SubExecutor)

fun F32MulDispatcher(instruction: FusedNumericInstruction.F32MulIi) = dispatchInstruction(instruction, ::F32MulExecutor)

fun F32MulDispatcher(instruction: FusedNumericInstruction.F32MulIs) = dispatchInstruction(instruction, ::F32MulExecutor)

fun F32MulDispatcher(instruction: FusedNumericInstruction.F32MulSi) = dispatchInstruction(instruction, ::F32MulExecutor)

fun F32MulDispatcher(instruction: FusedNumericInstruction.F32MulSs) = dispatchInstruction(instruction, ::F32MulExecutor)

fun F32DivDispatcher(instruction: FusedNumericInstruction.F32DivIi) = dispatchInstruction(instruction, ::F32DivExecutor)

fun F32DivDispatcher(instruction: FusedNumericInstruction.F32DivIs) = dispatchInstruction(instruction, ::F32DivExecutor)

fun F32DivDispatcher(instruction: FusedNumericInstruction.F32DivSi) = dispatchInstruction(instruction, ::F32DivExecutor)

fun F32DivDispatcher(instruction: FusedNumericInstruction.F32DivSs) = dispatchInstruction(instruction, ::F32DivExecutor)

fun F32MinDispatcher(instruction: FusedNumericInstruction.F32MinIi) = dispatchInstruction(instruction, ::F32MinExecutor)

fun F32MinDispatcher(instruction: FusedNumericInstruction.F32MinIs) = dispatchInstruction(instruction, ::F32MinExecutor)

fun F32MinDispatcher(instruction: FusedNumericInstruction.F32MinSi) = dispatchInstruction(instruction, ::F32MinExecutor)

fun F32MinDispatcher(instruction: FusedNumericInstruction.F32MinSs) = dispatchInstruction(instruction, ::F32MinExecutor)

fun F32MaxDispatcher(instruction: FusedNumericInstruction.F32MaxIi) = dispatchInstruction(instruction, ::F32MaxExecutor)

fun F32MaxDispatcher(instruction: FusedNumericInstruction.F32MaxIs) = dispatchInstruction(instruction, ::F32MaxExecutor)

fun F32MaxDispatcher(instruction: FusedNumericInstruction.F32MaxSi) = dispatchInstruction(instruction, ::F32MaxExecutor)

fun F32MaxDispatcher(instruction: FusedNumericInstruction.F32MaxSs) = dispatchInstruction(instruction, ::F32MaxExecutor)

fun F32CopysignDispatcher(instruction: FusedNumericInstruction.F32CopysignIi) = dispatchInstruction(instruction, ::F32CopysignExecutor)

fun F32CopysignDispatcher(instruction: FusedNumericInstruction.F32CopysignIs) = dispatchInstruction(instruction, ::F32CopysignExecutor)

fun F32CopysignDispatcher(instruction: FusedNumericInstruction.F32CopysignSi) = dispatchInstruction(instruction, ::F32CopysignExecutor)

fun F32CopysignDispatcher(instruction: FusedNumericInstruction.F32CopysignSs) = dispatchInstruction(instruction, ::F32CopysignExecutor)

fun F32AbsDispatcher(instruction: FusedNumericInstruction.F32AbsI) = dispatchInstruction(instruction, ::F32AbsExecutor)

fun F32AbsDispatcher(instruction: FusedNumericInstruction.F32AbsS) = dispatchInstruction(instruction, ::F32AbsExecutor)

fun F32NegDispatcher(instruction: FusedNumericInstruction.F32NegI) = dispatchInstruction(instruction, ::F32NegExecutor)

fun F32NegDispatcher(instruction: FusedNumericInstruction.F32NegS) = dispatchInstruction(instruction, ::F32NegExecutor)

fun F32CeilDispatcher(instruction: FusedNumericInstruction.F32CeilI) = dispatchInstruction(instruction, ::F32CeilExecutor)

fun F32CeilDispatcher(instruction: FusedNumericInstruction.F32CeilS) = dispatchInstruction(instruction, ::F32CeilExecutor)

fun F32FloorDispatcher(instruction: FusedNumericInstruction.F32FloorI) = dispatchInstruction(instruction, ::F32FloorExecutor)

fun F32FloorDispatcher(instruction: FusedNumericInstruction.F32FloorS) = dispatchInstruction(instruction, ::F32FloorExecutor)

fun F32TruncDispatcher(instruction: FusedNumericInstruction.F32TruncI) = dispatchInstruction(instruction, ::F32TruncExecutor)

fun F32TruncDispatcher(instruction: FusedNumericInstruction.F32TruncS) = dispatchInstruction(instruction, ::F32TruncExecutor)

fun F32NearestDispatcher(instruction: FusedNumericInstruction.F32NearestI) = dispatchInstruction(instruction, ::F32NearestExecutor)

fun F32NearestDispatcher(instruction: FusedNumericInstruction.F32NearestS) = dispatchInstruction(instruction, ::F32NearestExecutor)

fun F32SqrtDispatcher(instruction: FusedNumericInstruction.F32SqrtI) = dispatchInstruction(instruction, ::F32SqrtExecutor)

fun F32SqrtDispatcher(instruction: FusedNumericInstruction.F32SqrtS) = dispatchInstruction(instruction, ::F32SqrtExecutor)

fun F32EqDispatcher(instruction: FusedNumericInstruction.F32EqIi) = dispatchInstruction(instruction, ::F32EqExecutor)

fun F32EqDispatcher(instruction: FusedNumericInstruction.F32EqIs) = dispatchInstruction(instruction, ::F32EqExecutor)

fun F32EqDispatcher(instruction: FusedNumericInstruction.F32EqSi) = dispatchInstruction(instruction, ::F32EqExecutor)

fun F32EqDispatcher(instruction: FusedNumericInstruction.F32EqSs) = dispatchInstruction(instruction, ::F32EqExecutor)

fun F32NeDispatcher(instruction: FusedNumericInstruction.F32NeIi) = dispatchInstruction(instruction, ::F32NeExecutor)

fun F32NeDispatcher(instruction: FusedNumericInstruction.F32NeIs) = dispatchInstruction(instruction, ::F32NeExecutor)

fun F32NeDispatcher(instruction: FusedNumericInstruction.F32NeSi) = dispatchInstruction(instruction, ::F32NeExecutor)

fun F32NeDispatcher(instruction: FusedNumericInstruction.F32NeSs) = dispatchInstruction(instruction, ::F32NeExecutor)

fun F32LtDispatcher(instruction: FusedNumericInstruction.F32LtIi) = dispatchInstruction(instruction, ::F32LtExecutor)

fun F32LtDispatcher(instruction: FusedNumericInstruction.F32LtIs) = dispatchInstruction(instruction, ::F32LtExecutor)

fun F32LtDispatcher(instruction: FusedNumericInstruction.F32LtSi) = dispatchInstruction(instruction, ::F32LtExecutor)

fun F32LtDispatcher(instruction: FusedNumericInstruction.F32LtSs) = dispatchInstruction(instruction, ::F32LtExecutor)

fun F32GtDispatcher(instruction: FusedNumericInstruction.F32GtIi) = dispatchInstruction(instruction, ::F32GtExecutor)

fun F32GtDispatcher(instruction: FusedNumericInstruction.F32GtIs) = dispatchInstruction(instruction, ::F32GtExecutor)

fun F32GtDispatcher(instruction: FusedNumericInstruction.F32GtSi) = dispatchInstruction(instruction, ::F32GtExecutor)

fun F32GtDispatcher(instruction: FusedNumericInstruction.F32GtSs) = dispatchInstruction(instruction, ::F32GtExecutor)

fun F32LeDispatcher(instruction: FusedNumericInstruction.F32LeIi) = dispatchInstruction(instruction, ::F32LeExecutor)

fun F32LeDispatcher(instruction: FusedNumericInstruction.F32LeIs) = dispatchInstruction(instruction, ::F32LeExecutor)

fun F32LeDispatcher(instruction: FusedNumericInstruction.F32LeSi) = dispatchInstruction(instruction, ::F32LeExecutor)

fun F32LeDispatcher(instruction: FusedNumericInstruction.F32LeSs) = dispatchInstruction(instruction, ::F32LeExecutor)

fun F32GeDispatcher(instruction: FusedNumericInstruction.F32GeIi) = dispatchInstruction(instruction, ::F32GeExecutor)

fun F32GeDispatcher(instruction: FusedNumericInstruction.F32GeIs) = dispatchInstruction(instruction, ::F32GeExecutor)

fun F32GeDispatcher(instruction: FusedNumericInstruction.F32GeSi) = dispatchInstruction(instruction, ::F32GeExecutor)

fun F32GeDispatcher(instruction: FusedNumericInstruction.F32GeSs) = dispatchInstruction(instruction, ::F32GeExecutor)

fun F32ConvertI32SDispatcher(instruction: FusedNumericInstruction.F32ConvertI32SI) = dispatchInstruction(instruction, ::F32ConvertI32SExecutor)

fun F32ConvertI32SDispatcher(instruction: FusedNumericInstruction.F32ConvertI32SS) = dispatchInstruction(instruction, ::F32ConvertI32SExecutor)

fun F32ConvertI32UDispatcher(instruction: FusedNumericInstruction.F32ConvertI32UI) = dispatchInstruction(instruction, ::F32ConvertI32UExecutor)

fun F32ConvertI32UDispatcher(instruction: FusedNumericInstruction.F32ConvertI32US) = dispatchInstruction(instruction, ::F32ConvertI32UExecutor)

fun F32ConvertI64SDispatcher(instruction: FusedNumericInstruction.F32ConvertI64SI) = dispatchInstruction(instruction, ::F32ConvertI64SExecutor)

fun F32ConvertI64SDispatcher(instruction: FusedNumericInstruction.F32ConvertI64SS) = dispatchInstruction(instruction, ::F32ConvertI64SExecutor)

fun F32ConvertI64UDispatcher(instruction: FusedNumericInstruction.F32ConvertI64UI) = dispatchInstruction(instruction, ::F32ConvertI64UExecutor)

fun F32ConvertI64UDispatcher(instruction: FusedNumericInstruction.F32ConvertI64US) = dispatchInstruction(instruction, ::F32ConvertI64UExecutor)

fun F32DemoteF64Dispatcher(instruction: FusedNumericInstruction.F32DemoteF64I) = dispatchInstruction(instruction, ::F32DemoteF64Executor)

fun F32DemoteF64Dispatcher(instruction: FusedNumericInstruction.F32DemoteF64S) = dispatchInstruction(instruction, ::F32DemoteF64Executor)

fun F32ReinterpretI32Dispatcher(instruction: FusedNumericInstruction.F32ReinterpretI32I) = dispatchInstruction(instruction, ::F32ReinterpretI32Executor)

fun F32ReinterpretI32Dispatcher(instruction: FusedNumericInstruction.F32ReinterpretI32S) = dispatchInstruction(instruction, ::F32ReinterpretI32Executor)
