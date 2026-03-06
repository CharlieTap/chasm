package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64CopysignExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64DivExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64MaxExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64MinExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64PromoteF32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ReinterpretI64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64GeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64GtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64LeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64LtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64NeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64AbsExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64CeilExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64FloorExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64NearestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64NegExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64SqrtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64TruncExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64AddDispatcher(instruction: FusedNumericInstruction.F64AddIi) = dispatchInstruction(instruction, ::F64AddExecutor)

fun F64AddDispatcher(instruction: FusedNumericInstruction.F64AddIs) = dispatchInstruction(instruction, ::F64AddExecutor)

fun F64AddDispatcher(instruction: FusedNumericInstruction.F64AddSi) = dispatchInstruction(instruction, ::F64AddExecutor)

fun F64AddDispatcher(instruction: FusedNumericInstruction.F64AddSs) = dispatchInstruction(instruction, ::F64AddExecutor)

fun F64SubDispatcher(instruction: FusedNumericInstruction.F64SubIi) = dispatchInstruction(instruction, ::F64SubExecutor)

fun F64SubDispatcher(instruction: FusedNumericInstruction.F64SubIs) = dispatchInstruction(instruction, ::F64SubExecutor)

fun F64SubDispatcher(instruction: FusedNumericInstruction.F64SubSi) = dispatchInstruction(instruction, ::F64SubExecutor)

fun F64SubDispatcher(instruction: FusedNumericInstruction.F64SubSs) = dispatchInstruction(instruction, ::F64SubExecutor)

fun F64MulDispatcher(instruction: FusedNumericInstruction.F64MulIi) = dispatchInstruction(instruction, ::F64MulExecutor)

fun F64MulDispatcher(instruction: FusedNumericInstruction.F64MulIs) = dispatchInstruction(instruction, ::F64MulExecutor)

fun F64MulDispatcher(instruction: FusedNumericInstruction.F64MulSi) = dispatchInstruction(instruction, ::F64MulExecutor)

fun F64MulDispatcher(instruction: FusedNumericInstruction.F64MulSs) = dispatchInstruction(instruction, ::F64MulExecutor)

fun F64DivDispatcher(instruction: FusedNumericInstruction.F64DivIi) = dispatchInstruction(instruction, ::F64DivExecutor)

fun F64DivDispatcher(instruction: FusedNumericInstruction.F64DivIs) = dispatchInstruction(instruction, ::F64DivExecutor)

fun F64DivDispatcher(instruction: FusedNumericInstruction.F64DivSi) = dispatchInstruction(instruction, ::F64DivExecutor)

fun F64DivDispatcher(instruction: FusedNumericInstruction.F64DivSs) = dispatchInstruction(instruction, ::F64DivExecutor)

fun F64MinDispatcher(instruction: FusedNumericInstruction.F64MinIi) = dispatchInstruction(instruction, ::F64MinExecutor)

fun F64MinDispatcher(instruction: FusedNumericInstruction.F64MinIs) = dispatchInstruction(instruction, ::F64MinExecutor)

fun F64MinDispatcher(instruction: FusedNumericInstruction.F64MinSi) = dispatchInstruction(instruction, ::F64MinExecutor)

fun F64MinDispatcher(instruction: FusedNumericInstruction.F64MinSs) = dispatchInstruction(instruction, ::F64MinExecutor)

fun F64MaxDispatcher(instruction: FusedNumericInstruction.F64MaxIi) = dispatchInstruction(instruction, ::F64MaxExecutor)

fun F64MaxDispatcher(instruction: FusedNumericInstruction.F64MaxIs) = dispatchInstruction(instruction, ::F64MaxExecutor)

fun F64MaxDispatcher(instruction: FusedNumericInstruction.F64MaxSi) = dispatchInstruction(instruction, ::F64MaxExecutor)

fun F64MaxDispatcher(instruction: FusedNumericInstruction.F64MaxSs) = dispatchInstruction(instruction, ::F64MaxExecutor)

fun F64CopysignDispatcher(instruction: FusedNumericInstruction.F64CopysignIi) = dispatchInstruction(instruction, ::F64CopysignExecutor)

fun F64CopysignDispatcher(instruction: FusedNumericInstruction.F64CopysignIs) = dispatchInstruction(instruction, ::F64CopysignExecutor)

fun F64CopysignDispatcher(instruction: FusedNumericInstruction.F64CopysignSi) = dispatchInstruction(instruction, ::F64CopysignExecutor)

fun F64CopysignDispatcher(instruction: FusedNumericInstruction.F64CopysignSs) = dispatchInstruction(instruction, ::F64CopysignExecutor)

fun F64AbsDispatcher(instruction: FusedNumericInstruction.F64AbsI) = dispatchInstruction(instruction, ::F64AbsExecutor)

fun F64AbsDispatcher(instruction: FusedNumericInstruction.F64AbsS) = dispatchInstruction(instruction, ::F64AbsExecutor)

fun F64NegDispatcher(instruction: FusedNumericInstruction.F64NegI) = dispatchInstruction(instruction, ::F64NegExecutor)

fun F64NegDispatcher(instruction: FusedNumericInstruction.F64NegS) = dispatchInstruction(instruction, ::F64NegExecutor)

fun F64CeilDispatcher(instruction: FusedNumericInstruction.F64CeilI) = dispatchInstruction(instruction, ::F64CeilExecutor)

fun F64CeilDispatcher(instruction: FusedNumericInstruction.F64CeilS) = dispatchInstruction(instruction, ::F64CeilExecutor)

fun F64FloorDispatcher(instruction: FusedNumericInstruction.F64FloorI) = dispatchInstruction(instruction, ::F64FloorExecutor)

fun F64FloorDispatcher(instruction: FusedNumericInstruction.F64FloorS) = dispatchInstruction(instruction, ::F64FloorExecutor)

fun F64TruncDispatcher(instruction: FusedNumericInstruction.F64TruncI) = dispatchInstruction(instruction, ::F64TruncExecutor)

fun F64TruncDispatcher(instruction: FusedNumericInstruction.F64TruncS) = dispatchInstruction(instruction, ::F64TruncExecutor)

fun F64NearestDispatcher(instruction: FusedNumericInstruction.F64NearestI) = dispatchInstruction(instruction, ::F64NearestExecutor)

fun F64NearestDispatcher(instruction: FusedNumericInstruction.F64NearestS) = dispatchInstruction(instruction, ::F64NearestExecutor)

fun F64SqrtDispatcher(instruction: FusedNumericInstruction.F64SqrtI) = dispatchInstruction(instruction, ::F64SqrtExecutor)

fun F64SqrtDispatcher(instruction: FusedNumericInstruction.F64SqrtS) = dispatchInstruction(instruction, ::F64SqrtExecutor)

fun F64EqDispatcher(instruction: FusedNumericInstruction.F64EqIi) = dispatchInstruction(instruction, ::F64EqExecutor)

fun F64EqDispatcher(instruction: FusedNumericInstruction.F64EqIs) = dispatchInstruction(instruction, ::F64EqExecutor)

fun F64EqDispatcher(instruction: FusedNumericInstruction.F64EqSi) = dispatchInstruction(instruction, ::F64EqExecutor)

fun F64EqDispatcher(instruction: FusedNumericInstruction.F64EqSs) = dispatchInstruction(instruction, ::F64EqExecutor)

fun F64NeDispatcher(instruction: FusedNumericInstruction.F64NeIi) = dispatchInstruction(instruction, ::F64NeExecutor)

fun F64NeDispatcher(instruction: FusedNumericInstruction.F64NeIs) = dispatchInstruction(instruction, ::F64NeExecutor)

fun F64NeDispatcher(instruction: FusedNumericInstruction.F64NeSi) = dispatchInstruction(instruction, ::F64NeExecutor)

fun F64NeDispatcher(instruction: FusedNumericInstruction.F64NeSs) = dispatchInstruction(instruction, ::F64NeExecutor)

fun F64LtDispatcher(instruction: FusedNumericInstruction.F64LtIi) = dispatchInstruction(instruction, ::F64LtExecutor)

fun F64LtDispatcher(instruction: FusedNumericInstruction.F64LtIs) = dispatchInstruction(instruction, ::F64LtExecutor)

fun F64LtDispatcher(instruction: FusedNumericInstruction.F64LtSi) = dispatchInstruction(instruction, ::F64LtExecutor)

fun F64LtDispatcher(instruction: FusedNumericInstruction.F64LtSs) = dispatchInstruction(instruction, ::F64LtExecutor)

fun F64GtDispatcher(instruction: FusedNumericInstruction.F64GtIi) = dispatchInstruction(instruction, ::F64GtExecutor)

fun F64GtDispatcher(instruction: FusedNumericInstruction.F64GtIs) = dispatchInstruction(instruction, ::F64GtExecutor)

fun F64GtDispatcher(instruction: FusedNumericInstruction.F64GtSi) = dispatchInstruction(instruction, ::F64GtExecutor)

fun F64GtDispatcher(instruction: FusedNumericInstruction.F64GtSs) = dispatchInstruction(instruction, ::F64GtExecutor)

fun F64LeDispatcher(instruction: FusedNumericInstruction.F64LeIi) = dispatchInstruction(instruction, ::F64LeExecutor)

fun F64LeDispatcher(instruction: FusedNumericInstruction.F64LeIs) = dispatchInstruction(instruction, ::F64LeExecutor)

fun F64LeDispatcher(instruction: FusedNumericInstruction.F64LeSi) = dispatchInstruction(instruction, ::F64LeExecutor)

fun F64LeDispatcher(instruction: FusedNumericInstruction.F64LeSs) = dispatchInstruction(instruction, ::F64LeExecutor)

fun F64GeDispatcher(instruction: FusedNumericInstruction.F64GeIi) = dispatchInstruction(instruction, ::F64GeExecutor)

fun F64GeDispatcher(instruction: FusedNumericInstruction.F64GeIs) = dispatchInstruction(instruction, ::F64GeExecutor)

fun F64GeDispatcher(instruction: FusedNumericInstruction.F64GeSi) = dispatchInstruction(instruction, ::F64GeExecutor)

fun F64GeDispatcher(instruction: FusedNumericInstruction.F64GeSs) = dispatchInstruction(instruction, ::F64GeExecutor)

fun F64ConvertI32SDispatcher(instruction: FusedNumericInstruction.F64ConvertI32SI) = dispatchInstruction(instruction, ::F64ConvertI32SExecutor)

fun F64ConvertI32SDispatcher(instruction: FusedNumericInstruction.F64ConvertI32SS) = dispatchInstruction(instruction, ::F64ConvertI32SExecutor)

fun F64ConvertI32UDispatcher(instruction: FusedNumericInstruction.F64ConvertI32UI) = dispatchInstruction(instruction, ::F64ConvertI32UExecutor)

fun F64ConvertI32UDispatcher(instruction: FusedNumericInstruction.F64ConvertI32US) = dispatchInstruction(instruction, ::F64ConvertI32UExecutor)

fun F64ConvertI64SDispatcher(instruction: FusedNumericInstruction.F64ConvertI64SI) = dispatchInstruction(instruction, ::F64ConvertI64SExecutor)

fun F64ConvertI64SDispatcher(instruction: FusedNumericInstruction.F64ConvertI64SS) = dispatchInstruction(instruction, ::F64ConvertI64SExecutor)

fun F64ConvertI64UDispatcher(instruction: FusedNumericInstruction.F64ConvertI64UI) = dispatchInstruction(instruction, ::F64ConvertI64UExecutor)

fun F64ConvertI64UDispatcher(instruction: FusedNumericInstruction.F64ConvertI64US) = dispatchInstruction(instruction, ::F64ConvertI64UExecutor)

fun F64PromoteF32Dispatcher(instruction: FusedNumericInstruction.F64PromoteF32I) = dispatchInstruction(instruction, ::F64PromoteF32Executor)

fun F64PromoteF32Dispatcher(instruction: FusedNumericInstruction.F64PromoteF32S) = dispatchInstruction(instruction, ::F64PromoteF32Executor)

fun F64ReinterpretI64Dispatcher(instruction: FusedNumericInstruction.F64ReinterpretI64I) = dispatchInstruction(instruction, ::F64ReinterpretI64Executor)

fun F64ReinterpretI64Dispatcher(instruction: FusedNumericInstruction.F64ReinterpretI64S) = dispatchInstruction(instruction, ::F64ReinterpretI64Executor)
