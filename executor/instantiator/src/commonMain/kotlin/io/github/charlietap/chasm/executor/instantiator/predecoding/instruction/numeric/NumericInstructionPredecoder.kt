package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.numeric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
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
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Abs
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Add
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Ceil
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Const
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32ConvertI32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32ConvertI32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32ConvertI64S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32ConvertI64U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Copysign
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32DemoteF64
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Div
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Eq
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Floor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Ge
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Gt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Le
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Lt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Max
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Min
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Mul
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Ne
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Nearest
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Neg
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32ReinterpretI32
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Sqrt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Sub
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F32Trunc
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Abs
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Add
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Ceil
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Const
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64ConvertI32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64ConvertI32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64ConvertI64S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64ConvertI64U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Copysign
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Div
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Eq
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Floor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Ge
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Gt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Le
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Lt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Max
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Min
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Mul
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Ne
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Nearest
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Neg
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64PromoteF32
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64ReinterpretI64
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Sqrt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Sub
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.F64Trunc
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Add
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32And
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Clz
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Const
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Ctz
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32DivS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32DivU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Eq
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Eqz
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Extend16S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Extend8S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32GeS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32GeU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32GtS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32GtU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32LeS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32LeU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32LtS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32LtU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Mul
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Ne
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Or
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Popcnt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32ReinterpretF32
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32RemS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32RemU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Rotl
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Rotr
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Shl
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32ShrS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32ShrU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Sub
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncF32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncF32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncF64S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncF64U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncSatF32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncSatF32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncSatF64S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32TruncSatF64U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32WrapI64
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I32Xor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Add
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64And
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Clz
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Const
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Ctz
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64DivS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64DivU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Eq
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Eqz
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Extend16S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Extend32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Extend8S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64ExtendI32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64ExtendI32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64GeS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64GeU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64GtS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64GtU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64LeS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64LeU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64LtS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64LtU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Mul
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Ne
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Or
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Popcnt
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64ReinterpretF64
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64RemS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64RemU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Rotl
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Rotr
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Shl
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64ShrS
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64ShrU
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Sub
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncF32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncF32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncF64S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncF64U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncSatF32S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncSatF32U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncSatF64S
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64TruncSatF64U
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction.I64Xor

internal fun NumericInstructionPredecoder(
    context: InstantiationContext,
    instruction: NumericInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    NumericInstructionPredecoder(
        context = context,
        instruction = instruction,
        f32ConstDispatcher = ::F32ConstDispatcher,
        f64ConstDispatcher = ::F64ConstDispatcher,
        i32ConstDispatcher = ::I32ConstDispatcher,
        i64ConstDispatcher = ::I64ConstDispatcher,
        f32AddDispatcher = ::F32AddDispatcher,
        f32SubDispatcher = ::F32SubDispatcher,
        f32MulDispatcher = ::F32MulDispatcher,
        f32DivDispatcher = ::F32DivDispatcher,
        f32MinDispatcher = ::F32MinDispatcher,
        f32MaxDispatcher = ::F32MaxDispatcher,
        f32CopysignDispatcher = ::F32CopysignDispatcher,
        f32AbsDispatcher = ::F32AbsDispatcher,
        f32NegDispatcher = ::F32NegDispatcher,
        f32CeilDispatcher = ::F32CeilDispatcher,
        f32FloorDispatcher = ::F32FloorDispatcher,
        f32TruncDispatcher = ::F32TruncDispatcher,
        f32NearestDispatcher = ::F32NearestDispatcher,
        f32SqrtDispatcher = ::F32SqrtDispatcher,
        f64AddDispatcher = ::F64AddDispatcher,
        f64SubDispatcher = ::F64SubDispatcher,
        f64MulDispatcher = ::F64MulDispatcher,
        f64DivDispatcher = ::F64DivDispatcher,
        f64MinDispatcher = ::F64MinDispatcher,
        f64MaxDispatcher = ::F64MaxDispatcher,
        f64CopysignDispatcher = ::F64CopysignDispatcher,
        f64AbsDispatcher = ::F64AbsDispatcher,
        f64NegDispatcher = ::F64NegDispatcher,
        f64CeilDispatcher = ::F64CeilDispatcher,
        f64FloorDispatcher = ::F64FloorDispatcher,
        f64TruncDispatcher = ::F64TruncDispatcher,
        f64NearestDispatcher = ::F64NearestDispatcher,
        f64SqrtDispatcher = ::F64SqrtDispatcher,
        i32AddDispatcher = ::I32AddDispatcher,
        i32SubDispatcher = ::I32SubDispatcher,
        i32MulDispatcher = ::I32MulDispatcher,
        i32DivSDispatcher = ::I32DivSDispatcher,
        i32DivUDispatcher = ::I32DivUDispatcher,
        i32RemSDispatcher = ::I32RemSDispatcher,
        i32RemUDispatcher = ::I32RemUDispatcher,
        i32AndDispatcher = ::I32AndDispatcher,
        i32OrDispatcher = ::I32OrDispatcher,
        i32XorDispatcher = ::I32XorDispatcher,
        i32ShlDispatcher = ::I32ShlDispatcher,
        i32ShrSDispatcher = ::I32ShrSDispatcher,
        i32ShrUDispatcher = ::I32ShrUDispatcher,
        i32RotlDispatcher = ::I32RotlDispatcher,
        i32RotrDispatcher = ::I32RotrDispatcher,
        i32ClzDispatcher = ::I32ClzDispatcher,
        i32CtzDispatcher = ::I32CtzDispatcher,
        i32PopcntDispatcher = ::I32PopcntDispatcher,
        i32WrapI64Dispatcher = ::I32WrapI64Dispatcher,
        i32ReinterpretF32Dispatcher = ::I32ReinterpretF32Dispatcher,
        i32TruncF32SDispatcher = ::I32TruncF32SDispatcher,
        i32TruncF32UDispatcher = ::I32TruncF32UDispatcher,
        i32TruncF64SDispatcher = ::I32TruncF64SDispatcher,
        i32TruncF64UDispatcher = ::I32TruncF64UDispatcher,
        i32TruncSatF32SDispatcher = ::I32TruncSatF32SDispatcher,
        i32TruncSatF32UDispatcher = ::I32TruncSatF32UDispatcher,
        i32TruncSatF64SDispatcher = ::I32TruncSatF64SDispatcher,
        i32TruncSatF64UDispatcher = ::I32TruncSatF64UDispatcher,
        i64AddDispatcher = ::I64AddDispatcher,
        i64SubDispatcher = ::I64SubDispatcher,
        i64MulDispatcher = ::I64MulDispatcher,
        i64DivSDispatcher = ::I64DivSDispatcher,
        i64DivUDispatcher = ::I64DivUDispatcher,
        i64RemSDispatcher = ::I64RemSDispatcher,
        i64RemUDispatcher = ::I64RemUDispatcher,
        i64AndDispatcher = ::I64AndDispatcher,
        i64OrDispatcher = ::I64OrDispatcher,
        i64XorDispatcher = ::I64XorDispatcher,
        i64ShlDispatcher = ::I64ShlDispatcher,
        i64ShrSDispatcher = ::I64ShrSDispatcher,
        i64ShrUDispatcher = ::I64ShrUDispatcher,
        i64RotlDispatcher = ::I64RotlDispatcher,
        i64RotrDispatcher = ::I64RotrDispatcher,
        i64ClzDispatcher = ::I64ClzDispatcher,
        i64CtzDispatcher = ::I64CtzDispatcher,
        i64PopcntDispatcher = ::I64PopcntDispatcher,
        i64ExtendI32SDispatcher = ::I64ExtendI32SDispatcher,
        i64ExtendI32UDispatcher = ::I64ExtendI32UDispatcher,
        i64Extend8SDispatcher = ::I64Extend8SDispatcher,
        i64Extend16SDispatcher = ::I64Extend16SDispatcher,
        i64Extend32SDispatcher = ::I64Extend32SDispatcher,
        i64ReinterpretF64Dispatcher = ::I64ReinterpretF64Dispatcher,
        i64TruncF32SDispatcher = ::I64TruncF32SDispatcher,
        i64TruncF32UDispatcher = ::I64TruncF32UDispatcher,
        i64TruncF64SDispatcher = ::I64TruncF64SDispatcher,
        i64TruncF64UDispatcher = ::I64TruncF64UDispatcher,
        i64TruncSatF32SDispatcher = ::I64TruncSatF32SDispatcher,
        i64TruncSatF32UDispatcher = ::I64TruncSatF32UDispatcher,
        i64TruncSatF64SDispatcher = ::I64TruncSatF64SDispatcher,
        i64TruncSatF64UDispatcher = ::I64TruncSatF64UDispatcher,
        f32ConvertI32SDispatcher = ::F32ConvertI32SDispatcher,
        f32ConvertI32UDispatcher = ::F32ConvertI32UDispatcher,
        f32ConvertI64SDispatcher = ::F32ConvertI64SDispatcher,
        f32ConvertI64UDispatcher = ::F32ConvertI64UDispatcher,
        f32DemoteF64Dispatcher = ::F32DemoteF64Dispatcher,
        f64ConvertI32SDispatcher = ::F64ConvertI32SDispatcher,
        f64ConvertI32UDispatcher = ::F64ConvertI32UDispatcher,
        f64ConvertI64SDispatcher = ::F64ConvertI64SDispatcher,
        f64ConvertI64UDispatcher = ::F64ConvertI64UDispatcher,
        f64PromoteF32Dispatcher = ::F64PromoteF32Dispatcher,
        f64ReinterpretI64Dispatcher = ::F64ReinterpretI64Dispatcher,
        f32EqDispatcher = ::F32EqDispatcher,
        f32GeDispatcher = ::F32GeDispatcher,
        f32GtDispatcher = ::F32GtDispatcher,
        f32LeDispatcher = ::F32LeDispatcher,
        f32LtDispatcher = ::F32LtDispatcher,
        f32NeDispatcher = ::F32NeDispatcher,
        f32ReinterpretI32Dispatcher = ::F32ReinterpretI32Dispatcher,
        f64EqDispatcher = ::F64EqDispatcher,
        f64GeDispatcher = ::F64GeDispatcher,
        f64GtDispatcher = ::F64GtDispatcher,
        f64LeDispatcher = ::F64LeDispatcher,
        f64LtDispatcher = ::F64LtDispatcher,
        f64NeDispatcher = ::F64NeDispatcher,
        i32EqDispatcher = ::I32EqDispatcher,
        i32EqzDispatcher = ::I32EqzDispatcher,
        i32Extend16SDispatcher = ::I32Extend16SDispatcher,
        i32Extend8SDispatcher = ::I32Extend8SDispatcher,
        i32GeSDispatcher = ::I32GeSDispatcher,
        i32GeUDispatcher = ::I32GeUDispatcher,
        i32GtSDispatcher = ::I32GtSDispatcher,
        i32GtUDispatcher = ::I32GtUDispatcher,
        i32LeSDispatcher = ::I32LeSDispatcher,
        i32LeUDispatcher = ::I32LeUDispatcher,
        i32LtSDispatcher = ::I32LtSDispatcher,
        i32LtUDispatcher = ::I32LtUDispatcher,
        i32NeDispatcher = ::I32NeDispatcher,
        i64EqDispatcher = ::I64EqDispatcher,
        i64EqzDispatcher = ::I64EqzDispatcher,
        i64GeSDispatcher = ::I64GeSDispatcher,
        i64GeUDispatcher = ::I64GeUDispatcher,
        i64GtSDispatcher = ::I64GtSDispatcher,
        i64GtUDispatcher = ::I64GtUDispatcher,
        i64LeSDispatcher = ::I64LeSDispatcher,
        i64LeUDispatcher = ::I64LeUDispatcher,
        i64LtSDispatcher = ::I64LtSDispatcher,
        i64LtUDispatcher = ::I64LtUDispatcher,
        i64NeDispatcher = ::I64NeDispatcher,
    )

internal inline fun NumericInstructionPredecoder(
    context: InstantiationContext,
    instruction: NumericInstruction,
    crossinline f32ConstDispatcher: Dispatcher<F32Const>,
    crossinline f64ConstDispatcher: Dispatcher<F64Const>,
    crossinline i32ConstDispatcher: Dispatcher<I32Const>,
    crossinline i64ConstDispatcher: Dispatcher<I64Const>,
    crossinline f32AddDispatcher: Dispatcher<F32Add>,
    crossinline f32SubDispatcher: Dispatcher<F32Sub>,
    crossinline f32MulDispatcher: Dispatcher<F32Mul>,
    crossinline f32DivDispatcher: Dispatcher<F32Div>,
    crossinline f32MinDispatcher: Dispatcher<F32Min>,
    crossinline f32MaxDispatcher: Dispatcher<F32Max>,
    crossinline f32CopysignDispatcher: Dispatcher<F32Copysign>,
    crossinline f32AbsDispatcher: Dispatcher<F32Abs>,
    crossinline f32NegDispatcher: Dispatcher<F32Neg>,
    crossinline f32CeilDispatcher: Dispatcher<F32Ceil>,
    crossinline f32FloorDispatcher: Dispatcher<F32Floor>,
    crossinline f32TruncDispatcher: Dispatcher<F32Trunc>,
    crossinline f32NearestDispatcher: Dispatcher<F32Nearest>,
    crossinline f32SqrtDispatcher: Dispatcher<F32Sqrt>,
    crossinline f64AddDispatcher: Dispatcher<F64Add>,
    crossinline f64SubDispatcher: Dispatcher<F64Sub>,
    crossinline f64MulDispatcher: Dispatcher<F64Mul>,
    crossinline f64DivDispatcher: Dispatcher<F64Div>,
    crossinline f64MinDispatcher: Dispatcher<F64Min>,
    crossinline f64MaxDispatcher: Dispatcher<F64Max>,
    crossinline f64CopysignDispatcher: Dispatcher<F64Copysign>,
    crossinline f64AbsDispatcher: Dispatcher<F64Abs>,
    crossinline f64NegDispatcher: Dispatcher<F64Neg>,
    crossinline f64CeilDispatcher: Dispatcher<F64Ceil>,
    crossinline f64FloorDispatcher: Dispatcher<F64Floor>,
    crossinline f64TruncDispatcher: Dispatcher<F64Trunc>,
    crossinline f64NearestDispatcher: Dispatcher<F64Nearest>,
    crossinline f64SqrtDispatcher: Dispatcher<F64Sqrt>,
    crossinline i32AddDispatcher: Dispatcher<I32Add>,
    crossinline i32SubDispatcher: Dispatcher<I32Sub>,
    crossinline i32MulDispatcher: Dispatcher<I32Mul>,
    crossinline i32DivSDispatcher: Dispatcher<I32DivS>,
    crossinline i32DivUDispatcher: Dispatcher<I32DivU>,
    crossinline i32RemSDispatcher: Dispatcher<I32RemS>,
    crossinline i32RemUDispatcher: Dispatcher<I32RemU>,
    crossinline i32AndDispatcher: Dispatcher<I32And>,
    crossinline i32OrDispatcher: Dispatcher<I32Or>,
    crossinline i32XorDispatcher: Dispatcher<I32Xor>,
    crossinline i32ShlDispatcher: Dispatcher<I32Shl>,
    crossinline i32ShrSDispatcher: Dispatcher<I32ShrS>,
    crossinline i32ShrUDispatcher: Dispatcher<I32ShrU>,
    crossinline i32RotlDispatcher: Dispatcher<I32Rotl>,
    crossinline i32RotrDispatcher: Dispatcher<I32Rotr>,
    crossinline i32ClzDispatcher: Dispatcher<I32Clz>,
    crossinline i32CtzDispatcher: Dispatcher<I32Ctz>,
    crossinline i32PopcntDispatcher: Dispatcher<I32Popcnt>,
    crossinline i32WrapI64Dispatcher: Dispatcher<I32WrapI64>,
    crossinline i32ReinterpretF32Dispatcher: Dispatcher<I32ReinterpretF32>,
    crossinline i32TruncF32SDispatcher: Dispatcher<I32TruncF32S>,
    crossinline i32TruncF32UDispatcher: Dispatcher<I32TruncF32U>,
    crossinline i32TruncF64SDispatcher: Dispatcher<I32TruncF64S>,
    crossinline i32TruncF64UDispatcher: Dispatcher<I32TruncF64U>,
    crossinline i32TruncSatF32SDispatcher: Dispatcher<I32TruncSatF32S>,
    crossinline i32TruncSatF32UDispatcher: Dispatcher<I32TruncSatF32U>,
    crossinline i32TruncSatF64SDispatcher: Dispatcher<I32TruncSatF64S>,
    crossinline i32TruncSatF64UDispatcher: Dispatcher<I32TruncSatF64U>,
    crossinline i64AddDispatcher: Dispatcher<I64Add>,
    crossinline i64SubDispatcher: Dispatcher<I64Sub>,
    crossinline i64MulDispatcher: Dispatcher<I64Mul>,
    crossinline i64DivSDispatcher: Dispatcher<I64DivS>,
    crossinline i64DivUDispatcher: Dispatcher<I64DivU>,
    crossinline i64RemSDispatcher: Dispatcher<I64RemS>,
    crossinline i64RemUDispatcher: Dispatcher<I64RemU>,
    crossinline i64AndDispatcher: Dispatcher<I64And>,
    crossinline i64OrDispatcher: Dispatcher<I64Or>,
    crossinline i64XorDispatcher: Dispatcher<I64Xor>,
    crossinline i64ShlDispatcher: Dispatcher<I64Shl>,
    crossinline i64ShrSDispatcher: Dispatcher<I64ShrS>,
    crossinline i64ShrUDispatcher: Dispatcher<I64ShrU>,
    crossinline i64RotlDispatcher: Dispatcher<I64Rotl>,
    crossinline i64RotrDispatcher: Dispatcher<I64Rotr>,
    crossinline i64ClzDispatcher: Dispatcher<I64Clz>,
    crossinline i64CtzDispatcher: Dispatcher<I64Ctz>,
    crossinline i64PopcntDispatcher: Dispatcher<I64Popcnt>,
    crossinline i64ExtendI32SDispatcher: Dispatcher<I64ExtendI32S>,
    crossinline i64ExtendI32UDispatcher: Dispatcher<I64ExtendI32U>,
    crossinline i64Extend8SDispatcher: Dispatcher<I64Extend8S>,
    crossinline i64Extend16SDispatcher: Dispatcher<I64Extend16S>,
    crossinline i64Extend32SDispatcher: Dispatcher<I64Extend32S>,
    crossinline i64ReinterpretF64Dispatcher: Dispatcher<I64ReinterpretF64>,
    crossinline i64TruncF32SDispatcher: Dispatcher<I64TruncF32S>,
    crossinline i64TruncF32UDispatcher: Dispatcher<I64TruncF32U>,
    crossinline i64TruncF64SDispatcher: Dispatcher<I64TruncF64S>,
    crossinline i64TruncF64UDispatcher: Dispatcher<I64TruncF64U>,
    crossinline i64TruncSatF32SDispatcher: Dispatcher<I64TruncSatF32S>,
    crossinline i64TruncSatF32UDispatcher: Dispatcher<I64TruncSatF32U>,
    crossinline i64TruncSatF64SDispatcher: Dispatcher<I64TruncSatF64S>,
    crossinline i64TruncSatF64UDispatcher: Dispatcher<I64TruncSatF64U>,
    crossinline f32ConvertI32SDispatcher: Dispatcher<F32ConvertI32S>,
    crossinline f32ConvertI32UDispatcher: Dispatcher<F32ConvertI32U>,
    crossinline f32ConvertI64SDispatcher: Dispatcher<F32ConvertI64S>,
    crossinline f32ConvertI64UDispatcher: Dispatcher<F32ConvertI64U>,
    crossinline f32DemoteF64Dispatcher: Dispatcher<F32DemoteF64>,
    crossinline f64ConvertI32SDispatcher: Dispatcher<F64ConvertI32S>,
    crossinline f64ConvertI32UDispatcher: Dispatcher<F64ConvertI32U>,
    crossinline f64ConvertI64SDispatcher: Dispatcher<F64ConvertI64S>,
    crossinline f64ConvertI64UDispatcher: Dispatcher<F64ConvertI64U>,
    crossinline f64PromoteF32Dispatcher: Dispatcher<F64PromoteF32>,
    crossinline f64ReinterpretI64Dispatcher: Dispatcher<F64ReinterpretI64>,
    crossinline f32EqDispatcher: Dispatcher<F32Eq>,
    crossinline f32GeDispatcher: Dispatcher<F32Ge>,
    crossinline f32GtDispatcher: Dispatcher<F32Gt>,
    crossinline f32LeDispatcher: Dispatcher<F32Le>,
    crossinline f32LtDispatcher: Dispatcher<F32Lt>,
    crossinline f32NeDispatcher: Dispatcher<F32Ne>,
    crossinline f32ReinterpretI32Dispatcher: Dispatcher<F32ReinterpretI32>,
    crossinline f64EqDispatcher: Dispatcher<F64Eq>,
    crossinline f64GeDispatcher: Dispatcher<F64Ge>,
    crossinline f64GtDispatcher: Dispatcher<F64Gt>,
    crossinline f64LeDispatcher: Dispatcher<F64Le>,
    crossinline f64LtDispatcher: Dispatcher<F64Lt>,
    crossinline f64NeDispatcher: Dispatcher<F64Ne>,
    crossinline i32EqDispatcher: Dispatcher<I32Eq>,
    crossinline i32EqzDispatcher: Dispatcher<I32Eqz>,
    crossinline i32Extend16SDispatcher: Dispatcher<I32Extend16S>,
    crossinline i32Extend8SDispatcher: Dispatcher<I32Extend8S>,
    crossinline i32GeSDispatcher: Dispatcher<I32GeS>,
    crossinline i32GeUDispatcher: Dispatcher<I32GeU>,
    crossinline i32GtSDispatcher: Dispatcher<I32GtS>,
    crossinline i32GtUDispatcher: Dispatcher<I32GtU>,
    crossinline i32LeSDispatcher: Dispatcher<I32LeS>,
    crossinline i32LeUDispatcher: Dispatcher<I32LeU>,
    crossinline i32LtSDispatcher: Dispatcher<I32LtS>,
    crossinline i32LtUDispatcher: Dispatcher<I32LtU>,
    crossinline i32NeDispatcher: Dispatcher<I32Ne>,
    crossinline i64EqDispatcher: Dispatcher<I64Eq>,
    crossinline i64EqzDispatcher: Dispatcher<I64Eqz>,
    crossinline i64GeSDispatcher: Dispatcher<I64GeS>,
    crossinline i64GeUDispatcher: Dispatcher<I64GeU>,
    crossinline i64GtSDispatcher: Dispatcher<I64GtS>,
    crossinline i64GtUDispatcher: Dispatcher<I64GtU>,
    crossinline i64LeSDispatcher: Dispatcher<I64LeS>,
    crossinline i64LeUDispatcher: Dispatcher<I64LeU>,
    crossinline i64LtSDispatcher: Dispatcher<I64LtS>,
    crossinline i64LtUDispatcher: Dispatcher<I64LtU>,
    crossinline i64NeDispatcher: Dispatcher<I64Ne>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is NumericInstruction.F32Const -> f32ConstDispatcher(F32Const(instruction.value))
        is NumericInstruction.F64Const -> f64ConstDispatcher(F64Const(instruction.value))
        is NumericInstruction.I32Const -> i32ConstDispatcher(I32Const(instruction.value))
        is NumericInstruction.I64Const -> i64ConstDispatcher(I64Const(instruction.value))
        is NumericInstruction.F32Add -> f32AddDispatcher(F32Add)
        is NumericInstruction.F32Sub -> f32SubDispatcher(F32Sub)
        is NumericInstruction.F32Mul -> f32MulDispatcher(F32Mul)
        is NumericInstruction.F32Div -> f32DivDispatcher(F32Div)
        is NumericInstruction.F32Min -> f32MinDispatcher(F32Min)
        is NumericInstruction.F32Max -> f32MaxDispatcher(F32Max)
        is NumericInstruction.F32Copysign -> f32CopysignDispatcher(F32Copysign)
        is NumericInstruction.F32Abs -> f32AbsDispatcher(F32Abs)
        is NumericInstruction.F32Neg -> f32NegDispatcher(F32Neg)
        is NumericInstruction.F32Ceil -> f32CeilDispatcher(F32Ceil)
        is NumericInstruction.F32Floor -> f32FloorDispatcher(F32Floor)
        is NumericInstruction.F32Trunc -> f32TruncDispatcher(F32Trunc)
        is NumericInstruction.F32Nearest -> f32NearestDispatcher(F32Nearest)
        is NumericInstruction.F32Sqrt -> f32SqrtDispatcher(F32Sqrt)
        is NumericInstruction.F64Add -> f64AddDispatcher(F64Add)
        is NumericInstruction.F64Sub -> f64SubDispatcher(F64Sub)
        is NumericInstruction.F64Mul -> f64MulDispatcher(F64Mul)
        is NumericInstruction.F64Div -> f64DivDispatcher(F64Div)
        is NumericInstruction.F64Min -> f64MinDispatcher(F64Min)
        is NumericInstruction.F64Max -> f64MaxDispatcher(F64Max)
        is NumericInstruction.F64Copysign -> f64CopysignDispatcher(F64Copysign)
        is NumericInstruction.F64Abs -> f64AbsDispatcher(F64Abs)
        is NumericInstruction.F64Neg -> f64NegDispatcher(F64Neg)
        is NumericInstruction.F64Ceil -> f64CeilDispatcher(F64Ceil)
        is NumericInstruction.F64Floor -> f64FloorDispatcher(F64Floor)
        is NumericInstruction.F64Trunc -> f64TruncDispatcher(F64Trunc)
        is NumericInstruction.F64Nearest -> f64NearestDispatcher(F64Nearest)
        is NumericInstruction.F64Sqrt -> f64SqrtDispatcher(F64Sqrt)
        is NumericInstruction.I32Add -> i32AddDispatcher(I32Add)
        is NumericInstruction.I32Sub -> i32SubDispatcher(I32Sub)
        is NumericInstruction.I32Mul -> i32MulDispatcher(I32Mul)
        is NumericInstruction.I32DivS -> i32DivSDispatcher(I32DivS)
        is NumericInstruction.I32DivU -> i32DivUDispatcher(I32DivU)
        is NumericInstruction.I32RemS -> i32RemSDispatcher(I32RemS)
        is NumericInstruction.I32RemU -> i32RemUDispatcher(I32RemU)
        is NumericInstruction.I32And -> i32AndDispatcher(I32And)
        is NumericInstruction.I32Or -> i32OrDispatcher(I32Or)
        is NumericInstruction.I32Xor -> i32XorDispatcher(I32Xor)
        is NumericInstruction.I32Shl -> i32ShlDispatcher(I32Shl)
        is NumericInstruction.I32ShrS -> i32ShrSDispatcher(I32ShrS)
        is NumericInstruction.I32ShrU -> i32ShrUDispatcher(I32ShrU)
        is NumericInstruction.I32Rotl -> i32RotlDispatcher(I32Rotl)
        is NumericInstruction.I32Rotr -> i32RotrDispatcher(I32Rotr)
        is NumericInstruction.I32Clz -> i32ClzDispatcher(I32Clz)
        is NumericInstruction.I32Ctz -> i32CtzDispatcher(I32Ctz)
        is NumericInstruction.I32Popcnt -> i32PopcntDispatcher(I32Popcnt)
        is NumericInstruction.I32WrapI64 -> i32WrapI64Dispatcher(I32WrapI64)
        is NumericInstruction.I32ReinterpretF32 -> i32ReinterpretF32Dispatcher(I32ReinterpretF32)
        is NumericInstruction.I32TruncF32S -> i32TruncF32SDispatcher(I32TruncF32S)
        is NumericInstruction.I32TruncF32U -> i32TruncF32UDispatcher(I32TruncF32U)
        is NumericInstruction.I32TruncF64S -> i32TruncF64SDispatcher(I32TruncF64S)
        is NumericInstruction.I32TruncF64U -> i32TruncF64UDispatcher(I32TruncF64U)
        is NumericInstruction.I32TruncSatF32S -> i32TruncSatF32SDispatcher(I32TruncSatF32S)
        is NumericInstruction.I32TruncSatF32U -> i32TruncSatF32UDispatcher(I32TruncSatF32U)
        is NumericInstruction.I32TruncSatF64S -> i32TruncSatF64SDispatcher(I32TruncSatF64S)
        is NumericInstruction.I32TruncSatF64U -> i32TruncSatF64UDispatcher(I32TruncSatF64U)
        is NumericInstruction.I64Add -> i64AddDispatcher(I64Add)
        is NumericInstruction.I64Sub -> i64SubDispatcher(I64Sub)
        is NumericInstruction.I64Mul -> i64MulDispatcher(I64Mul)
        is NumericInstruction.I64DivS -> i64DivSDispatcher(I64DivS)
        is NumericInstruction.I64DivU -> i64DivUDispatcher(I64DivU)
        is NumericInstruction.I64RemS -> i64RemSDispatcher(I64RemS)
        is NumericInstruction.I64RemU -> i64RemUDispatcher(I64RemU)
        is NumericInstruction.I64And -> i64AndDispatcher(I64And)
        is NumericInstruction.I64Or -> i64OrDispatcher(I64Or)
        is NumericInstruction.I64Xor -> i64XorDispatcher(I64Xor)
        is NumericInstruction.I64Shl -> i64ShlDispatcher(I64Shl)
        is NumericInstruction.I64ShrS -> i64ShrSDispatcher(I64ShrS)
        is NumericInstruction.I64ShrU -> i64ShrUDispatcher(I64ShrU)
        is NumericInstruction.I64Rotl -> i64RotlDispatcher(I64Rotl)
        is NumericInstruction.I64Rotr -> i64RotrDispatcher(I64Rotr)
        is NumericInstruction.I64Clz -> i64ClzDispatcher(I64Clz)
        is NumericInstruction.I64Ctz -> i64CtzDispatcher(I64Ctz)
        is NumericInstruction.I64Popcnt -> i64PopcntDispatcher(I64Popcnt)
        is NumericInstruction.I64ExtendI32S -> i64ExtendI32SDispatcher(I64ExtendI32S)
        is NumericInstruction.I64ExtendI32U -> i64ExtendI32UDispatcher(I64ExtendI32U)
        is NumericInstruction.I64Extend8S -> i64Extend8SDispatcher(I64Extend8S)
        is NumericInstruction.I64Extend16S -> i64Extend16SDispatcher(I64Extend16S)
        is NumericInstruction.I64Extend32S -> i64Extend32SDispatcher(I64Extend32S)
        is NumericInstruction.I64ReinterpretF64 -> i64ReinterpretF64Dispatcher(I64ReinterpretF64)
        is NumericInstruction.I64TruncF32S -> i64TruncF32SDispatcher(I64TruncF32S)
        is NumericInstruction.I64TruncF32U -> i64TruncF32UDispatcher(I64TruncF32U)
        is NumericInstruction.I64TruncF64S -> i64TruncF64SDispatcher(I64TruncF64S)
        is NumericInstruction.I64TruncF64U -> i64TruncF64UDispatcher(I64TruncF64U)
        is NumericInstruction.I64TruncSatF32S -> i64TruncSatF32SDispatcher(I64TruncSatF32S)
        is NumericInstruction.I64TruncSatF32U -> i64TruncSatF32UDispatcher(I64TruncSatF32U)
        is NumericInstruction.I64TruncSatF64S -> i64TruncSatF64SDispatcher(I64TruncSatF64S)
        is NumericInstruction.I64TruncSatF64U -> i64TruncSatF64UDispatcher(I64TruncSatF64U)
        is NumericInstruction.F32ConvertI32S -> f32ConvertI32SDispatcher(F32ConvertI32S)
        is NumericInstruction.F32ConvertI32U -> f32ConvertI32UDispatcher(F32ConvertI32U)
        is NumericInstruction.F32ConvertI64S -> f32ConvertI64SDispatcher(F32ConvertI64S)
        is NumericInstruction.F32ConvertI64U -> f32ConvertI64UDispatcher(F32ConvertI64U)
        is NumericInstruction.F32DemoteF64 -> f32DemoteF64Dispatcher(F32DemoteF64)
        is NumericInstruction.F64ConvertI32S -> f64ConvertI32SDispatcher(F64ConvertI32S)
        is NumericInstruction.F64ConvertI32U -> f64ConvertI32UDispatcher(F64ConvertI32U)
        is NumericInstruction.F64ConvertI64S -> f64ConvertI64SDispatcher(F64ConvertI64S)
        is NumericInstruction.F64ConvertI64U -> f64ConvertI64UDispatcher(F64ConvertI64U)
        is NumericInstruction.F64PromoteF32 -> f64PromoteF32Dispatcher(F64PromoteF32)
        is NumericInstruction.F64ReinterpretI64 -> f64ReinterpretI64Dispatcher(F64ReinterpretI64)
        is NumericInstruction.F32Eq -> f32EqDispatcher(F32Eq)
        is NumericInstruction.F32Ge -> f32GeDispatcher(F32Ge)
        is NumericInstruction.F32Gt -> f32GtDispatcher(F32Gt)
        is NumericInstruction.F32Le -> f32LeDispatcher(F32Le)
        is NumericInstruction.F32Lt -> f32LtDispatcher(F32Lt)
        is NumericInstruction.F32Ne -> f32NeDispatcher(F32Ne)
        is NumericInstruction.F32ReinterpretI32 -> f32ReinterpretI32Dispatcher(F32ReinterpretI32)
        is NumericInstruction.F64Eq -> f64EqDispatcher(F64Eq)
        is NumericInstruction.F64Ge -> f64GeDispatcher(F64Ge)
        is NumericInstruction.F64Gt -> f64GtDispatcher(F64Gt)
        is NumericInstruction.F64Le -> f64LeDispatcher(F64Le)
        is NumericInstruction.F64Lt -> f64LtDispatcher(F64Lt)
        is NumericInstruction.F64Ne -> f64NeDispatcher(F64Ne)
        is NumericInstruction.I32Eq -> i32EqDispatcher(I32Eq)
        is NumericInstruction.I32Eqz -> i32EqzDispatcher(I32Eqz)
        is NumericInstruction.I32Extend16S -> i32Extend16SDispatcher(I32Extend16S)
        is NumericInstruction.I32Extend8S -> i32Extend8SDispatcher(I32Extend8S)
        is NumericInstruction.I32GeS -> i32GeSDispatcher(I32GeS)
        is NumericInstruction.I32GeU -> i32GeUDispatcher(I32GeU)
        is NumericInstruction.I32GtS -> i32GtSDispatcher(I32GtS)
        is NumericInstruction.I32GtU -> i32GtUDispatcher(I32GtU)
        is NumericInstruction.I32LeS -> i32LeSDispatcher(I32LeS)
        is NumericInstruction.I32LeU -> i32LeUDispatcher(I32LeU)
        is NumericInstruction.I32LtS -> i32LtSDispatcher(I32LtS)
        is NumericInstruction.I32LtU -> i32LtUDispatcher(I32LtU)
        is NumericInstruction.I32Ne -> i32NeDispatcher(I32Ne)
        is NumericInstruction.I64Eq -> i64EqDispatcher(I64Eq)
        is NumericInstruction.I64Eqz -> i64EqzDispatcher(I64Eqz)
        is NumericInstruction.I64GeS -> i64GeSDispatcher(I64GeS)
        is NumericInstruction.I64GeU -> i64GeUDispatcher(I64GeU)
        is NumericInstruction.I64GtS -> i64GtSDispatcher(I64GtS)
        is NumericInstruction.I64GtU -> i64GtUDispatcher(I64GtU)
        is NumericInstruction.I64LeS -> i64LeSDispatcher(I64LeS)
        is NumericInstruction.I64LeU -> i64LeUDispatcher(I64LeU)
        is NumericInstruction.I64LtS -> i64LtSDispatcher(I64LtS)
        is NumericInstruction.I64LtU -> i64LtUDispatcher(I64LtU)
        is NumericInstruction.I64Ne -> i64NeDispatcher(I64Ne)
    }
}
