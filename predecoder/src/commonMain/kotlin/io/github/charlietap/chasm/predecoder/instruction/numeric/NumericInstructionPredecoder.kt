package io.github.charlietap.chasm.predecoder.instruction.numeric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CeilDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CopysignDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DemoteF64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DivDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32FloorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MaxDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MinDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NearestDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NegDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ReinterpretI32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SqrtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32TruncDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CeilDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CopysignDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64DivDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64FloorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MaxDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MinDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NearestDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NegDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64PromoteF32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ReinterpretI64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SqrtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64TruncDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AndDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ClzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32CtzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32Extend16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32Extend8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32OrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32PopcntDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ReinterpretF32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32WrapI64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32XorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AndDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ClzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64CtzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ExtendI32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ExtendI32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64NeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64OrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64PopcntDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ReinterpretF64Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF64UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64XorDispatcher
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Abs
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Add
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Ceil
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Const
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32ConvertI32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32ConvertI32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32ConvertI64S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32ConvertI64U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Copysign
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32DemoteF64
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Div
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Eq
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Floor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Ge
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Gt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Le
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Lt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Max
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Min
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Mul
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Ne
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Nearest
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Neg
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32ReinterpretI32
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Sqrt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Sub
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F32Trunc
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Abs
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Add
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Ceil
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Const
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64ConvertI32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64ConvertI32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64ConvertI64S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64ConvertI64U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Copysign
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Div
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Eq
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Floor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Ge
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Gt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Le
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Lt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Max
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Min
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Mul
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Ne
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Nearest
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Neg
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64PromoteF32
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64ReinterpretI64
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Sqrt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Sub
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.F64Trunc
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Add
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32And
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Clz
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Const
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Ctz
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32DivS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32DivU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Eq
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Eqz
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Extend16S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Extend8S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32GeS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32GeU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32GtS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32GtU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32LeS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32LeU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32LtS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32LtU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Mul
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Ne
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Or
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Popcnt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32ReinterpretF32
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32RemS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32RemU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Rotl
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Rotr
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Shl
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32ShrS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32ShrU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Sub
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncF32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncF32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncF64S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncF64U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncSatF32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncSatF32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncSatF64S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32TruncSatF64U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32WrapI64
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I32Xor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Add
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64And
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Clz
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Const
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Ctz
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64DivS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64DivU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Eq
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Eqz
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Extend16S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Extend32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Extend8S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64ExtendI32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64ExtendI32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64GeS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64GeU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64GtS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64GtU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64LeS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64LeU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64LtS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64LtU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Mul
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Ne
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Or
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Popcnt
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64ReinterpretF64
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64RemS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64RemU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Rotl
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Rotr
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Shl
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64ShrS
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64ShrU
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Sub
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncF32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncF32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncF64S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncF64U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncSatF32S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncSatF32U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncSatF64S
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64TruncSatF64U
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction.I64Xor

internal fun NumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: NumericInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is NumericInstruction.F32Const -> F32ConstDispatcher(F32Const(instruction.value))
        is NumericInstruction.F32Add -> F32AddDispatcher(F32Add)
        is NumericInstruction.F32Abs -> F32AbsDispatcher(F32Abs)
        is NumericInstruction.F32Sub -> F32SubDispatcher(F32Sub)
        is NumericInstruction.F32Mul -> F32MulDispatcher(F32Mul)
        is NumericInstruction.F32Div -> F32DivDispatcher(F32Div)
        is NumericInstruction.F32Min -> F32MinDispatcher(F32Min)
        is NumericInstruction.F32Max -> F32MaxDispatcher(F32Max)
        is NumericInstruction.F32Copysign -> F32CopysignDispatcher(F32Copysign)
        is NumericInstruction.F32Neg -> F32NegDispatcher(F32Neg)
        is NumericInstruction.F32Ceil -> F32CeilDispatcher(F32Ceil)
        is NumericInstruction.F32Floor -> F32FloorDispatcher(F32Floor)
        is NumericInstruction.F32Trunc -> F32TruncDispatcher(F32Trunc)
        is NumericInstruction.F32Nearest -> F32NearestDispatcher(F32Nearest)
        is NumericInstruction.F32Sqrt -> F32SqrtDispatcher(F32Sqrt)
        is NumericInstruction.F64Const -> F64ConstDispatcher(F64Const(instruction.value))
        is NumericInstruction.F64Add -> F64AddDispatcher(F64Add)
        is NumericInstruction.F64Sub -> F64SubDispatcher(F64Sub)
        is NumericInstruction.F64Mul -> F64MulDispatcher(F64Mul)
        is NumericInstruction.F64Div -> F64DivDispatcher(F64Div)
        is NumericInstruction.F64Min -> F64MinDispatcher(F64Min)
        is NumericInstruction.F64Max -> F64MaxDispatcher(F64Max)
        is NumericInstruction.F64Copysign -> F64CopysignDispatcher(F64Copysign)
        is NumericInstruction.F64Abs -> F64AbsDispatcher(F64Abs)
        is NumericInstruction.F64Neg -> F64NegDispatcher(F64Neg)
        is NumericInstruction.F64Ceil -> F64CeilDispatcher(F64Ceil)
        is NumericInstruction.F64Floor -> F64FloorDispatcher(F64Floor)
        is NumericInstruction.F64Trunc -> F64TruncDispatcher(F64Trunc)
        is NumericInstruction.F64Nearest -> F64NearestDispatcher(F64Nearest)
        is NumericInstruction.F64Sqrt -> F64SqrtDispatcher(F64Sqrt)
        is NumericInstruction.I32Const -> I32ConstDispatcher(I32Const(instruction.value))
        is NumericInstruction.I32Add -> I32AddDispatcher(I32Add)
        is NumericInstruction.I32Sub -> I32SubDispatcher(I32Sub)
        is NumericInstruction.I32Mul -> I32MulDispatcher(I32Mul)
        is NumericInstruction.I32DivS -> I32DivSDispatcher(I32DivS)
        is NumericInstruction.I32DivU -> I32DivUDispatcher(I32DivU)
        is NumericInstruction.I32RemS -> I32RemSDispatcher(I32RemS)
        is NumericInstruction.I32RemU -> I32RemUDispatcher(I32RemU)
        is NumericInstruction.I32And -> I32AndDispatcher(I32And)
        is NumericInstruction.I32Or -> I32OrDispatcher(I32Or)
        is NumericInstruction.I32Xor -> I32XorDispatcher(I32Xor)
        is NumericInstruction.I32Shl -> I32ShlDispatcher(I32Shl)
        is NumericInstruction.I32ShrS -> I32ShrSDispatcher(I32ShrS)
        is NumericInstruction.I32ShrU -> I32ShrUDispatcher(I32ShrU)
        is NumericInstruction.I32Rotl -> I32RotlDispatcher(I32Rotl)
        is NumericInstruction.I32Rotr -> I32RotrDispatcher(I32Rotr)
        is NumericInstruction.I32Clz -> I32ClzDispatcher(I32Clz)
        is NumericInstruction.I32Ctz -> I32CtzDispatcher(I32Ctz)
        is NumericInstruction.I32Popcnt -> I32PopcntDispatcher(I32Popcnt)
        is NumericInstruction.I64Const -> I64ConstDispatcher(I64Const(instruction.value))
        is NumericInstruction.I64Add -> I64AddDispatcher(I64Add)
        is NumericInstruction.I64Sub -> I64SubDispatcher(I64Sub)
        is NumericInstruction.I64Mul -> I64MulDispatcher(I64Mul)
        is NumericInstruction.I64DivS -> I64DivSDispatcher(I64DivS)
        is NumericInstruction.I64DivU -> I64DivUDispatcher(I64DivU)
        is NumericInstruction.I64RemS -> I64RemSDispatcher(I64RemS)
        is NumericInstruction.I64RemU -> I64RemUDispatcher(I64RemU)
        is NumericInstruction.I64And -> I64AndDispatcher(I64And)
        is NumericInstruction.I64Or -> I64OrDispatcher(I64Or)
        is NumericInstruction.I64Xor -> I64XorDispatcher(I64Xor)
        is NumericInstruction.I64Shl -> I64ShlDispatcher(I64Shl)
        is NumericInstruction.I64ShrS -> I64ShrSDispatcher(I64ShrS)
        is NumericInstruction.I64ShrU -> I64ShrUDispatcher(I64ShrU)
        is NumericInstruction.I64Rotl -> I64RotlDispatcher(I64Rotl)
        is NumericInstruction.I64Rotr -> I64RotrDispatcher(I64Rotr)
        is NumericInstruction.I64Clz -> I64ClzDispatcher(I64Clz)
        is NumericInstruction.I64Ctz -> I64CtzDispatcher(I64Ctz)
        is NumericInstruction.I64Popcnt -> I64PopcntDispatcher(I64Popcnt)
        is NumericInstruction.I32WrapI64 -> I32WrapI64Dispatcher(I32WrapI64)
        is NumericInstruction.I32TruncF32S -> I32TruncF32SDispatcher(I32TruncF32S)
        is NumericInstruction.I32TruncF32U -> I32TruncF32UDispatcher(I32TruncF32U)
        is NumericInstruction.I32TruncF64S -> I32TruncF64SDispatcher(I32TruncF64S)
        is NumericInstruction.I32TruncF64U -> I32TruncF64UDispatcher(I32TruncF64U)
        is NumericInstruction.I64ExtendI32S -> I64ExtendI32SDispatcher(I64ExtendI32S)
        is NumericInstruction.I64ExtendI32U -> I64ExtendI32UDispatcher(I64ExtendI32U)
        is NumericInstruction.I64TruncF32S -> I64TruncF32SDispatcher(I64TruncF32S)
        is NumericInstruction.I64TruncF32U -> I64TruncF32UDispatcher(I64TruncF32U)
        is NumericInstruction.I64TruncF64S -> I64TruncF64SDispatcher(I64TruncF64S)
        is NumericInstruction.I64TruncF64U -> I64TruncF64UDispatcher(I64TruncF64U)
        is NumericInstruction.F32ConvertI32S -> F32ConvertI32SDispatcher(F32ConvertI32S)
        is NumericInstruction.F32ConvertI32U -> F32ConvertI32UDispatcher(F32ConvertI32U)
        is NumericInstruction.F32ConvertI64S -> F32ConvertI64SDispatcher(F32ConvertI64S)
        is NumericInstruction.F32ConvertI64U -> F32ConvertI64UDispatcher(F32ConvertI64U)
        is NumericInstruction.F32DemoteF64 -> F32DemoteF64Dispatcher(F32DemoteF64)
        is NumericInstruction.F64ConvertI32S -> F64ConvertI32SDispatcher(F64ConvertI32S)
        is NumericInstruction.F64ConvertI32U -> F64ConvertI32UDispatcher(F64ConvertI32U)
        is NumericInstruction.F64ConvertI64S -> F64ConvertI64SDispatcher(F64ConvertI64S)
        is NumericInstruction.F64ConvertI64U -> F64ConvertI64UDispatcher(F64ConvertI64U)
        is NumericInstruction.F64PromoteF32 -> F64PromoteF32Dispatcher(F64PromoteF32)
        is NumericInstruction.I32ReinterpretF32 -> I32ReinterpretF32Dispatcher(I32ReinterpretF32)
        is NumericInstruction.I64ReinterpretF64 -> I64ReinterpretF64Dispatcher(I64ReinterpretF64)
        is NumericInstruction.F32ReinterpretI32 -> F32ReinterpretI32Dispatcher(F32ReinterpretI32)
        is NumericInstruction.F64ReinterpretI64 -> F64ReinterpretI64Dispatcher(F64ReinterpretI64)
        is NumericInstruction.I32TruncSatF32S -> I32TruncSatF32SDispatcher(I32TruncSatF32S)
        is NumericInstruction.I32TruncSatF32U -> I32TruncSatF32UDispatcher(I32TruncSatF32U)
        is NumericInstruction.I32TruncSatF64S -> I32TruncSatF64SDispatcher(I32TruncSatF64S)
        is NumericInstruction.I32TruncSatF64U -> I32TruncSatF64UDispatcher(I32TruncSatF64U)
        is NumericInstruction.I64TruncSatF32S -> I64TruncSatF32SDispatcher(I64TruncSatF32S)
        is NumericInstruction.I64TruncSatF32U -> I64TruncSatF32UDispatcher(I64TruncSatF32U)
        is NumericInstruction.I64TruncSatF64S -> I64TruncSatF64SDispatcher(I64TruncSatF64S)
        is NumericInstruction.I64TruncSatF64U -> I64TruncSatF64UDispatcher(I64TruncSatF64U)
        is NumericInstruction.I32Extend8S -> I32Extend8SDispatcher(I32Extend8S)
        is NumericInstruction.I32Extend16S -> I32Extend16SDispatcher(I32Extend16S)
        is NumericInstruction.I64Extend8S -> I64Extend8SDispatcher(I64Extend8S)
        is NumericInstruction.I64Extend16S -> I64Extend16SDispatcher(I64Extend16S)
        is NumericInstruction.I64Extend32S -> I64Extend32SDispatcher(I64Extend32S)
        is NumericInstruction.I32Eqz -> I32EqzDispatcher(I32Eqz)
        is NumericInstruction.I32Eq -> I32EqDispatcher(I32Eq)
        is NumericInstruction.I32Ne -> I32NeDispatcher(I32Ne)
        is NumericInstruction.I32LtS -> I32LtSDispatcher(I32LtS)
        is NumericInstruction.I32LtU -> I32LtUDispatcher(I32LtU)
        is NumericInstruction.I32GtS -> I32GtSDispatcher(I32GtS)
        is NumericInstruction.I32GtU -> I32GtUDispatcher(I32GtU)
        is NumericInstruction.I32LeS -> I32LeSDispatcher(I32LeS)
        is NumericInstruction.I32LeU -> I32LeUDispatcher(I32LeU)
        is NumericInstruction.I32GeS -> I32GeSDispatcher(I32GeS)
        is NumericInstruction.I32GeU -> I32GeUDispatcher(I32GeU)
        is NumericInstruction.I64Eqz -> I64EqzDispatcher(I64Eqz)
        is NumericInstruction.I64Eq -> I64EqDispatcher(I64Eq)
        is NumericInstruction.I64Ne -> I64NeDispatcher(I64Ne)
        is NumericInstruction.I64LtS -> I64LtSDispatcher(I64LtS)
        is NumericInstruction.I64LtU -> I64LtUDispatcher(I64LtU)
        is NumericInstruction.I64GtS -> I64GtSDispatcher(I64GtS)
        is NumericInstruction.I64GtU -> I64GtUDispatcher(I64GtU)
        is NumericInstruction.I64LeS -> I64LeSDispatcher(I64LeS)
        is NumericInstruction.I64LeU -> I64LeUDispatcher(I64LeU)
        is NumericInstruction.I64GeS -> I64GeSDispatcher(I64GeS)
        is NumericInstruction.I64GeU -> I64GeUDispatcher(I64GeU)
        is NumericInstruction.F32Eq -> F32EqDispatcher(F32Eq)
        is NumericInstruction.F32Ne -> F32NeDispatcher(F32Ne)
        is NumericInstruction.F32Lt -> F32LtDispatcher(F32Lt)
        is NumericInstruction.F32Gt -> F32GtDispatcher(F32Gt)
        is NumericInstruction.F32Le -> F32LeDispatcher(F32Le)
        is NumericInstruction.F32Ge -> F32GeDispatcher(F32Ge)
        is NumericInstruction.F64Eq -> F64EqDispatcher(F64Eq)
        is NumericInstruction.F64Ne -> F64NeDispatcher(F64Ne)
        is NumericInstruction.F64Lt -> F64LtDispatcher(F64Lt)
        is NumericInstruction.F64Gt -> F64GtDispatcher(F64Gt)
        is NumericInstruction.F64Le -> F64LeDispatcher(F64Le)
        is NumericInstruction.F64Ge -> F64GeDispatcher(F64Ge)
    }
}
