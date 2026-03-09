package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32CeilDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConvertI32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConvertI32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConvertI64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConvertI64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32CopysignDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32DemoteF64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32DivDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32FloorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32GeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32GtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32LeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32LtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32MaxDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32MinDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32NearestDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32NegDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ReinterpretI32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32SqrtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32TruncDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64CeilDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConvertI32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConvertI32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConvertI64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConvertI64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64CopysignDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64DivDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64FloorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64GeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64GtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64LeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64LtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64MaxDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64MinDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64NearestDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64NegDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64PromoteF32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ReinterpretI64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64SqrtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64TruncDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AndDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ClzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32CtzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32Extend16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32Extend8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32GeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32GeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32GtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32GtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32LeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32LeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32LtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32LtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32OrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32PopcntDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ReinterpretF32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32RemSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32RemUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32RotlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32RotrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShrSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShrUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncSatF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncSatF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncSatF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32TruncSatF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32WrapI64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32XorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64Add128Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64AndDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ClzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64CtzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64Extend16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64Extend32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64Extend8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ExtendI32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ExtendI32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64GeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64GeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64GtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64GtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64LeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64LeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64LtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64LtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64MulWideSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64MulWideUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64OrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64PopcntDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ReinterpretF64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64RemSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64RemUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64RotlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64RotrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ShlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ShrSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ShrUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64Sub128Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncSatF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncSatF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncSatF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64TruncSatF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64XorDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.math.truncate
import kotlin.math.withSign
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction as RuntimeFusedNumericInstruction

internal fun FusedNumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedNumericInstruction.I32Const,
        is FusedNumericInstruction.I32Add,
        is FusedNumericInstruction.I32Eqz,
        is FusedNumericInstruction.I32And,
        is FusedNumericInstruction.I32Or,
        is FusedNumericInstruction.I32Xor,
        is FusedNumericInstruction.I32Sub,
        is FusedNumericInstruction.I32Mul,
        is FusedNumericInstruction.I32DivS,
        is FusedNumericInstruction.I32DivU,
        is FusedNumericInstruction.I32Shl,
        is FusedNumericInstruction.I32ShrS,
        is FusedNumericInstruction.I32ShrU,
        is FusedNumericInstruction.I32Eq,
        is FusedNumericInstruction.I32Ne,
        is FusedNumericInstruction.I32LtS,
        is FusedNumericInstruction.I32LtU,
        is FusedNumericInstruction.I32GtS,
        is FusedNumericInstruction.I32GtU,
        is FusedNumericInstruction.I32LeS,
        is FusedNumericInstruction.I32LeU,
        is FusedNumericInstruction.I32GeS,
        is FusedNumericInstruction.I32GeU,
        is FusedNumericInstruction.I32Clz,
        is FusedNumericInstruction.I32Ctz,
        is FusedNumericInstruction.I32Extend16S,
        is FusedNumericInstruction.I32Extend8S,
        is FusedNumericInstruction.I32RemS,
        is FusedNumericInstruction.I32RemU,
        is FusedNumericInstruction.I32Rotl,
        is FusedNumericInstruction.I32Rotr,
        is FusedNumericInstruction.I32TruncF32S,
        is FusedNumericInstruction.I32TruncF32U,
        is FusedNumericInstruction.I32TruncF64S,
        is FusedNumericInstruction.I32TruncF64U,
        is FusedNumericInstruction.I32TruncSatF32S,
        is FusedNumericInstruction.I32TruncSatF32U,
        is FusedNumericInstruction.I32TruncSatF64S,
        is FusedNumericInstruction.I32TruncSatF64U,
        is FusedNumericInstruction.I32WrapI64,
        is FusedNumericInstruction.I32Popcnt,
        is FusedNumericInstruction.I32ReinterpretF32,
        -> i32NumericInstructionPredecoder(context, instruction)

        is FusedNumericInstruction.I64Const,
        is FusedNumericInstruction.I64Eqz,
        is FusedNumericInstruction.I64Add128,
        is FusedNumericInstruction.I64Sub128,
        is FusedNumericInstruction.I64MulWideS,
        is FusedNumericInstruction.I64MulWideU,
        is FusedNumericInstruction.I64Add,
        is FusedNumericInstruction.I64Sub,
        is FusedNumericInstruction.I64Mul,
        is FusedNumericInstruction.I64DivS,
        is FusedNumericInstruction.I64DivU,
        is FusedNumericInstruction.I64And,
        is FusedNumericInstruction.I64Clz,
        is FusedNumericInstruction.I64Ctz,
        is FusedNumericInstruction.I64Eq,
        is FusedNumericInstruction.I64Extend16S,
        is FusedNumericInstruction.I64Extend32S,
        is FusedNumericInstruction.I64Extend8S,
        is FusedNumericInstruction.I64ExtendI32S,
        is FusedNumericInstruction.I64ExtendI32U,
        is FusedNumericInstruction.I64Ne,
        is FusedNumericInstruction.I64Or,
        is FusedNumericInstruction.I64Popcnt,
        is FusedNumericInstruction.I64ReinterpretF64,
        is FusedNumericInstruction.I64RemS,
        is FusedNumericInstruction.I64RemU,
        is FusedNumericInstruction.I64Rotl,
        is FusedNumericInstruction.I64Rotr,
        is FusedNumericInstruction.I64Shl,
        is FusedNumericInstruction.I64ShrS,
        is FusedNumericInstruction.I64ShrU,
        is FusedNumericInstruction.I64TruncF32S,
        is FusedNumericInstruction.I64TruncF32U,
        is FusedNumericInstruction.I64TruncF64S,
        is FusedNumericInstruction.I64TruncF64U,
        is FusedNumericInstruction.I64TruncSatF32S,
        is FusedNumericInstruction.I64TruncSatF32U,
        is FusedNumericInstruction.I64TruncSatF64S,
        is FusedNumericInstruction.I64TruncSatF64U,
        is FusedNumericInstruction.I64Xor,
        is FusedNumericInstruction.I64GeS,
        is FusedNumericInstruction.I64GeU,
        is FusedNumericInstruction.I64GtS,
        is FusedNumericInstruction.I64GtU,
        is FusedNumericInstruction.I64LeS,
        is FusedNumericInstruction.I64LeU,
        is FusedNumericInstruction.I64LtS,
        is FusedNumericInstruction.I64LtU,
        -> i64NumericInstructionPredecoder(context, instruction)

        is FusedNumericInstruction.F32Const,
        is FusedNumericInstruction.F32Abs,
        is FusedNumericInstruction.F32Add,
        is FusedNumericInstruction.F32Sub,
        is FusedNumericInstruction.F32Mul,
        is FusedNumericInstruction.F32Div,
        is FusedNumericInstruction.F32Ceil,
        is FusedNumericInstruction.F32ConvertI32S,
        is FusedNumericInstruction.F32ConvertI32U,
        is FusedNumericInstruction.F32ConvertI64S,
        is FusedNumericInstruction.F32ConvertI64U,
        is FusedNumericInstruction.F32Copysign,
        is FusedNumericInstruction.F32DemoteF64,
        is FusedNumericInstruction.F32Eq,
        is FusedNumericInstruction.F32Floor,
        is FusedNumericInstruction.F32Ge,
        is FusedNumericInstruction.F32Gt,
        is FusedNumericInstruction.F32Le,
        is FusedNumericInstruction.F32Lt,
        is FusedNumericInstruction.F32Ne,
        is FusedNumericInstruction.F32Nearest,
        is FusedNumericInstruction.F32Neg,
        is FusedNumericInstruction.F32ReinterpretI32,
        is FusedNumericInstruction.F32Sqrt,
        is FusedNumericInstruction.F32Trunc,
        is FusedNumericInstruction.F32Max,
        is FusedNumericInstruction.F32Min,
        -> f32NumericInstructionPredecoder(context, instruction)

        is FusedNumericInstruction.F64Const,
        is FusedNumericInstruction.F64Add,
        is FusedNumericInstruction.F64Sub,
        is FusedNumericInstruction.F64Mul,
        is FusedNumericInstruction.F64Div,
        is FusedNumericInstruction.F64Ge,
        is FusedNumericInstruction.F64Lt,
        is FusedNumericInstruction.F64Abs,
        is FusedNumericInstruction.F64Ceil,
        is FusedNumericInstruction.F64ConvertI32S,
        is FusedNumericInstruction.F64ConvertI32U,
        is FusedNumericInstruction.F64ConvertI64S,
        is FusedNumericInstruction.F64ConvertI64U,
        is FusedNumericInstruction.F64Copysign,
        is FusedNumericInstruction.F64Eq,
        is FusedNumericInstruction.F64Floor,
        is FusedNumericInstruction.F64Gt,
        is FusedNumericInstruction.F64Le,
        is FusedNumericInstruction.F64Ne,
        is FusedNumericInstruction.F64Nearest,
        is FusedNumericInstruction.F64Neg,
        is FusedNumericInstruction.F64PromoteF32,
        is FusedNumericInstruction.F64ReinterpretI64,
        is FusedNumericInstruction.F64Sqrt,
        is FusedNumericInstruction.F64Trunc,
        is FusedNumericInstruction.F64Max,
        is FusedNumericInstruction.F64Min,
        -> f64NumericInstructionPredecoder(context, instruction)
    }
}

private fun i32NumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I32Const -> strictI32ConstInstruction(instruction)
    is FusedNumericInstruction.I32Add,
    is FusedNumericInstruction.I32And,
    is FusedNumericInstruction.I32Or,
    is FusedNumericInstruction.I32Xor,
    is FusedNumericInstruction.I32Sub,
    is FusedNumericInstruction.I32Mul,
    is FusedNumericInstruction.I32DivS,
    is FusedNumericInstruction.I32DivU,
    is FusedNumericInstruction.I32Shl,
    is FusedNumericInstruction.I32ShrS,
    is FusedNumericInstruction.I32ShrU,
    is FusedNumericInstruction.I32RemS,
    is FusedNumericInstruction.I32RemU,
    is FusedNumericInstruction.I32Rotl,
    is FusedNumericInstruction.I32Rotr,
    -> i32ArithmeticInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I32Eqz,
    is FusedNumericInstruction.I32Eq,
    is FusedNumericInstruction.I32Ne,
    is FusedNumericInstruction.I32LtS,
    is FusedNumericInstruction.I32LtU,
    is FusedNumericInstruction.I32GtS,
    is FusedNumericInstruction.I32GtU,
    is FusedNumericInstruction.I32LeS,
    is FusedNumericInstruction.I32LeU,
    is FusedNumericInstruction.I32GeS,
    is FusedNumericInstruction.I32GeU,
    -> i32RelationInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I32Clz,
    is FusedNumericInstruction.I32Ctz,
    is FusedNumericInstruction.I32Extend16S,
    is FusedNumericInstruction.I32Extend8S,
    is FusedNumericInstruction.I32Popcnt,
    -> i32UnaryInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I32TruncF32S,
    is FusedNumericInstruction.I32TruncF32U,
    is FusedNumericInstruction.I32TruncF64S,
    is FusedNumericInstruction.I32TruncF64U,
    is FusedNumericInstruction.I32TruncSatF32S,
    is FusedNumericInstruction.I32TruncSatF32U,
    is FusedNumericInstruction.I32TruncSatF64S,
    is FusedNumericInstruction.I32TruncSatF64U,
    is FusedNumericInstruction.I32WrapI64,
    is FusedNumericInstruction.I32ReinterpretF32,
    -> i32ConversionInstructionPredecoder(context, instruction)

    else -> error("unsupported i32 fused instruction: $instruction")
}

private fun i64NumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I64Const -> strictI64ConstInstruction(instruction)
    is FusedNumericInstruction.I64Add128,
    is FusedNumericInstruction.I64Sub128,
    is FusedNumericInstruction.I64MulWideS,
    is FusedNumericInstruction.I64MulWideU,
    -> i64WideInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I64Add,
    is FusedNumericInstruction.I64Sub,
    is FusedNumericInstruction.I64Mul,
    is FusedNumericInstruction.I64DivS,
    is FusedNumericInstruction.I64DivU,
    is FusedNumericInstruction.I64And,
    is FusedNumericInstruction.I64Or,
    is FusedNumericInstruction.I64RemS,
    is FusedNumericInstruction.I64RemU,
    is FusedNumericInstruction.I64Rotl,
    is FusedNumericInstruction.I64Rotr,
    is FusedNumericInstruction.I64Shl,
    is FusedNumericInstruction.I64ShrS,
    is FusedNumericInstruction.I64ShrU,
    is FusedNumericInstruction.I64Xor,
    -> i64ArithmeticInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I64Eqz,
    is FusedNumericInstruction.I64Eq,
    is FusedNumericInstruction.I64Ne,
    is FusedNumericInstruction.I64GeS,
    is FusedNumericInstruction.I64GeU,
    is FusedNumericInstruction.I64GtS,
    is FusedNumericInstruction.I64GtU,
    is FusedNumericInstruction.I64LeS,
    is FusedNumericInstruction.I64LeU,
    is FusedNumericInstruction.I64LtS,
    is FusedNumericInstruction.I64LtU,
    -> i64RelationInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I64Clz,
    is FusedNumericInstruction.I64Ctz,
    is FusedNumericInstruction.I64Extend16S,
    is FusedNumericInstruction.I64Extend32S,
    is FusedNumericInstruction.I64Extend8S,
    is FusedNumericInstruction.I64ExtendI32S,
    is FusedNumericInstruction.I64ExtendI32U,
    is FusedNumericInstruction.I64Popcnt,
    is FusedNumericInstruction.I64ReinterpretF64,
    -> i64UnaryInstructionPredecoder(context, instruction)

    is FusedNumericInstruction.I64TruncF32S,
    is FusedNumericInstruction.I64TruncF32U,
    is FusedNumericInstruction.I64TruncF64S,
    is FusedNumericInstruction.I64TruncF64U,
    is FusedNumericInstruction.I64TruncSatF32S,
    is FusedNumericInstruction.I64TruncSatF32U,
    is FusedNumericInstruction.I64TruncSatF64S,
    is FusedNumericInstruction.I64TruncSatF64U,
    -> i64ConversionInstructionPredecoder(context, instruction)

    else -> error("unsupported i64 fused instruction: $instruction")
}

private fun i32ArithmeticInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I32Add -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32AddDispatcher(RuntimeFusedNumericInstruction.I32AddIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32AddDispatcher(RuntimeFusedNumericInstruction.I32AddIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32AddDispatcher(RuntimeFusedNumericInstruction.I32AddSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32AddDispatcher(RuntimeFusedNumericInstruction.I32AddSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32And -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32AndDispatcher(RuntimeFusedNumericInstruction.I32AndIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32AndDispatcher(RuntimeFusedNumericInstruction.I32AndIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32AndDispatcher(RuntimeFusedNumericInstruction.I32AndSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32AndDispatcher(RuntimeFusedNumericInstruction.I32AndSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Or -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32OrDispatcher(RuntimeFusedNumericInstruction.I32OrIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32OrDispatcher(RuntimeFusedNumericInstruction.I32OrIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32OrDispatcher(RuntimeFusedNumericInstruction.I32OrSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32OrDispatcher(RuntimeFusedNumericInstruction.I32OrSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Xor -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32XorDispatcher(RuntimeFusedNumericInstruction.I32XorIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32XorDispatcher(RuntimeFusedNumericInstruction.I32XorIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32XorDispatcher(RuntimeFusedNumericInstruction.I32XorSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32XorDispatcher(RuntimeFusedNumericInstruction.I32XorSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Sub -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32SubDispatcher(RuntimeFusedNumericInstruction.I32SubIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32SubDispatcher(RuntimeFusedNumericInstruction.I32SubIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32SubDispatcher(RuntimeFusedNumericInstruction.I32SubSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32SubDispatcher(RuntimeFusedNumericInstruction.I32SubSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Mul -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32MulDispatcher(RuntimeFusedNumericInstruction.I32MulIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32MulDispatcher(RuntimeFusedNumericInstruction.I32MulIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32MulDispatcher(RuntimeFusedNumericInstruction.I32MulSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32MulDispatcher(RuntimeFusedNumericInstruction.I32MulSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32DivS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32DivSDispatcher(RuntimeFusedNumericInstruction.I32DivSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32DivSDispatcher(RuntimeFusedNumericInstruction.I32DivSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32DivSDispatcher(RuntimeFusedNumericInstruction.I32DivSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32DivSDispatcher(RuntimeFusedNumericInstruction.I32DivSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32DivU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32DivUDispatcher(RuntimeFusedNumericInstruction.I32DivUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32DivUDispatcher(RuntimeFusedNumericInstruction.I32DivUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32DivUDispatcher(RuntimeFusedNumericInstruction.I32DivUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32DivUDispatcher(RuntimeFusedNumericInstruction.I32DivUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Shl -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32ShlDispatcher(RuntimeFusedNumericInstruction.I32ShlIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32ShlDispatcher(RuntimeFusedNumericInstruction.I32ShlIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32ShlDispatcher(RuntimeFusedNumericInstruction.I32ShlSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32ShlDispatcher(RuntimeFusedNumericInstruction.I32ShlSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32ShrS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32ShrSDispatcher(RuntimeFusedNumericInstruction.I32ShrSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32ShrSDispatcher(RuntimeFusedNumericInstruction.I32ShrSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32ShrSDispatcher(RuntimeFusedNumericInstruction.I32ShrSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32ShrSDispatcher(RuntimeFusedNumericInstruction.I32ShrSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32ShrU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32ShrUDispatcher(RuntimeFusedNumericInstruction.I32ShrUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32ShrUDispatcher(RuntimeFusedNumericInstruction.I32ShrUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32ShrUDispatcher(RuntimeFusedNumericInstruction.I32ShrUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32ShrUDispatcher(RuntimeFusedNumericInstruction.I32ShrUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32RemS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32RemSDispatcher(RuntimeFusedNumericInstruction.I32RemSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32RemSDispatcher(RuntimeFusedNumericInstruction.I32RemSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32RemSDispatcher(RuntimeFusedNumericInstruction.I32RemSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32RemSDispatcher(RuntimeFusedNumericInstruction.I32RemSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32RemU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32RemUDispatcher(RuntimeFusedNumericInstruction.I32RemUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32RemUDispatcher(RuntimeFusedNumericInstruction.I32RemUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32RemUDispatcher(RuntimeFusedNumericInstruction.I32RemUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32RemUDispatcher(RuntimeFusedNumericInstruction.I32RemUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Rotl -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32RotlDispatcher(RuntimeFusedNumericInstruction.I32RotlIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32RotlDispatcher(RuntimeFusedNumericInstruction.I32RotlIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32RotlDispatcher(RuntimeFusedNumericInstruction.I32RotlSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32RotlDispatcher(RuntimeFusedNumericInstruction.I32RotlSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Rotr -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32RotrDispatcher(RuntimeFusedNumericInstruction.I32RotrIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32RotrDispatcher(RuntimeFusedNumericInstruction.I32RotrIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32RotrDispatcher(RuntimeFusedNumericInstruction.I32RotrSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32RotrDispatcher(RuntimeFusedNumericInstruction.I32RotrSs(leftSlot, rightSlot, destinationSlot)) },
    )
    else -> error("unsupported i32 arithmetic fused instruction: $instruction")
}

private fun i32RelationInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I32Eqz -> strictI32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32EqzDispatcher(RuntimeFusedNumericInstruction.I32EqzI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32EqzDispatcher(RuntimeFusedNumericInstruction.I32EqzS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Eq -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32EqDispatcher(RuntimeFusedNumericInstruction.I32EqIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32EqDispatcher(RuntimeFusedNumericInstruction.I32EqIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32EqDispatcher(RuntimeFusedNumericInstruction.I32EqSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32EqDispatcher(RuntimeFusedNumericInstruction.I32EqSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Ne -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32NeDispatcher(RuntimeFusedNumericInstruction.I32NeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32NeDispatcher(RuntimeFusedNumericInstruction.I32NeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32NeDispatcher(RuntimeFusedNumericInstruction.I32NeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32NeDispatcher(RuntimeFusedNumericInstruction.I32NeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32LtS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32LtSDispatcher(RuntimeFusedNumericInstruction.I32LtSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32LtSDispatcher(RuntimeFusedNumericInstruction.I32LtSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32LtSDispatcher(RuntimeFusedNumericInstruction.I32LtSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32LtSDispatcher(RuntimeFusedNumericInstruction.I32LtSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32LtU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32LtUDispatcher(RuntimeFusedNumericInstruction.I32LtUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32LtUDispatcher(RuntimeFusedNumericInstruction.I32LtUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32LtUDispatcher(RuntimeFusedNumericInstruction.I32LtUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32LtUDispatcher(RuntimeFusedNumericInstruction.I32LtUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32GtS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32GtSDispatcher(RuntimeFusedNumericInstruction.I32GtSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32GtSDispatcher(RuntimeFusedNumericInstruction.I32GtSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32GtSDispatcher(RuntimeFusedNumericInstruction.I32GtSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32GtSDispatcher(RuntimeFusedNumericInstruction.I32GtSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32GtU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32GtUDispatcher(RuntimeFusedNumericInstruction.I32GtUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32GtUDispatcher(RuntimeFusedNumericInstruction.I32GtUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32GtUDispatcher(RuntimeFusedNumericInstruction.I32GtUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32GtUDispatcher(RuntimeFusedNumericInstruction.I32GtUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32LeS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32LeSDispatcher(RuntimeFusedNumericInstruction.I32LeSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32LeSDispatcher(RuntimeFusedNumericInstruction.I32LeSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32LeSDispatcher(RuntimeFusedNumericInstruction.I32LeSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32LeSDispatcher(RuntimeFusedNumericInstruction.I32LeSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32LeU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32LeUDispatcher(RuntimeFusedNumericInstruction.I32LeUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32LeUDispatcher(RuntimeFusedNumericInstruction.I32LeUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32LeUDispatcher(RuntimeFusedNumericInstruction.I32LeUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32LeUDispatcher(RuntimeFusedNumericInstruction.I32LeUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32GeS -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32GeSDispatcher(RuntimeFusedNumericInstruction.I32GeSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32GeSDispatcher(RuntimeFusedNumericInstruction.I32GeSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32GeSDispatcher(RuntimeFusedNumericInstruction.I32GeSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32GeSDispatcher(RuntimeFusedNumericInstruction.I32GeSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32GeU -> strictI32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I32GeUDispatcher(RuntimeFusedNumericInstruction.I32GeUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I32GeUDispatcher(RuntimeFusedNumericInstruction.I32GeUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I32GeUDispatcher(RuntimeFusedNumericInstruction.I32GeUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I32GeUDispatcher(RuntimeFusedNumericInstruction.I32GeUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    else -> error("unsupported i32 relation fused instruction: $instruction")
}

private fun i32UnaryInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I32Clz -> strictI32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32ClzDispatcher(RuntimeFusedNumericInstruction.I32ClzI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32ClzDispatcher(RuntimeFusedNumericInstruction.I32ClzS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Ctz -> strictI32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32CtzDispatcher(RuntimeFusedNumericInstruction.I32CtzI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32CtzDispatcher(RuntimeFusedNumericInstruction.I32CtzS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Extend16S -> strictI32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32Extend16SDispatcher(RuntimeFusedNumericInstruction.I32Extend16SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32Extend16SDispatcher(RuntimeFusedNumericInstruction.I32Extend16SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Extend8S -> strictI32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32Extend8SDispatcher(RuntimeFusedNumericInstruction.I32Extend8SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32Extend8SDispatcher(RuntimeFusedNumericInstruction.I32Extend8SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32Popcnt -> strictI32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32PopcntDispatcher(RuntimeFusedNumericInstruction.I32PopcntI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32PopcntDispatcher(RuntimeFusedNumericInstruction.I32PopcntS(operandSlot, destinationSlot)) },
    )
    else -> error("unsupported i32 unary fused instruction: $instruction")
}

private fun i32ConversionInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I32TruncF32S -> strictF32ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncF32SDispatcher(RuntimeFusedNumericInstruction.I32TruncF32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncF32SDispatcher(RuntimeFusedNumericInstruction.I32TruncF32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncF32U -> strictF32ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncF32UDispatcher(RuntimeFusedNumericInstruction.I32TruncF32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncF32UDispatcher(RuntimeFusedNumericInstruction.I32TruncF32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncF64S -> strictF64ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncF64SDispatcher(RuntimeFusedNumericInstruction.I32TruncF64SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncF64SDispatcher(RuntimeFusedNumericInstruction.I32TruncF64SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncF64U -> strictF64ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncF64UDispatcher(RuntimeFusedNumericInstruction.I32TruncF64UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncF64UDispatcher(RuntimeFusedNumericInstruction.I32TruncF64US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncSatF32S -> strictF32ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncSatF32SDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncSatF32SDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncSatF32U -> strictF32ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncSatF32UDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncSatF32UDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncSatF64S -> strictF64ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncSatF64SDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF64SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncSatF64SDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF64SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32TruncSatF64U -> strictF64ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32TruncSatF64UDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF64UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32TruncSatF64UDispatcher(RuntimeFusedNumericInstruction.I32TruncSatF64US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32WrapI64 -> strictI64ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32WrapI64Dispatcher(RuntimeFusedNumericInstruction.I32WrapI64I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32WrapI64Dispatcher(RuntimeFusedNumericInstruction.I32WrapI64S(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I32ReinterpretF32 -> strictF32ToI32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I32ReinterpretF32Dispatcher(RuntimeFusedNumericInstruction.I32ReinterpretF32I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I32ReinterpretF32Dispatcher(RuntimeFusedNumericInstruction.I32ReinterpretF32S(operandSlot, destinationSlot)) },
    )
    else -> error("unsupported i32 conversion fused instruction: $instruction")
}

private fun i64WideInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I64Add128 -> strictI64Add128(
        context = context,
        leftLow = instruction.leftLow,
        leftHigh = instruction.leftHigh,
        rightLow = instruction.rightLow,
        rightHigh = instruction.rightHigh,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
    )
    is FusedNumericInstruction.I64Sub128 -> strictI64Sub128(
        context = context,
        leftLow = instruction.leftLow,
        leftHigh = instruction.leftHigh,
        rightLow = instruction.rightLow,
        rightHigh = instruction.rightHigh,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
    )
    is FusedNumericInstruction.I64MulWideS -> strictI64MulWideSigned(
        context = context,
        left = instruction.left,
        right = instruction.right,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
    )
    is FusedNumericInstruction.I64MulWideU -> strictI64MulWideUnsigned(
        context = context,
        left = instruction.left,
        right = instruction.right,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
    )
    else -> error("unsupported i64 wide fused instruction: $instruction")
}

private fun i64ArithmeticInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I64Add -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64AddDispatcher(RuntimeFusedNumericInstruction.I64AddIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64AddDispatcher(RuntimeFusedNumericInstruction.I64AddIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64AddDispatcher(RuntimeFusedNumericInstruction.I64AddSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64AddDispatcher(RuntimeFusedNumericInstruction.I64AddSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Sub -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64SubDispatcher(RuntimeFusedNumericInstruction.I64SubIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64SubDispatcher(RuntimeFusedNumericInstruction.I64SubIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64SubDispatcher(RuntimeFusedNumericInstruction.I64SubSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64SubDispatcher(RuntimeFusedNumericInstruction.I64SubSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Mul -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64MulDispatcher(RuntimeFusedNumericInstruction.I64MulIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64MulDispatcher(RuntimeFusedNumericInstruction.I64MulIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64MulDispatcher(RuntimeFusedNumericInstruction.I64MulSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64MulDispatcher(RuntimeFusedNumericInstruction.I64MulSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64DivS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64DivSDispatcher(RuntimeFusedNumericInstruction.I64DivSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64DivSDispatcher(RuntimeFusedNumericInstruction.I64DivSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64DivSDispatcher(RuntimeFusedNumericInstruction.I64DivSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64DivSDispatcher(RuntimeFusedNumericInstruction.I64DivSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64DivU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64DivUDispatcher(RuntimeFusedNumericInstruction.I64DivUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64DivUDispatcher(RuntimeFusedNumericInstruction.I64DivUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64DivUDispatcher(RuntimeFusedNumericInstruction.I64DivUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64DivUDispatcher(RuntimeFusedNumericInstruction.I64DivUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64And -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64AndDispatcher(RuntimeFusedNumericInstruction.I64AndIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64AndDispatcher(RuntimeFusedNumericInstruction.I64AndIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64AndDispatcher(RuntimeFusedNumericInstruction.I64AndSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64AndDispatcher(RuntimeFusedNumericInstruction.I64AndSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Or -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64OrDispatcher(RuntimeFusedNumericInstruction.I64OrIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64OrDispatcher(RuntimeFusedNumericInstruction.I64OrIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64OrDispatcher(RuntimeFusedNumericInstruction.I64OrSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64OrDispatcher(RuntimeFusedNumericInstruction.I64OrSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64RemS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64RemSDispatcher(RuntimeFusedNumericInstruction.I64RemSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64RemSDispatcher(RuntimeFusedNumericInstruction.I64RemSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64RemSDispatcher(RuntimeFusedNumericInstruction.I64RemSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64RemSDispatcher(RuntimeFusedNumericInstruction.I64RemSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64RemU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64RemUDispatcher(RuntimeFusedNumericInstruction.I64RemUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64RemUDispatcher(RuntimeFusedNumericInstruction.I64RemUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64RemUDispatcher(RuntimeFusedNumericInstruction.I64RemUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64RemUDispatcher(RuntimeFusedNumericInstruction.I64RemUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Rotl -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64RotlDispatcher(RuntimeFusedNumericInstruction.I64RotlIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64RotlDispatcher(RuntimeFusedNumericInstruction.I64RotlIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64RotlDispatcher(RuntimeFusedNumericInstruction.I64RotlSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64RotlDispatcher(RuntimeFusedNumericInstruction.I64RotlSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Rotr -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64RotrDispatcher(RuntimeFusedNumericInstruction.I64RotrIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64RotrDispatcher(RuntimeFusedNumericInstruction.I64RotrIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64RotrDispatcher(RuntimeFusedNumericInstruction.I64RotrSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64RotrDispatcher(RuntimeFusedNumericInstruction.I64RotrSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Shl -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64ShlDispatcher(RuntimeFusedNumericInstruction.I64ShlIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64ShlDispatcher(RuntimeFusedNumericInstruction.I64ShlIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64ShlDispatcher(RuntimeFusedNumericInstruction.I64ShlSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64ShlDispatcher(RuntimeFusedNumericInstruction.I64ShlSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64ShrS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64ShrSDispatcher(RuntimeFusedNumericInstruction.I64ShrSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64ShrSDispatcher(RuntimeFusedNumericInstruction.I64ShrSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64ShrSDispatcher(RuntimeFusedNumericInstruction.I64ShrSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64ShrSDispatcher(RuntimeFusedNumericInstruction.I64ShrSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64ShrU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64ShrUDispatcher(RuntimeFusedNumericInstruction.I64ShrUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64ShrUDispatcher(RuntimeFusedNumericInstruction.I64ShrUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64ShrUDispatcher(RuntimeFusedNumericInstruction.I64ShrUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64ShrUDispatcher(RuntimeFusedNumericInstruction.I64ShrUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Xor -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64XorDispatcher(RuntimeFusedNumericInstruction.I64XorIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64XorDispatcher(RuntimeFusedNumericInstruction.I64XorIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64XorDispatcher(RuntimeFusedNumericInstruction.I64XorSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64XorDispatcher(RuntimeFusedNumericInstruction.I64XorSs(leftSlot, rightSlot, destinationSlot)) },
    )
    else -> error("unsupported i64 arithmetic fused instruction: $instruction")
}

private fun i64RelationInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I64Eqz -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64EqzDispatcher(RuntimeFusedNumericInstruction.I64EqzI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64EqzDispatcher(RuntimeFusedNumericInstruction.I64EqzS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Eq -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64EqDispatcher(RuntimeFusedNumericInstruction.I64EqIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64EqDispatcher(RuntimeFusedNumericInstruction.I64EqIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64EqDispatcher(RuntimeFusedNumericInstruction.I64EqSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64EqDispatcher(RuntimeFusedNumericInstruction.I64EqSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Ne -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64NeDispatcher(RuntimeFusedNumericInstruction.I64NeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64NeDispatcher(RuntimeFusedNumericInstruction.I64NeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64NeDispatcher(RuntimeFusedNumericInstruction.I64NeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64NeDispatcher(RuntimeFusedNumericInstruction.I64NeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64GeS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64GeSDispatcher(RuntimeFusedNumericInstruction.I64GeSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64GeSDispatcher(RuntimeFusedNumericInstruction.I64GeSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64GeSDispatcher(RuntimeFusedNumericInstruction.I64GeSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64GeSDispatcher(RuntimeFusedNumericInstruction.I64GeSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64GeU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64GeUDispatcher(RuntimeFusedNumericInstruction.I64GeUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64GeUDispatcher(RuntimeFusedNumericInstruction.I64GeUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64GeUDispatcher(RuntimeFusedNumericInstruction.I64GeUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64GeUDispatcher(RuntimeFusedNumericInstruction.I64GeUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64GtS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64GtSDispatcher(RuntimeFusedNumericInstruction.I64GtSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64GtSDispatcher(RuntimeFusedNumericInstruction.I64GtSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64GtSDispatcher(RuntimeFusedNumericInstruction.I64GtSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64GtSDispatcher(RuntimeFusedNumericInstruction.I64GtSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64GtU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64GtUDispatcher(RuntimeFusedNumericInstruction.I64GtUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64GtUDispatcher(RuntimeFusedNumericInstruction.I64GtUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64GtUDispatcher(RuntimeFusedNumericInstruction.I64GtUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64GtUDispatcher(RuntimeFusedNumericInstruction.I64GtUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64LeS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64LeSDispatcher(RuntimeFusedNumericInstruction.I64LeSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64LeSDispatcher(RuntimeFusedNumericInstruction.I64LeSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64LeSDispatcher(RuntimeFusedNumericInstruction.I64LeSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64LeSDispatcher(RuntimeFusedNumericInstruction.I64LeSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64LeU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64LeUDispatcher(RuntimeFusedNumericInstruction.I64LeUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64LeUDispatcher(RuntimeFusedNumericInstruction.I64LeUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64LeUDispatcher(RuntimeFusedNumericInstruction.I64LeUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64LeUDispatcher(RuntimeFusedNumericInstruction.I64LeUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64LtS -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64LtSDispatcher(RuntimeFusedNumericInstruction.I64LtSIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64LtSDispatcher(RuntimeFusedNumericInstruction.I64LtSIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64LtSDispatcher(RuntimeFusedNumericInstruction.I64LtSSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64LtSDispatcher(RuntimeFusedNumericInstruction.I64LtSSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64LtU -> strictI64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> I64LtUDispatcher(RuntimeFusedNumericInstruction.I64LtUIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> I64LtUDispatcher(RuntimeFusedNumericInstruction.I64LtUIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> I64LtUDispatcher(RuntimeFusedNumericInstruction.I64LtUSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> I64LtUDispatcher(RuntimeFusedNumericInstruction.I64LtUSs(leftSlot, rightSlot, destinationSlot)) },
    )
    else -> error("unsupported i64 relation fused instruction: $instruction")
}

private fun i64UnaryInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I64Clz -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64ClzDispatcher(RuntimeFusedNumericInstruction.I64ClzI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64ClzDispatcher(RuntimeFusedNumericInstruction.I64ClzS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Ctz -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64CtzDispatcher(RuntimeFusedNumericInstruction.I64CtzI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64CtzDispatcher(RuntimeFusedNumericInstruction.I64CtzS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Extend16S -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64Extend16SDispatcher(RuntimeFusedNumericInstruction.I64Extend16SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64Extend16SDispatcher(RuntimeFusedNumericInstruction.I64Extend16SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Extend32S -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64Extend32SDispatcher(RuntimeFusedNumericInstruction.I64Extend32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64Extend32SDispatcher(RuntimeFusedNumericInstruction.I64Extend32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Extend8S -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64Extend8SDispatcher(RuntimeFusedNumericInstruction.I64Extend8SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64Extend8SDispatcher(RuntimeFusedNumericInstruction.I64Extend8SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64ExtendI32S -> strictI32ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64ExtendI32SDispatcher(RuntimeFusedNumericInstruction.I64ExtendI32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64ExtendI32SDispatcher(RuntimeFusedNumericInstruction.I64ExtendI32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64ExtendI32U -> strictI32ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64ExtendI32UDispatcher(RuntimeFusedNumericInstruction.I64ExtendI32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64ExtendI32UDispatcher(RuntimeFusedNumericInstruction.I64ExtendI32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64Popcnt -> strictI64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64PopcntDispatcher(RuntimeFusedNumericInstruction.I64PopcntI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64PopcntDispatcher(RuntimeFusedNumericInstruction.I64PopcntS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64ReinterpretF64 -> strictF64ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64ReinterpretF64Dispatcher(RuntimeFusedNumericInstruction.I64ReinterpretF64I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64ReinterpretF64Dispatcher(RuntimeFusedNumericInstruction.I64ReinterpretF64S(operandSlot, destinationSlot)) },
    )
    else -> error("unsupported i64 unary fused instruction: $instruction")
}

private fun i64ConversionInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.I64TruncF32S -> strictF32ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncF32SDispatcher(RuntimeFusedNumericInstruction.I64TruncF32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncF32SDispatcher(RuntimeFusedNumericInstruction.I64TruncF32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncF32U -> strictF32ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncF32UDispatcher(RuntimeFusedNumericInstruction.I64TruncF32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncF32UDispatcher(RuntimeFusedNumericInstruction.I64TruncF32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncF64S -> strictF64ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncF64SDispatcher(RuntimeFusedNumericInstruction.I64TruncF64SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncF64SDispatcher(RuntimeFusedNumericInstruction.I64TruncF64SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncF64U -> strictF64ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncF64UDispatcher(RuntimeFusedNumericInstruction.I64TruncF64UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncF64UDispatcher(RuntimeFusedNumericInstruction.I64TruncF64US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncSatF32S -> strictF32ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncSatF32SDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncSatF32SDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncSatF32U -> strictF32ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncSatF32UDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncSatF32UDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncSatF64S -> strictF64ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncSatF64SDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF64SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncSatF64SDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF64SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.I64TruncSatF64U -> strictF64ToI64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> I64TruncSatF64UDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF64UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> I64TruncSatF64UDispatcher(RuntimeFusedNumericInstruction.I64TruncSatF64US(operandSlot, destinationSlot)) },
    )
    else -> error("unsupported i64 conversion fused instruction: $instruction")
}

private fun f32NumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.F32Const -> strictF32ConstInstruction(instruction)
    is FusedNumericInstruction.F32Abs -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32AbsDispatcher(RuntimeFusedNumericInstruction.F32AbsI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32AbsDispatcher(RuntimeFusedNumericInstruction.F32AbsS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Add -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32AddDispatcher(RuntimeFusedNumericInstruction.F32AddIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32AddDispatcher(RuntimeFusedNumericInstruction.F32AddIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32AddDispatcher(RuntimeFusedNumericInstruction.F32AddSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32AddDispatcher(RuntimeFusedNumericInstruction.F32AddSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Sub -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32SubDispatcher(RuntimeFusedNumericInstruction.F32SubIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32SubDispatcher(RuntimeFusedNumericInstruction.F32SubIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32SubDispatcher(RuntimeFusedNumericInstruction.F32SubSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32SubDispatcher(RuntimeFusedNumericInstruction.F32SubSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Mul -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32MulDispatcher(RuntimeFusedNumericInstruction.F32MulIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32MulDispatcher(RuntimeFusedNumericInstruction.F32MulIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32MulDispatcher(RuntimeFusedNumericInstruction.F32MulSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32MulDispatcher(RuntimeFusedNumericInstruction.F32MulSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Div -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32DivDispatcher(RuntimeFusedNumericInstruction.F32DivIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32DivDispatcher(RuntimeFusedNumericInstruction.F32DivIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32DivDispatcher(RuntimeFusedNumericInstruction.F32DivSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32DivDispatcher(RuntimeFusedNumericInstruction.F32DivSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Ceil -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32CeilDispatcher(RuntimeFusedNumericInstruction.F32CeilI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32CeilDispatcher(RuntimeFusedNumericInstruction.F32CeilS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32ConvertI32S -> strictI32ToF32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32ConvertI32SDispatcher(RuntimeFusedNumericInstruction.F32ConvertI32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32ConvertI32SDispatcher(RuntimeFusedNumericInstruction.F32ConvertI32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32ConvertI32U -> strictI32ToF32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32ConvertI32UDispatcher(RuntimeFusedNumericInstruction.F32ConvertI32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32ConvertI32UDispatcher(RuntimeFusedNumericInstruction.F32ConvertI32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32ConvertI64S -> strictI64ToF32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32ConvertI64SDispatcher(RuntimeFusedNumericInstruction.F32ConvertI64SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32ConvertI64SDispatcher(RuntimeFusedNumericInstruction.F32ConvertI64SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32ConvertI64U -> strictI64ToF32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32ConvertI64UDispatcher(RuntimeFusedNumericInstruction.F32ConvertI64UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32ConvertI64UDispatcher(RuntimeFusedNumericInstruction.F32ConvertI64US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Copysign -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32CopysignDispatcher(RuntimeFusedNumericInstruction.F32CopysignIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32CopysignDispatcher(RuntimeFusedNumericInstruction.F32CopysignIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32CopysignDispatcher(RuntimeFusedNumericInstruction.F32CopysignSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32CopysignDispatcher(RuntimeFusedNumericInstruction.F32CopysignSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32DemoteF64 -> strictF64ToF32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32DemoteF64Dispatcher(RuntimeFusedNumericInstruction.F32DemoteF64I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32DemoteF64Dispatcher(RuntimeFusedNumericInstruction.F32DemoteF64S(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Eq -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32EqDispatcher(RuntimeFusedNumericInstruction.F32EqIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32EqDispatcher(RuntimeFusedNumericInstruction.F32EqIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32EqDispatcher(RuntimeFusedNumericInstruction.F32EqSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32EqDispatcher(RuntimeFusedNumericInstruction.F32EqSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Floor -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32FloorDispatcher(RuntimeFusedNumericInstruction.F32FloorI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32FloorDispatcher(RuntimeFusedNumericInstruction.F32FloorS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Ge -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32GeDispatcher(RuntimeFusedNumericInstruction.F32GeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32GeDispatcher(RuntimeFusedNumericInstruction.F32GeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32GeDispatcher(RuntimeFusedNumericInstruction.F32GeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32GeDispatcher(RuntimeFusedNumericInstruction.F32GeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Gt -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32GtDispatcher(RuntimeFusedNumericInstruction.F32GtIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32GtDispatcher(RuntimeFusedNumericInstruction.F32GtIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32GtDispatcher(RuntimeFusedNumericInstruction.F32GtSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32GtDispatcher(RuntimeFusedNumericInstruction.F32GtSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Le -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32LeDispatcher(RuntimeFusedNumericInstruction.F32LeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32LeDispatcher(RuntimeFusedNumericInstruction.F32LeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32LeDispatcher(RuntimeFusedNumericInstruction.F32LeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32LeDispatcher(RuntimeFusedNumericInstruction.F32LeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Lt -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32LtDispatcher(RuntimeFusedNumericInstruction.F32LtIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32LtDispatcher(RuntimeFusedNumericInstruction.F32LtIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32LtDispatcher(RuntimeFusedNumericInstruction.F32LtSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32LtDispatcher(RuntimeFusedNumericInstruction.F32LtSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Ne -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32NeDispatcher(RuntimeFusedNumericInstruction.F32NeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32NeDispatcher(RuntimeFusedNumericInstruction.F32NeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32NeDispatcher(RuntimeFusedNumericInstruction.F32NeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32NeDispatcher(RuntimeFusedNumericInstruction.F32NeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Nearest -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32NearestDispatcher(RuntimeFusedNumericInstruction.F32NearestI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32NearestDispatcher(RuntimeFusedNumericInstruction.F32NearestS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Neg -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32NegDispatcher(RuntimeFusedNumericInstruction.F32NegI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32NegDispatcher(RuntimeFusedNumericInstruction.F32NegS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32ReinterpretI32 -> strictI32BitsToF32(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32ReinterpretI32Dispatcher(RuntimeFusedNumericInstruction.F32ReinterpretI32I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32ReinterpretI32Dispatcher(RuntimeFusedNumericInstruction.F32ReinterpretI32S(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Sqrt -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32SqrtDispatcher(RuntimeFusedNumericInstruction.F32SqrtI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32SqrtDispatcher(RuntimeFusedNumericInstruction.F32SqrtS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Trunc -> strictF32Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F32TruncDispatcher(RuntimeFusedNumericInstruction.F32TruncI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F32TruncDispatcher(RuntimeFusedNumericInstruction.F32TruncS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Max -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32MaxDispatcher(RuntimeFusedNumericInstruction.F32MaxIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32MaxDispatcher(RuntimeFusedNumericInstruction.F32MaxIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32MaxDispatcher(RuntimeFusedNumericInstruction.F32MaxSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32MaxDispatcher(RuntimeFusedNumericInstruction.F32MaxSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F32Min -> strictF32Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F32MinDispatcher(RuntimeFusedNumericInstruction.F32MinIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F32MinDispatcher(RuntimeFusedNumericInstruction.F32MinIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F32MinDispatcher(RuntimeFusedNumericInstruction.F32MinSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F32MinDispatcher(RuntimeFusedNumericInstruction.F32MinSs(leftSlot, rightSlot, destinationSlot)) },
    )
    else -> error("unsupported f32 fused instruction: $instruction")
}

private fun f64NumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): DispatchableInstruction = when (instruction) {
    is FusedNumericInstruction.F64Const -> strictF64ConstInstruction(instruction)
    is FusedNumericInstruction.F64Add -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64AddDispatcher(RuntimeFusedNumericInstruction.F64AddIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64AddDispatcher(RuntimeFusedNumericInstruction.F64AddIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64AddDispatcher(RuntimeFusedNumericInstruction.F64AddSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64AddDispatcher(RuntimeFusedNumericInstruction.F64AddSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Sub -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64SubDispatcher(RuntimeFusedNumericInstruction.F64SubIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64SubDispatcher(RuntimeFusedNumericInstruction.F64SubIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64SubDispatcher(RuntimeFusedNumericInstruction.F64SubSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64SubDispatcher(RuntimeFusedNumericInstruction.F64SubSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Mul -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64MulDispatcher(RuntimeFusedNumericInstruction.F64MulIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64MulDispatcher(RuntimeFusedNumericInstruction.F64MulIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64MulDispatcher(RuntimeFusedNumericInstruction.F64MulSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64MulDispatcher(RuntimeFusedNumericInstruction.F64MulSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Div -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64DivDispatcher(RuntimeFusedNumericInstruction.F64DivIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64DivDispatcher(RuntimeFusedNumericInstruction.F64DivIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64DivDispatcher(RuntimeFusedNumericInstruction.F64DivSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64DivDispatcher(RuntimeFusedNumericInstruction.F64DivSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Ge -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64GeDispatcher(RuntimeFusedNumericInstruction.F64GeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64GeDispatcher(RuntimeFusedNumericInstruction.F64GeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64GeDispatcher(RuntimeFusedNumericInstruction.F64GeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64GeDispatcher(RuntimeFusedNumericInstruction.F64GeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Lt -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64LtDispatcher(RuntimeFusedNumericInstruction.F64LtIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64LtDispatcher(RuntimeFusedNumericInstruction.F64LtIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64LtDispatcher(RuntimeFusedNumericInstruction.F64LtSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64LtDispatcher(RuntimeFusedNumericInstruction.F64LtSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Abs -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64AbsDispatcher(RuntimeFusedNumericInstruction.F64AbsI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64AbsDispatcher(RuntimeFusedNumericInstruction.F64AbsS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Ceil -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64CeilDispatcher(RuntimeFusedNumericInstruction.F64CeilI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64CeilDispatcher(RuntimeFusedNumericInstruction.F64CeilS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64ConvertI32S -> strictI32ToF64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64ConvertI32SDispatcher(RuntimeFusedNumericInstruction.F64ConvertI32SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64ConvertI32SDispatcher(RuntimeFusedNumericInstruction.F64ConvertI32SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64ConvertI32U -> strictI32ToF64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64ConvertI32UDispatcher(RuntimeFusedNumericInstruction.F64ConvertI32UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64ConvertI32UDispatcher(RuntimeFusedNumericInstruction.F64ConvertI32US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64ConvertI64S -> strictI64ToF64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64ConvertI64SDispatcher(RuntimeFusedNumericInstruction.F64ConvertI64SI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64ConvertI64SDispatcher(RuntimeFusedNumericInstruction.F64ConvertI64SS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64ConvertI64U -> strictI64ToF64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64ConvertI64UDispatcher(RuntimeFusedNumericInstruction.F64ConvertI64UI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64ConvertI64UDispatcher(RuntimeFusedNumericInstruction.F64ConvertI64US(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Copysign -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64CopysignDispatcher(RuntimeFusedNumericInstruction.F64CopysignIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64CopysignDispatcher(RuntimeFusedNumericInstruction.F64CopysignIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64CopysignDispatcher(RuntimeFusedNumericInstruction.F64CopysignSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64CopysignDispatcher(RuntimeFusedNumericInstruction.F64CopysignSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Eq -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64EqDispatcher(RuntimeFusedNumericInstruction.F64EqIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64EqDispatcher(RuntimeFusedNumericInstruction.F64EqIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64EqDispatcher(RuntimeFusedNumericInstruction.F64EqSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64EqDispatcher(RuntimeFusedNumericInstruction.F64EqSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Floor -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64FloorDispatcher(RuntimeFusedNumericInstruction.F64FloorI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64FloorDispatcher(RuntimeFusedNumericInstruction.F64FloorS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Gt -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64GtDispatcher(RuntimeFusedNumericInstruction.F64GtIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64GtDispatcher(RuntimeFusedNumericInstruction.F64GtIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64GtDispatcher(RuntimeFusedNumericInstruction.F64GtSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64GtDispatcher(RuntimeFusedNumericInstruction.F64GtSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Le -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64LeDispatcher(RuntimeFusedNumericInstruction.F64LeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64LeDispatcher(RuntimeFusedNumericInstruction.F64LeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64LeDispatcher(RuntimeFusedNumericInstruction.F64LeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64LeDispatcher(RuntimeFusedNumericInstruction.F64LeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Ne -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64NeDispatcher(RuntimeFusedNumericInstruction.F64NeIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64NeDispatcher(RuntimeFusedNumericInstruction.F64NeIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64NeDispatcher(RuntimeFusedNumericInstruction.F64NeSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64NeDispatcher(RuntimeFusedNumericInstruction.F64NeSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Nearest -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64NearestDispatcher(RuntimeFusedNumericInstruction.F64NearestI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64NearestDispatcher(RuntimeFusedNumericInstruction.F64NearestS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Neg -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64NegDispatcher(RuntimeFusedNumericInstruction.F64NegI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64NegDispatcher(RuntimeFusedNumericInstruction.F64NegS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64PromoteF32 -> strictF32ToF64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64PromoteF32Dispatcher(RuntimeFusedNumericInstruction.F64PromoteF32I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64PromoteF32Dispatcher(RuntimeFusedNumericInstruction.F64PromoteF32S(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64ReinterpretI64 -> strictI64BitsToF64(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64ReinterpretI64Dispatcher(RuntimeFusedNumericInstruction.F64ReinterpretI64I(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64ReinterpretI64Dispatcher(RuntimeFusedNumericInstruction.F64ReinterpretI64S(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Sqrt -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64SqrtDispatcher(RuntimeFusedNumericInstruction.F64SqrtI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64SqrtDispatcher(RuntimeFusedNumericInstruction.F64SqrtS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Trunc -> strictF64Unary(
        operand = instruction.operand,
        destination = instruction.destination,
        i = { operand, destinationSlot -> F64TruncDispatcher(RuntimeFusedNumericInstruction.F64TruncI(operand, destinationSlot)) },
        s = { operandSlot, destinationSlot -> F64TruncDispatcher(RuntimeFusedNumericInstruction.F64TruncS(operandSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Max -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64MaxDispatcher(RuntimeFusedNumericInstruction.F64MaxIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64MaxDispatcher(RuntimeFusedNumericInstruction.F64MaxIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64MaxDispatcher(RuntimeFusedNumericInstruction.F64MaxSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64MaxDispatcher(RuntimeFusedNumericInstruction.F64MaxSs(leftSlot, rightSlot, destinationSlot)) },
    )
    is FusedNumericInstruction.F64Min -> strictF64Binary(
        left = instruction.left,
        right = instruction.right,
        destination = instruction.destination,
        ii = { left, right, destinationSlot -> F64MinDispatcher(RuntimeFusedNumericInstruction.F64MinIi(left, right, destinationSlot)) },
        `is` = { left, rightSlot, destinationSlot -> F64MinDispatcher(RuntimeFusedNumericInstruction.F64MinIs(left, rightSlot, destinationSlot)) },
        si = { leftSlot, right, destinationSlot -> F64MinDispatcher(RuntimeFusedNumericInstruction.F64MinSi(leftSlot, right, destinationSlot)) },
        ss = { leftSlot, rightSlot, destinationSlot -> F64MinDispatcher(RuntimeFusedNumericInstruction.F64MinSs(leftSlot, rightSlot, destinationSlot)) },
    )
    else -> error("unsupported f64 fused instruction: $instruction")
}

private fun unsupportedUnloweredNumericInstruction(): DispatchableInstruction =
    error("numeric fused instruction must be frame-slot lowered to immediate and frame-slot operands before predecode")

private fun strictI32ConstInstruction(
    instruction: FusedNumericInstruction.I32Const,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(instruction.destination) ?: return unsupportedUnloweredNumericInstruction()
    return I32ConstDispatcher(RuntimeFusedNumericInstruction.I32ConstS(instruction.value, destinationSlot))
}

private fun strictI64ConstInstruction(
    instruction: FusedNumericInstruction.I64Const,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(instruction.destination) ?: return unsupportedUnloweredNumericInstruction()
    return I64ConstDispatcher(RuntimeFusedNumericInstruction.I64ConstS(instruction.value, destinationSlot))
}

private fun strictF32ConstInstruction(
    instruction: FusedNumericInstruction.F32Const,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(instruction.destination) ?: return unsupportedUnloweredNumericInstruction()
    return F32ConstDispatcher(RuntimeFusedNumericInstruction.F32ConstS(instruction.bits, destinationSlot))
}

private fun strictF64ConstInstruction(
    instruction: FusedNumericInstruction.F64Const,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(instruction.destination) ?: return unsupportedUnloweredNumericInstruction()
    return F64ConstDispatcher(RuntimeFusedNumericInstruction.F64ConstS(instruction.bits, destinationSlot))
}

private fun strictI64Add128(
    context: PredecodingContext,
    leftLow: FusedOperand,
    leftHigh: FusedOperand,
    rightLow: FusedOperand,
    rightHigh: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
): DispatchableInstruction {
    val destinationSlots = strictDualDestinationSlots(destinationLow, destinationHigh)
        ?: return unsupportedUnloweredNumericInstruction()
    val (destinationLowSlot, destinationHighSlot) = destinationSlots
    val leftLowSlot = strictI32OperandSlot(leftLow)
    val leftHighSlot = strictI32OperandSlot(leftHigh)
    val rightLowSlot = strictI32OperandSlot(rightLow)
    val rightHighSlot = strictI32OperandSlot(rightHigh)

    return when {
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Iiii(leftLow.const, leftHigh.const, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Iiis(leftLow.const, leftHigh.const, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Iisi(leftLow.const, leftHigh.const, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Iiss(leftLow.const, leftHigh.const, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Isii(leftLow.const, leftHighSlot, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Isis(leftLow.const, leftHighSlot, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Issi(leftLow.const, leftHighSlot, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLowSlot != null && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Isss(leftLow.const, leftHighSlot, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Siii(leftLowSlot, leftHigh.const, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Siis(leftLowSlot, leftHigh.const, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Sisi(leftLowSlot, leftHigh.const, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Siss(leftLowSlot, leftHigh.const, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Ssii(leftLowSlot, leftHighSlot, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Ssis(leftLowSlot, leftHighSlot, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Sssi(leftLowSlot, leftHighSlot, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLowSlot != null && rightHighSlot != null ->
            I64Add128Dispatcher(RuntimeFusedNumericInstruction.I64Add128Ssss(leftLowSlot, leftHighSlot, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private fun strictI64Sub128(
    context: PredecodingContext,
    leftLow: FusedOperand,
    leftHigh: FusedOperand,
    rightLow: FusedOperand,
    rightHigh: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
): DispatchableInstruction {
    val destinationSlots = strictDualDestinationSlots(destinationLow, destinationHigh)
        ?: return unsupportedUnloweredNumericInstruction()
    val (destinationLowSlot, destinationHighSlot) = destinationSlots
    val leftLowSlot = strictI32OperandSlot(leftLow)
    val leftHighSlot = strictI32OperandSlot(leftHigh)
    val rightLowSlot = strictI32OperandSlot(rightLow)
    val rightHighSlot = strictI32OperandSlot(rightHigh)

    return when {
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Iiii(leftLow.const, leftHigh.const, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Iiis(leftLow.const, leftHigh.const, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Iisi(leftLow.const, leftHigh.const, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Iiss(leftLow.const, leftHigh.const, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Isii(leftLow.const, leftHighSlot, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Isis(leftLow.const, leftHighSlot, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Issi(leftLow.const, leftHighSlot, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLow is FusedOperand.I64Const && leftHighSlot != null && rightLowSlot != null && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Isss(leftLow.const, leftHighSlot, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Siii(leftLowSlot, leftHigh.const, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Siis(leftLowSlot, leftHigh.const, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Sisi(leftLowSlot, leftHigh.const, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHigh is FusedOperand.I64Const && rightLowSlot != null && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Siss(leftLowSlot, leftHigh.const, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Ssii(leftLowSlot, leftHighSlot, rightLow.const, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLow is FusedOperand.I64Const && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Ssis(leftLowSlot, leftHighSlot, rightLow.const, rightHighSlot, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLowSlot != null && rightHigh is FusedOperand.I64Const ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Sssi(leftLowSlot, leftHighSlot, rightLowSlot, rightHigh.const, destinationLowSlot, destinationHighSlot))
        leftLowSlot != null && leftHighSlot != null && rightLowSlot != null && rightHighSlot != null ->
            I64Sub128Dispatcher(RuntimeFusedNumericInstruction.I64Sub128Ssss(leftLowSlot, leftHighSlot, rightLowSlot, rightHighSlot, destinationLowSlot, destinationHighSlot))
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64BinaryDualDestination(
    left: FusedOperand,
    right: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
    ii: (Long, Long, Int, Int) -> DispatchableInstruction,
    `is`: (Long, Int, Int, Int) -> DispatchableInstruction,
    si: (Int, Long, Int, Int) -> DispatchableInstruction,
    ss: (Int, Int, Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlots = strictDualDestinationSlots(destinationLow, destinationHigh) ?: return unsupportedUnloweredNumericInstruction()
    val (destinationLowSlot, destinationHighSlot) = destinationSlots
    val leftSlot = strictI32OperandSlot(left)
    val rightSlot = strictI32OperandSlot(right)

    return when {
        left is FusedOperand.I64Const && right is FusedOperand.I64Const -> ii(left.const, right.const, destinationLowSlot, destinationHighSlot)
        left is FusedOperand.I64Const && rightSlot != null -> `is`(left.const, rightSlot, destinationLowSlot, destinationHighSlot)
        leftSlot != null && right is FusedOperand.I64Const -> si(leftSlot, right.const, destinationLowSlot, destinationHighSlot)
        leftSlot != null && rightSlot != null -> ss(leftSlot, rightSlot, destinationLowSlot, destinationHighSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private fun strictI64MulWideSigned(
    context: PredecodingContext,
    left: FusedOperand,
    right: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
): DispatchableInstruction = strictI64BinaryDualDestination(
    left = left,
    right = right,
    destinationLow = destinationLow,
    destinationHigh = destinationHigh,
    ii = { left, right, destinationLowSlot, destinationHighSlot ->
        I64MulWideSDispatcher(RuntimeFusedNumericInstruction.I64MulWideSIi(left, right, destinationLowSlot, destinationHighSlot))
    },
    `is` = { left, rightSlot, destinationLowSlot, destinationHighSlot ->
        I64MulWideSDispatcher(RuntimeFusedNumericInstruction.I64MulWideSIs(left, rightSlot, destinationLowSlot, destinationHighSlot))
    },
    si = { leftSlot, right, destinationLowSlot, destinationHighSlot ->
        I64MulWideSDispatcher(RuntimeFusedNumericInstruction.I64MulWideSSi(leftSlot, right, destinationLowSlot, destinationHighSlot))
    },
    ss = { leftSlot, rightSlot, destinationLowSlot, destinationHighSlot ->
        I64MulWideSDispatcher(RuntimeFusedNumericInstruction.I64MulWideSSs(leftSlot, rightSlot, destinationLowSlot, destinationHighSlot))
    },
)

private fun strictI64MulWideUnsigned(
    context: PredecodingContext,
    left: FusedOperand,
    right: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
): DispatchableInstruction = strictI64BinaryDualDestination(
    left = left,
    right = right,
    destinationLow = destinationLow,
    destinationHigh = destinationHigh,
    ii = { left, right, destinationLowSlot, destinationHighSlot ->
        I64MulWideUDispatcher(RuntimeFusedNumericInstruction.I64MulWideUIi(left, right, destinationLowSlot, destinationHighSlot))
    },
    `is` = { left, rightSlot, destinationLowSlot, destinationHighSlot ->
        I64MulWideUDispatcher(RuntimeFusedNumericInstruction.I64MulWideUIs(left, rightSlot, destinationLowSlot, destinationHighSlot))
    },
    si = { leftSlot, right, destinationLowSlot, destinationHighSlot ->
        I64MulWideUDispatcher(RuntimeFusedNumericInstruction.I64MulWideUSi(leftSlot, right, destinationLowSlot, destinationHighSlot))
    },
    ss = { leftSlot, rightSlot, destinationLowSlot, destinationHighSlot ->
        I64MulWideUDispatcher(RuntimeFusedNumericInstruction.I64MulWideUSs(leftSlot, rightSlot, destinationLowSlot, destinationHighSlot))
    },
)

private inline fun strictI32Binary(
    left: FusedOperand,
    right: FusedOperand,
    destination: FusedDestination,
    ii: (Int, Int, Int) -> DispatchableInstruction,
    `is`: (Int, Int, Int) -> DispatchableInstruction,
    si: (Int, Int, Int) -> DispatchableInstruction,
    ss: (Int, Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val leftSlot = strictI32OperandSlot(left)
    val rightSlot = strictI32OperandSlot(right)

    return when {
        left is FusedOperand.I32Const && right is FusedOperand.I32Const -> ii(left.const, right.const, destinationSlot)
        left is FusedOperand.I32Const && rightSlot != null -> `is`(left.const, rightSlot, destinationSlot)
        leftSlot != null && right is FusedOperand.I32Const -> si(leftSlot, right.const, destinationSlot)
        leftSlot != null && rightSlot != null -> ss(leftSlot, rightSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI32Unary(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Int, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF32Binary(
    left: FusedOperand,
    right: FusedOperand,
    destination: FusedDestination,
    ii: (Float, Float, Int) -> DispatchableInstruction,
    `is`: (Float, Int, Int) -> DispatchableInstruction,
    si: (Int, Float, Int) -> DispatchableInstruction,
    ss: (Int, Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val leftSlot = strictI32OperandSlot(left)
    val rightSlot = strictI32OperandSlot(right)

    return when {
        left is FusedOperand.F32Const && right is FusedOperand.F32Const -> ii(left.const, right.const, destinationSlot)
        left is FusedOperand.F32Const && rightSlot != null -> `is`(left.const, rightSlot, destinationSlot)
        leftSlot != null && right is FusedOperand.F32Const -> si(leftSlot, right.const, destinationSlot)
        leftSlot != null && rightSlot != null -> ss(leftSlot, rightSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF32Unary(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Float, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF64Binary(
    left: FusedOperand,
    right: FusedOperand,
    destination: FusedDestination,
    ii: (Double, Double, Int) -> DispatchableInstruction,
    `is`: (Double, Int, Int) -> DispatchableInstruction,
    si: (Int, Double, Int) -> DispatchableInstruction,
    ss: (Int, Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val leftSlot = strictI32OperandSlot(left)
    val rightSlot = strictI32OperandSlot(right)

    return when {
        left is FusedOperand.F64Const && right is FusedOperand.F64Const -> ii(left.const, right.const, destinationSlot)
        left is FusedOperand.F64Const && rightSlot != null -> `is`(left.const, rightSlot, destinationSlot)
        leftSlot != null && right is FusedOperand.F64Const -> si(leftSlot, right.const, destinationSlot)
        leftSlot != null && rightSlot != null -> ss(leftSlot, rightSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF64Unary(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Double, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64Binary(
    left: FusedOperand,
    right: FusedOperand,
    destination: FusedDestination,
    ii: (Long, Long, Int) -> DispatchableInstruction,
    `is`: (Long, Int, Int) -> DispatchableInstruction,
    si: (Int, Long, Int) -> DispatchableInstruction,
    ss: (Int, Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val leftSlot = strictI32OperandSlot(left)
    val rightSlot = strictI32OperandSlot(right)

    return when {
        left is FusedOperand.I64Const && right is FusedOperand.I64Const -> ii(left.const, right.const, destinationSlot)
        left is FusedOperand.I64Const && rightSlot != null -> `is`(left.const, rightSlot, destinationSlot)
        leftSlot != null && right is FusedOperand.I64Const -> si(leftSlot, right.const, destinationSlot)
        leftSlot != null && rightSlot != null -> ss(leftSlot, rightSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64Unary(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Long, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI32ToF64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Int, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64ToF64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Long, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF32ToF64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Float, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64BitsToF64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Long, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI32ToF32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Int, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64ToF32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Long, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF64ToF32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Double, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI32BitsToF32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Int, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI32ToI64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Int, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF32ToI64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Float, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF64ToI64(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Double, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF32ToI32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Float, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F32Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictF64ToI32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Double, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.F64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private inline fun strictI64ToI32(
    operand: FusedOperand,
    destination: FusedDestination,
    i: (Long, Int) -> DispatchableInstruction,
    s: (Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val destinationSlot = strictI32DestinationSlot(destination) ?: return unsupportedUnloweredNumericInstruction()
    val operandSlot = strictI32OperandSlot(operand)

    return when {
        operand is FusedOperand.I64Const -> i(operand.const, destinationSlot)
        operandSlot != null -> s(operandSlot, destinationSlot)
        else -> unsupportedUnloweredNumericInstruction()
    }
}

private fun strictI32OperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictI32DestinationSlot(
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> destination.index.idx
    else -> null
}

private fun strictDualDestinationSlots(
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
): Pair<Int, Int>? {
    val lowSlot = strictI32DestinationSlot(destinationLow) ?: return null
    val highSlot = strictI32DestinationSlot(destinationHigh) ?: return null
    return lowSlot to highSlot
}

private fun Boolean.toWasmBool(): Long = if (this) 1L else 0L

private fun i32DivS(left: Int, right: Int): Int {
    if (left == Int.MIN_VALUE && right == -1) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }
    return try {
        left / right
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}

private fun i32DivU(left: Int, right: Int): Int = try {
    (left.toUInt() / right.toUInt()).toInt()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

private fun i32RemS(left: Int, right: Int): Int = try {
    left % right
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

private fun i32RemU(left: Int, right: Int): Int = try {
    (left.toUInt() % right.toUInt()).toInt()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

private fun i64DivS(left: Long, right: Long): Long {
    if (left == Long.MIN_VALUE && right == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }
    return try {
        left / right
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}

private fun i64DivU(left: Long, right: Long): Long = try {
    (left.toULong() / right.toULong()).toLong()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

private fun i64RemS(left: Long, right: Long): Long = try {
    left % right
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

private fun i64RemU(left: Long, right: Long): Long = try {
    (left.toULong() % right.toULong()).toLong()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

private fun f32ToI32Trapping(value: Float): Int = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated.toLong() > Int.MAX_VALUE || truncated.toLong() < Int.MIN_VALUE) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toInt()
    }
}

private fun f32ToI32UnsignedTrapping(value: Float): Int = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated < 0 || truncated.toLong() > UInt.MAX_VALUE.toLong()) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toUInt().toInt()
    }
}

private fun f32ToI64Trapping(value: Float): Long = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated.toULong() > Long.MAX_VALUE.toULong() || truncated < Long.MIN_VALUE) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toLong()
    }
}

private fun f32ToI64UnsignedTrapping(value: Float): Long = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated < 0 || truncated >= ULong.MAX_VALUE.toDouble()) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toULong().toLong()
    }
}

private fun f64ToI32Trapping(value: Double): Int = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated.toLong() > Int.MAX_VALUE || truncated.toLong() < Int.MIN_VALUE) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toInt()
    }
}

private fun f64ToI32UnsignedTrapping(value: Double): Int = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated < 0 || truncated.toLong() > UInt.MAX_VALUE.toLong()) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toUInt().toInt()
    }
}

private fun f64ToI64Trapping(value: Double): Long = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated.toULong() > Long.MAX_VALUE.toULong() || truncated < Long.MIN_VALUE) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toLong()
    }
}

private fun f64ToI64UnsignedTrapping(value: Double): Long = when {
    value.isNaN() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    value.isInfinite() -> throw InvocationException(InvocationError.ConvertOperationFailed)
    else -> {
        val truncated = truncate(value)
        if (truncated < 0 || truncated >= ULong.MAX_VALUE.toDouble()) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
        truncated.toULong().toLong()
    }
}

private fun u64ToF32(value: Long): Float {
    val unsigned = value.toULong()
    return if (unsigned < 0x10_0000_0000_0000uL) {
        unsigned.toFloat()
    } else {
        val roundBit = if (unsigned and 0xfffuL == 0uL) 0uL else 1uL
        val rounded = (unsigned shr 12) or roundBit
        rounded.toFloat() * 2.0f.pow(12)
    }
}

private fun mulWideUnsigned(a: ULong, b: ULong): Pair<ULong, ULong> {
    val mask32 = 0xFFFFFFFFUL
    val aLo = a and mask32
    val aHi = a shr 32
    val bLo = b and mask32
    val bHi = b shr 32
    val loLo = aLo * bLo
    val loHi = loLo shr 32
    val mid1 = aHi * bLo
    val mid2 = aLo * bHi
    val sum = loHi + (mid1 and mask32) + (mid2 and mask32)
    val low = (loLo and mask32) or ((sum and mask32) shl 32)
    val high = (aHi * bHi) + (mid1 shr 32) + (mid2 shr 32) + (sum shr 32)
    return low to high
}
