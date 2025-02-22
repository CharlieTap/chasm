package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AndDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32OrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32XorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64SubDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32And
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Eqz
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Mul
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Or
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Shl
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Xor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I64Add
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I64DivS
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I64DivU
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I64Eqz
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I64Mul
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I64Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory

internal fun FusedNumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedNumericInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        i32AddDispatcher = ::I32AddDispatcher,
        i32AndDispatcher = ::I32AndDispatcher,
        i32OrDispatcher = ::I32OrDispatcher,
        i32XorDispatcher = ::I32XorDispatcher,
        i32SubDispatcher = ::I32SubDispatcher,
        i32MulDispatcher = ::I32MulDispatcher,
        i32DivSDispatcher = ::I32DivSDispatcher,
        i32DivUDispatcher = ::I32DivUDispatcher,
        i32ShlDispatcher = ::I32ShlDispatcher,
        i32EqzDispatcher = ::I32EqzDispatcher,
        i64EqzDispatcher = ::I64EqzDispatcher,
        f32AbsDispatcher = ::F32AbsDispatcher,
        i64AddDispatcher = ::I64AddDispatcher,
        i64SubDispatcher = ::I64SubDispatcher,
        i64MulDispatcher = ::I64MulDispatcher,
        i64DivSDispatcher = ::I64DivSDispatcher,
        i64DivUDispatcher = ::I64DivUDispatcher,
    )

internal inline fun FusedNumericInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline i32AddDispatcher: Dispatcher<I32Add>,
    crossinline i32AndDispatcher: Dispatcher<I32And>,
    crossinline i32OrDispatcher: Dispatcher<I32Or>,
    crossinline i32XorDispatcher: Dispatcher<I32Xor>,
    crossinline i32SubDispatcher: Dispatcher<I32Sub>,
    crossinline i32MulDispatcher: Dispatcher<I32Mul>,
    crossinline i32DivSDispatcher: Dispatcher<I32DivS>,
    crossinline i32DivUDispatcher: Dispatcher<I32DivU>,
    crossinline i32ShlDispatcher: Dispatcher<I32Shl>,
    crossinline i32EqzDispatcher: Dispatcher<I32Eqz>,
    crossinline i64EqzDispatcher: Dispatcher<I64Eqz>,
    crossinline f32AbsDispatcher: Dispatcher<F32Abs>,
    crossinline i64AddDispatcher: Dispatcher<I64Add>,
    crossinline i64SubDispatcher: Dispatcher<I64Sub>,
    crossinline i64MulDispatcher: Dispatcher<I64Mul>,
    crossinline i64DivSDispatcher: Dispatcher<I64DivS>,
    crossinline i64DivUDispatcher: Dispatcher<I64DivU>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedNumericInstruction.I32Add -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32AddDispatcher(
                I32Add(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32And -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32AndDispatcher(
                I32And(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Or -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32OrDispatcher(
                I32Or(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Xor -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32XorDispatcher(
                I32Xor(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Eqz -> {
            val operand = loadFactory(context, instruction.operand)
            val destination = storeFactory(context, instruction.destination)

            i32EqzDispatcher(
                I32Eqz(
                    operand = operand,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.F32Abs -> {

            val operand = loadFactory(context, instruction.operand)
            val destination = storeFactory(context, instruction.destination)

            f32AbsDispatcher(
                F32Abs(
                    operand = operand,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Sub -> {

            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32SubDispatcher(
                I32Sub(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Mul -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32MulDispatcher(
                I32Mul(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32DivS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32DivSDispatcher(
                I32DivS(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32DivU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32DivUDispatcher(
                I32DivU(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I32Shl -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32ShlDispatcher(
                I32Shl(
                    left = left,
                    right = right,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I64Eqz -> {
            val operand = loadFactory(context, instruction.operand)
            val destination = storeFactory(context, instruction.destination)

            i64EqzDispatcher(
                I64Eqz(
                    operand = operand,
                    destination = destination,
                ),
            )
        }
        is FusedNumericInstruction.I64Add -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i64AddDispatcher(I64Add(left, right, destination))
        }
        is FusedNumericInstruction.I64Sub -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i64SubDispatcher(I64Sub(left, right, destination))
        }
        is FusedNumericInstruction.I64Mul -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i64MulDispatcher(I64Mul(left, right, destination))
        }
        is FusedNumericInstruction.I64DivS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i64DivSDispatcher(I64DivS(left, right, destination))
        }
        is FusedNumericInstruction.I64DivU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i64DivUDispatcher(I64DivU(left, right, destination))
        }
        is FusedNumericInstruction.F32Add -> TODO()
        is FusedNumericInstruction.F32Ceil -> TODO()
        is FusedNumericInstruction.F32ConvertI32S -> TODO()
        is FusedNumericInstruction.F32ConvertI32U -> TODO()
        is FusedNumericInstruction.F32ConvertI64S -> TODO()
        is FusedNumericInstruction.F32ConvertI64U -> TODO()
        is FusedNumericInstruction.F32Copysign -> TODO()
        is FusedNumericInstruction.F32DemoteF64 -> TODO()
        is FusedNumericInstruction.F32Div -> TODO()
        is FusedNumericInstruction.F32Eq -> TODO()
        is FusedNumericInstruction.F32Floor -> TODO()
        is FusedNumericInstruction.F32Ge -> TODO()
        is FusedNumericInstruction.F32Gt -> TODO()
        is FusedNumericInstruction.F32Le -> TODO()
        is FusedNumericInstruction.F32Lt -> TODO()
        is FusedNumericInstruction.F32Max -> TODO()
        is FusedNumericInstruction.F32Min -> TODO()
        is FusedNumericInstruction.F32Mul -> TODO()
        is FusedNumericInstruction.F32Ne -> TODO()
        is FusedNumericInstruction.F32Nearest -> TODO()
        is FusedNumericInstruction.F32Neg -> TODO()
        is FusedNumericInstruction.F32ReinterpretI32 -> TODO()
        is FusedNumericInstruction.F32Sqrt -> TODO()
        is FusedNumericInstruction.F32Sub -> TODO()
        is FusedNumericInstruction.F32Trunc -> TODO()
        is FusedNumericInstruction.F64Abs -> TODO()
        is FusedNumericInstruction.F64Add -> TODO()
        is FusedNumericInstruction.F64Ceil -> TODO()
        is FusedNumericInstruction.F64ConvertI32S -> TODO()
        is FusedNumericInstruction.F64ConvertI32U -> TODO()
        is FusedNumericInstruction.F64ConvertI64S -> TODO()
        is FusedNumericInstruction.F64ConvertI64U -> TODO()
        is FusedNumericInstruction.F64Copysign -> TODO()
        is FusedNumericInstruction.F64Div -> TODO()
        is FusedNumericInstruction.F64Eq -> TODO()
        is FusedNumericInstruction.F64Floor -> TODO()
        is FusedNumericInstruction.F64Ge -> TODO()
        is FusedNumericInstruction.F64Gt -> TODO()
        is FusedNumericInstruction.F64Le -> TODO()
        is FusedNumericInstruction.F64Lt -> TODO()
        is FusedNumericInstruction.F64Max -> TODO()
        is FusedNumericInstruction.F64Min -> TODO()
        is FusedNumericInstruction.F64Mul -> TODO()
        is FusedNumericInstruction.F64Ne -> TODO()
        is FusedNumericInstruction.F64Nearest -> TODO()
        is FusedNumericInstruction.F64Neg -> TODO()
        is FusedNumericInstruction.F64PromoteF32 -> TODO()
        is FusedNumericInstruction.F64ReinterpretI64 -> TODO()
        is FusedNumericInstruction.F64Sqrt -> TODO()
        is FusedNumericInstruction.F64Sub -> TODO()
        is FusedNumericInstruction.F64Trunc -> TODO()
        is FusedNumericInstruction.I32Clz -> TODO()
        is FusedNumericInstruction.I32Ctz -> TODO()
        is FusedNumericInstruction.I32Eq -> TODO()
        is FusedNumericInstruction.I32Extend16S -> TODO()
        is FusedNumericInstruction.I32Extend8S -> TODO()
        is FusedNumericInstruction.I32GeS -> TODO()
        is FusedNumericInstruction.I32GeU -> TODO()
        is FusedNumericInstruction.I32GtS -> TODO()
        is FusedNumericInstruction.I32GtU -> TODO()
        is FusedNumericInstruction.I32LeS -> TODO()
        is FusedNumericInstruction.I32LeU -> TODO()
        is FusedNumericInstruction.I32LtS -> TODO()
        is FusedNumericInstruction.I32LtU -> TODO()
        is FusedNumericInstruction.I32Ne -> TODO()
        is FusedNumericInstruction.I32Popcnt -> TODO()
        is FusedNumericInstruction.I32ReinterpretF32 -> TODO()
        is FusedNumericInstruction.I32RemS -> TODO()
        is FusedNumericInstruction.I32RemU -> TODO()
        is FusedNumericInstruction.I32Rotl -> TODO()
        is FusedNumericInstruction.I32Rotr -> TODO()
        is FusedNumericInstruction.I32ShrS -> TODO()
        is FusedNumericInstruction.I32ShrU -> TODO()
        is FusedNumericInstruction.I32TruncF32S -> TODO()
        is FusedNumericInstruction.I32TruncF32U -> TODO()
        is FusedNumericInstruction.I32TruncF64S -> TODO()
        is FusedNumericInstruction.I32TruncF64U -> TODO()
        is FusedNumericInstruction.I32TruncSatF32S -> TODO()
        is FusedNumericInstruction.I32TruncSatF32U -> TODO()
        is FusedNumericInstruction.I32TruncSatF64S -> TODO()
        is FusedNumericInstruction.I32TruncSatF64U -> TODO()
        is FusedNumericInstruction.I32WrapI64 -> TODO()
        is FusedNumericInstruction.I64And -> TODO()
        is FusedNumericInstruction.I64Clz -> TODO()
        is FusedNumericInstruction.I64Ctz -> TODO()
        is FusedNumericInstruction.I64Eq -> TODO()
        is FusedNumericInstruction.I64Extend16S -> TODO()
        is FusedNumericInstruction.I64Extend32S -> TODO()
        is FusedNumericInstruction.I64Extend8S -> TODO()
        is FusedNumericInstruction.I64ExtendI32S -> TODO()
        is FusedNumericInstruction.I64ExtendI32U -> TODO()
        is FusedNumericInstruction.I64GeS -> TODO()
        is FusedNumericInstruction.I64GeU -> TODO()
        is FusedNumericInstruction.I64GtS -> TODO()
        is FusedNumericInstruction.I64GtU -> TODO()
        is FusedNumericInstruction.I64LeS -> TODO()
        is FusedNumericInstruction.I64LeU -> TODO()
        is FusedNumericInstruction.I64LtS -> TODO()
        is FusedNumericInstruction.I64LtU -> TODO()
        is FusedNumericInstruction.I64Ne -> TODO()
        is FusedNumericInstruction.I64Or -> TODO()
        is FusedNumericInstruction.I64Popcnt -> TODO()
        is FusedNumericInstruction.I64ReinterpretF64 -> TODO()
        is FusedNumericInstruction.I64RemS -> TODO()
        is FusedNumericInstruction.I64RemU -> TODO()
        is FusedNumericInstruction.I64Rotl -> TODO()
        is FusedNumericInstruction.I64Rotr -> TODO()
        is FusedNumericInstruction.I64Shl -> TODO()
        is FusedNumericInstruction.I64ShrS -> TODO()
        is FusedNumericInstruction.I64ShrU -> TODO()
        is FusedNumericInstruction.I64TruncF32S -> TODO()
        is FusedNumericInstruction.I64TruncF32U -> TODO()
        is FusedNumericInstruction.I64TruncF64S -> TODO()
        is FusedNumericInstruction.I64TruncF64U -> TODO()
        is FusedNumericInstruction.I64TruncSatF32S -> TODO()
        is FusedNumericInstruction.I64TruncSatF32U -> TODO()
        is FusedNumericInstruction.I64TruncSatF64S -> TODO()
        is FusedNumericInstruction.I64TruncSatF64U -> TODO()
        is FusedNumericInstruction.I64Xor -> TODO()
    }
}
