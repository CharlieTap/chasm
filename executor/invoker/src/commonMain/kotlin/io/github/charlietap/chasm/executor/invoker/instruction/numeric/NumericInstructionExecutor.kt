package io.github.charlietap.chasm.executor.invoker.instruction.numeric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32CopysignExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32DivExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MaxExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MinExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64CopysignExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64DivExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MaxExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MinExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32AddExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32AndExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32MulExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32OrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32SubExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32XorExecutor
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
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F32ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F64ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.I32ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.I64ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32DemoteF64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ReinterpretI32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64PromoteF32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ReinterpretI64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32ReinterpretF32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32WrapI64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ReinterpretF64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32NeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64NeExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32NeExecutor
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
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I32EqzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I64EqzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32AbsExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32CeilExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32FloorExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NearestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NegExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32SqrtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32TruncExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64AbsExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64CeilExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64FloorExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NearestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NegExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64SqrtExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64TruncExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32PopcntExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64PopcntExecutor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun NumericInstructionExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction,
): Result<Unit, InvocationError> =
    NumericInstructionExecutor(
        context = context,
        instruction = instruction,
        i32ConstExecutor = ::I32ConstExecutor,
        i64ConstExecutor = ::I64ConstExecutor,
        f32ConstExecutor = ::F32ConstExecutor,
        f64ConstExecutor = ::F64ConstExecutor,
        i32ClzExecutor = ::I32ClzExecutor,
        i32CtzExecutor = ::I32CtzExecutor,
        i32PopcntExecutor = ::I32PopcntExecutor,
        i64ClzExecutor = ::I64ClzExecutor,
        i64CtzExecutor = ::I64CtzExecutor,
        i64PopcntExecutor = ::I64PopcntExecutor,
        f32AbsExecutor = ::F32AbsExecutor,
        f32NegExecutor = ::F32NegExecutor,
        f32CeilExecutor = ::F32CeilExecutor,
        f32FloorExecutor = ::F32FloorExecutor,
        f32TruncExecutor = ::F32TruncExecutor,
        f32NearestExecutor = ::F32NearestExecutor,
        f32SqrtExecutor = ::F32SqrtExecutor,
        f64AbsExecutor = ::F64AbsExecutor,
        f64NegExecutor = ::F64NegExecutor,
        f64CeilExecutor = ::F64CeilExecutor,
        f64FloorExecutor = ::F64FloorExecutor,
        f64TruncExecutor = ::F64TruncExecutor,
        f64NearestExecutor = ::F64NearestExecutor,
        f64SqrtExecutor = ::F64SqrtExecutor,
        i32AddExecutor = ::I32AddExecutor,
        i32SubExecutor = ::I32SubExecutor,
        i32MulExecutor = ::I32MulExecutor,
        i32DivSExecutor = ::I32DivSExecutor,
        i32DivUExecutor = ::I32DivUExecutor,
        i32RemSExecutor = ::I32RemSExecutor,
        i32RemUExecutor = ::I32RemUExecutor,
        i32AndExecutor = ::I32AndExecutor,
        i32OrExecutor = ::I32OrExecutor,
        i32XorExecutor = ::I32XorExecutor,
        i32ShlExecutor = ::I32ShlExecutor,
        i32ShrSExecutor = ::I32ShrSExecutor,
        i32ShrUExecutor = ::I32ShrUExecutor,
        i32RotlExecutor = ::I32RotlExecutor,
        i32RotrExecutor = ::I32RotrExecutor,
        i64AddExecutor = ::I64AddExecutor,
        i64SubExecutor = ::I64SubExecutor,
        i64MulExecutor = ::I64MulExecutor,
        i64DivSExecutor = ::I64DivSExecutor,
        i64DivUExecutor = ::I64DivUExecutor,
        i64RemSExecutor = ::I64RemSExecutor,
        i64RemUExecutor = ::I64RemUExecutor,
        i64AndExecutor = ::I64AndExecutor,
        i64OrExecutor = ::I64OrExecutor,
        i64XorExecutor = ::I64XorExecutor,
        i64ShlExecutor = ::I64ShlExecutor,
        i64ShrSExecutor = ::I64ShrSExecutor,
        i64ShrUExecutor = ::I64ShrUExecutor,
        i64RotlExecutor = ::I64RotlExecutor,
        i64RotrExecutor = ::I64RotrExecutor,
        f32AddExecutor = ::F32AddExecutor,
        f32SubExecutor = ::F32SubExecutor,
        f32MulExecutor = ::F32MulExecutor,
        f32DivExecutor = ::F32DivExecutor,
        f32MinExecutor = ::F32MinExecutor,
        f32MaxExecutor = ::F32MaxExecutor,
        f32CopysignExecutor = ::F32CopysignExecutor,
        f64AddExecutor = ::F64AddExecutor,
        f64SubExecutor = ::F64SubExecutor,
        f64MulExecutor = ::F64MulExecutor,
        f64DivExecutor = ::F64DivExecutor,
        f64MinExecutor = ::F64MinExecutor,
        f64MaxExecutor = ::F64MaxExecutor,
        f64CopysignExecutor = ::F64CopysignExecutor,
        i32EqzExecutor = ::I32EqzExecutor,
        i64EqzExecutor = ::I64EqzExecutor,
        i32EqExecutor = ::I32EqExecutor,
        i32NeExecutor = ::I32NeExecutor,
        i32LtSExecutor = ::I32LtSExecutor,
        i32LtUExecutor = ::I32LtUExecutor,
        i32LeSExecutor = ::I32LeSExecutor,
        i32LeUExecutor = ::I32LeUExecutor,
        i32GtSExecutor = ::I32GtSExecutor,
        i32GtUExecutor = ::I32GtUExecutor,
        i32GeSExecutor = ::I32GeSExecutor,
        i32GeUExecutor = ::I32GeUExecutor,
        i64EqExecutor = ::I64EqExecutor,
        i64NeExecutor = ::I64NeExecutor,
        i64LtSExecutor = ::I64LtSExecutor,
        i64LtUExecutor = ::I64LtUExecutor,
        i64LeSExecutor = ::I64LeSExecutor,
        i64LeUExecutor = ::I64LeUExecutor,
        i64GtSExecutor = ::I64GtSExecutor,
        i64GtUExecutor = ::I64GtUExecutor,
        i64GeSExecutor = ::I64GeSExecutor,
        i64GeUExecutor = ::I64GeUExecutor,
        f32EqExecutor = ::F32EqExecutor,
        f32NeExecutor = ::F32NeExecutor,
        f32LtExecutor = ::F32LtExecutor,
        f32LeExecutor = ::F32LeExecutor,
        f32GtExecutor = ::F32GtExecutor,
        f32GeExecutor = ::F32GeExecutor,
        f64EqExecutor = ::F64EqExecutor,
        f64NeExecutor = ::F64NeExecutor,
        f64LtExecutor = ::F64LtExecutor,
        f64LeExecutor = ::F64LeExecutor,
        f64GtExecutor = ::F64GtExecutor,
        f64GeExecutor = ::F64GeExecutor,
        i32Extend8SExecutor = ::I32Extend8SExecutor,
        i32Extend16SExecutor = ::I32Extend16SExecutor,
        i32WrapI64Executor = ::I32WrapI64Executor,
        i32TruncF32SExecutor = ::I32TruncF32SExecutor,
        i32TruncF32UExecutor = ::I32TruncF32UExecutor,
        i32TruncF64SExecutor = ::I32TruncF64SExecutor,
        i32TruncF64UExecutor = ::I32TruncF64UExecutor,
        i32TruncSatF32SExecutor = ::I32TruncSatF32SExecutor,
        i32TruncSatF32UExecutor = ::I32TruncSatF32UExecutor,
        i32TruncSatF64SExecutor = ::I32TruncSatF64SExecutor,
        i32TruncSatF64UExecutor = ::I32TruncSatF64UExecutor,
        i64Extend8SExecutor = ::I64Extend8SExecutor,
        i64Extend16SExecutor = ::I64Extend16SExecutor,
        i64Extend32SExecutor = ::I64Extend32SExecutor,
        i64ExtendI32SExecutor = ::I64ExtendI32SExecutor,
        i64ExtendI32UExecutor = ::I64ExtendI32UExecutor,
        i64TruncF32SExecutor = ::I64TruncF32SExecutor,
        i64TruncF32UExecutor = ::I64TruncF32UExecutor,
        i64TruncF64SExecutor = ::I64TruncF64SExecutor,
        i64TruncF64UExecutor = ::I64TruncF64UExecutor,
        i64TruncSatF32SExecutor = ::I64TruncSatF32SExecutor,
        i64TruncSatF32UExecutor = ::I64TruncSatF32UExecutor,
        i64TruncSatF64SExecutor = ::I64TruncSatF64SExecutor,
        i64TruncSatF64UExecutor = ::I64TruncSatF64UExecutor,
        f32ConvertI32SExecutor = ::F32ConvertI32SExecutor,
        f32ConvertI32UExecutor = ::F32ConvertI32UExecutor,
        f32ConvertI64SExecutor = ::F32ConvertI64SExecutor,
        f32ConvertI64UExecutor = ::F32ConvertI64UExecutor,
        f32DemoteF64Executor = ::F32DemoteF64Executor,
        f64ConvertI32SExecutor = ::F64ConvertI32SExecutor,
        f64ConvertI32UExecutor = ::F64ConvertI32UExecutor,
        f64ConvertI64SExecutor = ::F64ConvertI64SExecutor,
        f64ConvertI64UExecutor = ::F64ConvertI64UExecutor,
        f64PromoteF32Executor = ::F64PromoteF32Executor,
        f32ReinterpretI32Executor = ::F32ReinterpretI32Executor,
        f64ReinterpretI64Executor = ::F64ReinterpretI64Executor,
        i32ReinterpretF32Executor = ::I32ReinterpretF32Executor,
        i64ReinterpretF64Executor = ::I64ReinterpretF64Executor,
    )

internal inline fun NumericInstructionExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction,
    crossinline i32ConstExecutor: Executor<NumericInstruction.I32Const>,
    crossinline i64ConstExecutor: Executor<NumericInstruction.I64Const>,
    crossinline f32ConstExecutor: Executor<NumericInstruction.F32Const>,
    crossinline f64ConstExecutor: Executor<NumericInstruction.F64Const>,
    crossinline i32ClzExecutor: Executor<NumericInstruction.I32Clz>,
    crossinline i32CtzExecutor: Executor<NumericInstruction.I32Ctz>,
    crossinline i32PopcntExecutor: Executor<NumericInstruction.I32Popcnt>,
    crossinline i64ClzExecutor: Executor<NumericInstruction.I64Clz>,
    crossinline i64CtzExecutor: Executor<NumericInstruction.I64Ctz>,
    crossinline i64PopcntExecutor: Executor<NumericInstruction.I64Popcnt>,
    crossinline f32AbsExecutor: Executor<NumericInstruction.F32Abs>,
    crossinline f32NegExecutor: Executor<NumericInstruction.F32Neg>,
    crossinline f32CeilExecutor: Executor<NumericInstruction.F32Ceil>,
    crossinline f32FloorExecutor: Executor<NumericInstruction.F32Floor>,
    crossinline f32TruncExecutor: Executor<NumericInstruction.F32Trunc>,
    crossinline f32NearestExecutor: Executor<NumericInstruction.F32Nearest>,
    crossinline f32SqrtExecutor: Executor<NumericInstruction.F32Sqrt>,
    crossinline f64AbsExecutor: Executor<NumericInstruction.F64Abs>,
    crossinline f64NegExecutor: Executor<NumericInstruction.F64Neg>,
    crossinline f64CeilExecutor: Executor<NumericInstruction.F64Ceil>,
    crossinline f64FloorExecutor: Executor<NumericInstruction.F64Floor>,
    crossinline f64TruncExecutor: Executor<NumericInstruction.F64Trunc>,
    crossinline f64NearestExecutor: Executor<NumericInstruction.F64Nearest>,
    crossinline f64SqrtExecutor: Executor<NumericInstruction.F64Sqrt>,
    crossinline i32AddExecutor: Executor<NumericInstruction.I32Add>,
    crossinline i32SubExecutor: Executor<NumericInstruction.I32Sub>,
    crossinline i32MulExecutor: Executor<NumericInstruction.I32Mul>,
    crossinline i32DivSExecutor: Executor<NumericInstruction.I32DivS>,
    crossinline i32DivUExecutor: Executor<NumericInstruction.I32DivU>,
    crossinline i32RemSExecutor: Executor<NumericInstruction.I32RemS>,
    crossinline i32RemUExecutor: Executor<NumericInstruction.I32RemU>,
    crossinline i32AndExecutor: Executor<NumericInstruction.I32And>,
    crossinline i32OrExecutor: Executor<NumericInstruction.I32Or>,
    crossinline i32XorExecutor: Executor<NumericInstruction.I32Xor>,
    crossinline i32ShlExecutor: Executor<NumericInstruction.I32Shl>,
    crossinline i32ShrSExecutor: Executor<NumericInstruction.I32ShrS>,
    crossinline i32ShrUExecutor: Executor<NumericInstruction.I32ShrU>,
    crossinline i32RotlExecutor: Executor<NumericInstruction.I32Rotl>,
    crossinline i32RotrExecutor: Executor<NumericInstruction.I32Rotr>,
    crossinline i64AddExecutor: Executor<NumericInstruction.I64Add>,
    crossinline i64SubExecutor: Executor<NumericInstruction.I64Sub>,
    crossinline i64MulExecutor: Executor<NumericInstruction.I64Mul>,
    crossinline i64DivSExecutor: Executor<NumericInstruction.I64DivS>,
    crossinline i64DivUExecutor: Executor<NumericInstruction.I64DivU>,
    crossinline i64RemSExecutor: Executor<NumericInstruction.I64RemS>,
    crossinline i64RemUExecutor: Executor<NumericInstruction.I64RemU>,
    crossinline i64AndExecutor: Executor<NumericInstruction.I64And>,
    crossinline i64OrExecutor: Executor<NumericInstruction.I64Or>,
    crossinline i64XorExecutor: Executor<NumericInstruction.I64Xor>,
    crossinline i64ShlExecutor: Executor<NumericInstruction.I64Shl>,
    crossinline i64ShrSExecutor: Executor<NumericInstruction.I64ShrS>,
    crossinline i64ShrUExecutor: Executor<NumericInstruction.I64ShrU>,
    crossinline i64RotlExecutor: Executor<NumericInstruction.I64Rotl>,
    crossinline i64RotrExecutor: Executor<NumericInstruction.I64Rotr>,
    crossinline f32AddExecutor: Executor<NumericInstruction.F32Add>,
    crossinline f32SubExecutor: Executor<NumericInstruction.F32Sub>,
    crossinline f32MulExecutor: Executor<NumericInstruction.F32Mul>,
    crossinline f32DivExecutor: Executor<NumericInstruction.F32Div>,
    crossinline f32MinExecutor: Executor<NumericInstruction.F32Min>,
    crossinline f32MaxExecutor: Executor<NumericInstruction.F32Max>,
    crossinline f32CopysignExecutor: Executor<NumericInstruction.F32Copysign>,
    crossinline f64AddExecutor: Executor<NumericInstruction.F64Add>,
    crossinline f64SubExecutor: Executor<NumericInstruction.F64Sub>,
    crossinline f64MulExecutor: Executor<NumericInstruction.F64Mul>,
    crossinline f64DivExecutor: Executor<NumericInstruction.F64Div>,
    crossinline f64MinExecutor: Executor<NumericInstruction.F64Min>,
    crossinline f64MaxExecutor: Executor<NumericInstruction.F64Max>,
    crossinline f64CopysignExecutor: Executor<NumericInstruction.F64Copysign>,
    crossinline i32EqzExecutor: Executor<NumericInstruction.I32Eqz>,
    crossinline i64EqzExecutor: Executor<NumericInstruction.I64Eqz>,
    crossinline i32EqExecutor: Executor<NumericInstruction.I32Eq>,
    crossinline i32NeExecutor: Executor<NumericInstruction.I32Ne>,
    crossinline i32LtSExecutor: Executor<NumericInstruction.I32LtS>,
    crossinline i32LtUExecutor: Executor<NumericInstruction.I32LtU>,
    crossinline i32LeSExecutor: Executor<NumericInstruction.I32LeS>,
    crossinline i32LeUExecutor: Executor<NumericInstruction.I32LeU>,
    crossinline i32GtSExecutor: Executor<NumericInstruction.I32GtS>,
    crossinline i32GtUExecutor: Executor<NumericInstruction.I32GtU>,
    crossinline i32GeSExecutor: Executor<NumericInstruction.I32GeS>,
    crossinline i32GeUExecutor: Executor<NumericInstruction.I32GeU>,
    crossinline i64EqExecutor: Executor<NumericInstruction.I64Eq>,
    crossinline i64NeExecutor: Executor<NumericInstruction.I64Ne>,
    crossinline i64LtSExecutor: Executor<NumericInstruction.I64LtS>,
    crossinline i64LtUExecutor: Executor<NumericInstruction.I64LtU>,
    crossinline i64LeSExecutor: Executor<NumericInstruction.I64LeS>,
    crossinline i64LeUExecutor: Executor<NumericInstruction.I64LeU>,
    crossinline i64GtSExecutor: Executor<NumericInstruction.I64GtS>,
    crossinline i64GtUExecutor: Executor<NumericInstruction.I64GtU>,
    crossinline i64GeSExecutor: Executor<NumericInstruction.I64GeS>,
    crossinline i64GeUExecutor: Executor<NumericInstruction.I64GeU>,
    crossinline f32EqExecutor: Executor<NumericInstruction.F32Eq>,
    crossinline f32NeExecutor: Executor<NumericInstruction.F32Ne>,
    crossinline f32LtExecutor: Executor<NumericInstruction.F32Lt>,
    crossinline f32LeExecutor: Executor<NumericInstruction.F32Le>,
    crossinline f32GtExecutor: Executor<NumericInstruction.F32Gt>,
    crossinline f32GeExecutor: Executor<NumericInstruction.F32Ge>,
    crossinline f64EqExecutor: Executor<NumericInstruction.F64Eq>,
    crossinline f64NeExecutor: Executor<NumericInstruction.F64Ne>,
    crossinline f64LtExecutor: Executor<NumericInstruction.F64Lt>,
    crossinline f64LeExecutor: Executor<NumericInstruction.F64Le>,
    crossinline f64GtExecutor: Executor<NumericInstruction.F64Gt>,
    crossinline f64GeExecutor: Executor<NumericInstruction.F64Ge>,
    crossinline i32Extend8SExecutor: Executor<NumericInstruction.I32Extend8S>,
    crossinline i32Extend16SExecutor: Executor<NumericInstruction.I32Extend16S>,
    crossinline i32WrapI64Executor: Executor<NumericInstruction.I32WrapI64>,
    crossinline i32TruncF32SExecutor: Executor<NumericInstruction.I32TruncF32S>,
    crossinline i32TruncF32UExecutor: Executor<NumericInstruction.I32TruncF32U>,
    crossinline i32TruncF64SExecutor: Executor<NumericInstruction.I32TruncF64S>,
    crossinline i32TruncF64UExecutor: Executor<NumericInstruction.I32TruncF64U>,
    crossinline i32TruncSatF32SExecutor: Executor<NumericInstruction.I32TruncSatF32S>,
    crossinline i32TruncSatF32UExecutor: Executor<NumericInstruction.I32TruncSatF32U>,
    crossinline i32TruncSatF64SExecutor: Executor<NumericInstruction.I32TruncSatF64S>,
    crossinline i32TruncSatF64UExecutor: Executor<NumericInstruction.I32TruncSatF64U>,
    crossinline i64Extend8SExecutor: Executor<NumericInstruction.I64Extend8S>,
    crossinline i64Extend16SExecutor: Executor<NumericInstruction.I64Extend16S>,
    crossinline i64Extend32SExecutor: Executor<NumericInstruction.I64Extend32S>,
    crossinline i64ExtendI32SExecutor: Executor<NumericInstruction.I64ExtendI32S>,
    crossinline i64ExtendI32UExecutor: Executor<NumericInstruction.I64ExtendI32U>,
    crossinline i64TruncF32SExecutor: Executor<NumericInstruction.I64TruncF32S>,
    crossinline i64TruncF32UExecutor: Executor<NumericInstruction.I64TruncF32U>,
    crossinline i64TruncF64SExecutor: Executor<NumericInstruction.I64TruncF64S>,
    crossinline i64TruncF64UExecutor: Executor<NumericInstruction.I64TruncF64U>,
    crossinline i64TruncSatF32SExecutor: Executor<NumericInstruction.I64TruncSatF32S>,
    crossinline i64TruncSatF32UExecutor: Executor<NumericInstruction.I64TruncSatF32U>,
    crossinline i64TruncSatF64SExecutor: Executor<NumericInstruction.I64TruncSatF64S>,
    crossinline i64TruncSatF64UExecutor: Executor<NumericInstruction.I64TruncSatF64U>,
    crossinline f32ConvertI32SExecutor: Executor<NumericInstruction.F32ConvertI32S>,
    crossinline f32ConvertI32UExecutor: Executor<NumericInstruction.F32ConvertI32U>,
    crossinline f32ConvertI64SExecutor: Executor<NumericInstruction.F32ConvertI64S>,
    crossinline f32ConvertI64UExecutor: Executor<NumericInstruction.F32ConvertI64U>,
    crossinline f32DemoteF64Executor: Executor<NumericInstruction.F32DemoteF64>,
    crossinline f64ConvertI32SExecutor: Executor<NumericInstruction.F64ConvertI32S>,
    crossinline f64ConvertI32UExecutor: Executor<NumericInstruction.F64ConvertI32U>,
    crossinline f64ConvertI64SExecutor: Executor<NumericInstruction.F64ConvertI64S>,
    crossinline f64ConvertI64UExecutor: Executor<NumericInstruction.F64ConvertI64U>,
    crossinline f64PromoteF32Executor: Executor<NumericInstruction.F64PromoteF32>,
    crossinline f32ReinterpretI32Executor: Executor<NumericInstruction.F32ReinterpretI32>,
    crossinline f64ReinterpretI64Executor: Executor<NumericInstruction.F64ReinterpretI64>,
    crossinline i32ReinterpretF32Executor: Executor<NumericInstruction.I32ReinterpretF32>,
    crossinline i64ReinterpretF64Executor: Executor<NumericInstruction.I64ReinterpretF64>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is NumericInstruction.I32Const -> i32ConstExecutor(context, instruction).bind()
        is NumericInstruction.I64Const -> i64ConstExecutor(context, instruction).bind()
        is NumericInstruction.F32Const -> f32ConstExecutor(context, instruction).bind()
        is NumericInstruction.F64Const -> f64ConstExecutor(context, instruction).bind()

        is NumericInstruction.I32Clz -> i32ClzExecutor(context, instruction).bind()
        is NumericInstruction.I32Ctz -> i32CtzExecutor(context, instruction).bind()
        is NumericInstruction.I32Popcnt -> i32PopcntExecutor(context, instruction).bind()
        is NumericInstruction.I64Clz -> i64ClzExecutor(context, instruction).bind()
        is NumericInstruction.I64Ctz -> i64CtzExecutor(context, instruction).bind()
        is NumericInstruction.I64Popcnt -> i64PopcntExecutor(context, instruction).bind()

        is NumericInstruction.F32Abs -> f32AbsExecutor(context, instruction).bind()
        is NumericInstruction.F32Neg -> f32NegExecutor(context, instruction).bind()
        is NumericInstruction.F32Ceil -> f32CeilExecutor(context, instruction).bind()
        is NumericInstruction.F32Floor -> f32FloorExecutor(context, instruction).bind()
        is NumericInstruction.F32Trunc -> f32TruncExecutor(context, instruction).bind()
        is NumericInstruction.F32Nearest -> f32NearestExecutor(context, instruction).bind()
        is NumericInstruction.F32Sqrt -> f32SqrtExecutor(context, instruction).bind()
        is NumericInstruction.F64Abs -> f64AbsExecutor(context, instruction).bind()
        is NumericInstruction.F64Neg -> f64NegExecutor(context, instruction).bind()
        is NumericInstruction.F64Ceil -> f64CeilExecutor(context, instruction).bind()
        is NumericInstruction.F64Floor -> f64FloorExecutor(context, instruction).bind()
        is NumericInstruction.F64Trunc -> f64TruncExecutor(context, instruction).bind()
        is NumericInstruction.F64Nearest -> f64NearestExecutor(context, instruction).bind()
        is NumericInstruction.F64Sqrt -> f64SqrtExecutor(context, instruction).bind()

        is NumericInstruction.I32Add -> i32AddExecutor(context, instruction).bind()
        is NumericInstruction.I32Sub -> i32SubExecutor(context, instruction).bind()
        is NumericInstruction.I32Mul -> i32MulExecutor(context, instruction).bind()
        is NumericInstruction.I32DivS -> i32DivSExecutor(context, instruction).bind()
        is NumericInstruction.I32DivU -> i32DivUExecutor(context, instruction).bind()
        is NumericInstruction.I32RemS -> i32RemSExecutor(context, instruction).bind()
        is NumericInstruction.I32RemU -> i32RemUExecutor(context, instruction).bind()
        is NumericInstruction.I32And -> i32AndExecutor(context, instruction).bind()
        is NumericInstruction.I32Or -> i32OrExecutor(context, instruction).bind()
        is NumericInstruction.I32Xor -> i32XorExecutor(context, instruction).bind()
        is NumericInstruction.I32Shl -> i32ShlExecutor(context, instruction).bind()
        is NumericInstruction.I32ShrS -> i32ShrSExecutor(context, instruction).bind()
        is NumericInstruction.I32ShrU -> i32ShrUExecutor(context, instruction).bind()
        is NumericInstruction.I32Rotl -> i32RotlExecutor(context, instruction).bind()
        is NumericInstruction.I32Rotr -> i32RotrExecutor(context, instruction).bind()

        is NumericInstruction.I64Add -> i64AddExecutor(context, instruction).bind()
        is NumericInstruction.I64Sub -> i64SubExecutor(context, instruction).bind()
        is NumericInstruction.I64Mul -> i64MulExecutor(context, instruction).bind()
        is NumericInstruction.I64DivS -> i64DivSExecutor(context, instruction).bind()
        is NumericInstruction.I64DivU -> i64DivUExecutor(context, instruction).bind()
        is NumericInstruction.I64RemS -> i64RemSExecutor(context, instruction).bind()
        is NumericInstruction.I64RemU -> i64RemUExecutor(context, instruction).bind()
        is NumericInstruction.I64And -> i64AndExecutor(context, instruction).bind()
        is NumericInstruction.I64Or -> i64OrExecutor(context, instruction).bind()
        is NumericInstruction.I64Xor -> i64XorExecutor(context, instruction).bind()
        is NumericInstruction.I64Shl -> i64ShlExecutor(context, instruction).bind()
        is NumericInstruction.I64ShrS -> i64ShrSExecutor(context, instruction).bind()
        is NumericInstruction.I64ShrU -> i64ShrUExecutor(context, instruction).bind()
        is NumericInstruction.I64Rotl -> i64RotlExecutor(context, instruction).bind()
        is NumericInstruction.I64Rotr -> i64RotrExecutor(context, instruction).bind()

        is NumericInstruction.F32Add -> f32AddExecutor(context, instruction).bind()
        is NumericInstruction.F32Sub -> f32SubExecutor(context, instruction).bind()
        is NumericInstruction.F32Mul -> f32MulExecutor(context, instruction).bind()
        is NumericInstruction.F32Div -> f32DivExecutor(context, instruction).bind()
        is NumericInstruction.F32Min -> f32MinExecutor(context, instruction).bind()
        is NumericInstruction.F32Max -> f32MaxExecutor(context, instruction).bind()
        is NumericInstruction.F32Copysign -> f32CopysignExecutor(context, instruction).bind()
        is NumericInstruction.F64Add -> f64AddExecutor(context, instruction).bind()
        is NumericInstruction.F64Sub -> f64SubExecutor(context, instruction).bind()
        is NumericInstruction.F64Mul -> f64MulExecutor(context, instruction).bind()
        is NumericInstruction.F64Div -> f64DivExecutor(context, instruction).bind()
        is NumericInstruction.F64Min -> f64MinExecutor(context, instruction).bind()
        is NumericInstruction.F64Max -> f64MaxExecutor(context, instruction).bind()
        is NumericInstruction.F64Copysign -> f64CopysignExecutor(context, instruction).bind()

        is NumericInstruction.I32Eqz -> i32EqzExecutor(context, instruction).bind()
        is NumericInstruction.I64Eqz -> i64EqzExecutor(context, instruction).bind()

        is NumericInstruction.I32Eq -> i32EqExecutor(context, instruction).bind()
        is NumericInstruction.I32Ne -> i32NeExecutor(context, instruction).bind()
        is NumericInstruction.I32LtS -> i32LtSExecutor(context, instruction).bind()
        is NumericInstruction.I32LtU -> i32LtUExecutor(context, instruction).bind()
        is NumericInstruction.I32LeS -> i32LeSExecutor(context, instruction).bind()
        is NumericInstruction.I32LeU -> i32LeUExecutor(context, instruction).bind()
        is NumericInstruction.I32GtS -> i32GtSExecutor(context, instruction).bind()
        is NumericInstruction.I32GtU -> i32GtUExecutor(context, instruction).bind()
        is NumericInstruction.I32GeS -> i32GeSExecutor(context, instruction).bind()
        is NumericInstruction.I32GeU -> i32GeUExecutor(context, instruction).bind()

        is NumericInstruction.I64Eq -> i64EqExecutor(context, instruction).bind()
        is NumericInstruction.I64Ne -> i64NeExecutor(context, instruction).bind()
        is NumericInstruction.I64LtS -> i64LtSExecutor(context, instruction).bind()
        is NumericInstruction.I64LtU -> i64LtUExecutor(context, instruction).bind()
        is NumericInstruction.I64LeS -> i64LeSExecutor(context, instruction).bind()
        is NumericInstruction.I64LeU -> i64LeUExecutor(context, instruction).bind()
        is NumericInstruction.I64GtS -> i64GtSExecutor(context, instruction).bind()
        is NumericInstruction.I64GtU -> i64GtUExecutor(context, instruction).bind()
        is NumericInstruction.I64GeS -> i64GeSExecutor(context, instruction).bind()
        is NumericInstruction.I64GeU -> i64GeUExecutor(context, instruction).bind()

        is NumericInstruction.F32Eq -> f32EqExecutor(context, instruction).bind()
        is NumericInstruction.F32Ne -> f32NeExecutor(context, instruction).bind()
        is NumericInstruction.F32Lt -> f32LtExecutor(context, instruction).bind()
        is NumericInstruction.F32Le -> f32LeExecutor(context, instruction).bind()
        is NumericInstruction.F32Gt -> f32GtExecutor(context, instruction).bind()
        is NumericInstruction.F32Ge -> f32GeExecutor(context, instruction).bind()
        is NumericInstruction.F64Eq -> f64EqExecutor(context, instruction).bind()
        is NumericInstruction.F64Ne -> f64NeExecutor(context, instruction).bind()
        is NumericInstruction.F64Lt -> f64LtExecutor(context, instruction).bind()
        is NumericInstruction.F64Le -> f64LeExecutor(context, instruction).bind()
        is NumericInstruction.F64Gt -> f64GtExecutor(context, instruction).bind()
        is NumericInstruction.F64Ge -> f64GeExecutor(context, instruction).bind()

        is NumericInstruction.I32Extend8S -> i32Extend8SExecutor(context, instruction).bind()
        is NumericInstruction.I32Extend16S -> i32Extend16SExecutor(context, instruction).bind()
        is NumericInstruction.I32WrapI64 -> i32WrapI64Executor(context, instruction).bind()
        is NumericInstruction.I32TruncF32S -> i32TruncF32SExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncF32U -> i32TruncF32UExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncF64S -> i32TruncF64SExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncF64U -> i32TruncF64UExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncSatF32S -> i32TruncSatF32SExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncSatF32U -> i32TruncSatF32UExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncSatF64S -> i32TruncSatF64SExecutor(context, instruction).bind()
        is NumericInstruction.I32TruncSatF64U -> i32TruncSatF64UExecutor(context, instruction).bind()

        is NumericInstruction.I64Extend8S -> i64Extend8SExecutor(context, instruction).bind()
        is NumericInstruction.I64Extend16S -> i64Extend16SExecutor(context, instruction).bind()
        is NumericInstruction.I64Extend32S -> i64Extend32SExecutor(context, instruction).bind()
        is NumericInstruction.I64ExtendI32S -> i64ExtendI32SExecutor(context, instruction).bind()
        is NumericInstruction.I64ExtendI32U -> i64ExtendI32UExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncF32S -> i64TruncF32SExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncF32U -> i64TruncF32UExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncF64S -> i64TruncF64SExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncF64U -> i64TruncF64UExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncSatF32S -> i64TruncSatF32SExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncSatF32U -> i64TruncSatF32UExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncSatF64S -> i64TruncSatF64SExecutor(context, instruction).bind()
        is NumericInstruction.I64TruncSatF64U -> i64TruncSatF64UExecutor(context, instruction).bind()

        is NumericInstruction.F32ConvertI32S -> f32ConvertI32SExecutor(context, instruction).bind()
        is NumericInstruction.F32ConvertI32U -> f32ConvertI32UExecutor(context, instruction).bind()
        is NumericInstruction.F32ConvertI64S -> f32ConvertI64SExecutor(context, instruction).bind()
        is NumericInstruction.F32ConvertI64U -> f32ConvertI64UExecutor(context, instruction).bind()
        is NumericInstruction.F32DemoteF64 -> f32DemoteF64Executor(context, instruction).bind()
        is NumericInstruction.F64ConvertI32S -> f64ConvertI32SExecutor(context, instruction).bind()
        is NumericInstruction.F64ConvertI32U -> f64ConvertI32UExecutor(context, instruction).bind()
        is NumericInstruction.F64ConvertI64S -> f64ConvertI64SExecutor(context, instruction).bind()
        is NumericInstruction.F64ConvertI64U -> f64ConvertI64UExecutor(context, instruction).bind()
        is NumericInstruction.F64PromoteF32 -> f64PromoteF32Executor(context, instruction).bind()

        is NumericInstruction.F32ReinterpretI32 -> f32ReinterpretI32Executor(context, instruction).bind()
        is NumericInstruction.F64ReinterpretI64 -> f64ReinterpretI64Executor(context, instruction).bind()
        is NumericInstruction.I32ReinterpretF32 -> i32ReinterpretF32Executor(context, instruction).bind()
        is NumericInstruction.I64ReinterpretF64 -> i64ReinterpretF64Executor(context, instruction).bind()
    }
}
