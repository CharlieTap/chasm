package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32DivDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64DivDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64GeDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64LtDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AndDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32EqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32EqzDispatcher
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
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShlDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShrSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ShrUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32SubDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32XorDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64EqzDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64SubDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Add
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Div
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Mul
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Sub
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Add
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Div
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Ge
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Lt
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Mul
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Sub
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32And
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Eq
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Eqz
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32GeS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32GeU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32GtS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32GtU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32LeS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32LeU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32LtS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32LtU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Mul
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Ne
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Or
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Shl
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32ShrS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32ShrU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I32Xor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64Add
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64DivS
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64DivU
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64Eqz
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64Mul
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.I64Sub

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
        i32ShrSDispatcher = ::I32ShrSDispatcher,
        i32ShrUDispatcher = ::I32ShrUDispatcher,
        i32LtSDispatcher = ::I32LtSDispatcher,
        i32LtUDispatcher = ::I32LtUDispatcher,
        i32GtSDispatcher = ::I32GtSDispatcher,
        i32GtUDispatcher = ::I32GtUDispatcher,
        i32LeSDispatcher = ::I32LeSDispatcher,
        i32LeUDispatcher = ::I32LeUDispatcher,
        i32GeSDispatcher = ::I32GeSDispatcher,
        i32GeUDispatcher = ::I32GeUDispatcher,
        i32EqDispatcher = ::I32EqDispatcher,
        i32EqzDispatcher = ::I32EqzDispatcher,
        i32NeDispatcher = ::I32NeDispatcher,
        i64EqzDispatcher = ::I64EqzDispatcher,
        f32AbsDispatcher = ::F32AbsDispatcher,
        i64AddDispatcher = ::I64AddDispatcher,
        i64SubDispatcher = ::I64SubDispatcher,
        i64MulDispatcher = ::I64MulDispatcher,
        i64DivSDispatcher = ::I64DivSDispatcher,
        i64DivUDispatcher = ::I64DivUDispatcher,
        f32AddDispatcher = ::F32AddDispatcher,
        f32SubDispatcher = ::F32SubDispatcher,
        f32MulDispatcher = ::F32MulDispatcher,
        f32DivDispatcher = ::F32DivDispatcher,
        f64AddDispatcher = ::F64AddDispatcher,
        f64SubDispatcher = ::F64SubDispatcher,
        f64MulDispatcher = ::F64MulDispatcher,
        f64DivDispatcher = ::F64DivDispatcher,
        f64GeDispatcher = ::F64GeDispatcher,
        f64LtDispatcher = ::F64LtDispatcher,
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
    crossinline i32ShrSDispatcher: Dispatcher<I32ShrS>,
    crossinline i32ShrUDispatcher: Dispatcher<I32ShrU>,
    crossinline i32LtSDispatcher: Dispatcher<I32LtS>,
    crossinline i32LtUDispatcher: Dispatcher<I32LtU>,
    crossinline i32GtSDispatcher: Dispatcher<I32GtS>,
    crossinline i32GtUDispatcher: Dispatcher<I32GtU>,
    crossinline i32LeSDispatcher: Dispatcher<I32LeS>,
    crossinline i32LeUDispatcher: Dispatcher<I32LeU>,
    crossinline i32GeSDispatcher: Dispatcher<I32GeS>,
    crossinline i32GeUDispatcher: Dispatcher<I32GeU>,
    crossinline i32EqDispatcher: Dispatcher<I32Eq>,
    crossinline i32EqzDispatcher: Dispatcher<I32Eqz>,
    crossinline i32NeDispatcher: Dispatcher<I32Ne>,
    crossinline i64EqzDispatcher: Dispatcher<I64Eqz>,
    crossinline f32AbsDispatcher: Dispatcher<F32Abs>,
    crossinline i64AddDispatcher: Dispatcher<I64Add>,
    crossinline i64SubDispatcher: Dispatcher<I64Sub>,
    crossinline i64MulDispatcher: Dispatcher<I64Mul>,
    crossinline i64DivSDispatcher: Dispatcher<I64DivS>,
    crossinline i64DivUDispatcher: Dispatcher<I64DivU>,
    crossinline f32AddDispatcher: Dispatcher<F32Add>,
    crossinline f32SubDispatcher: Dispatcher<F32Sub>,
    crossinline f32MulDispatcher: Dispatcher<F32Mul>,
    crossinline f32DivDispatcher: Dispatcher<F32Div>,
    crossinline f64AddDispatcher: Dispatcher<F64Add>,
    crossinline f64SubDispatcher: Dispatcher<F64Sub>,
    crossinline f64MulDispatcher: Dispatcher<F64Mul>,
    crossinline f64DivDispatcher: Dispatcher<F64Div>,
    crossinline f64GeDispatcher: Dispatcher<F64Ge>,
    crossinline f64LtDispatcher: Dispatcher<F64Lt>,
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
        is FusedNumericInstruction.I32Eq -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32EqDispatcher(
                I32Eq(
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
        is FusedNumericInstruction.I32Ne -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)

            i32NeDispatcher(
                I32Ne(
                    left = left,
                    right = right,
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
        is FusedNumericInstruction.I32ShrS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32ShrSDispatcher(I32ShrS(left, right, destination))
        }
        is FusedNumericInstruction.I32ShrU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32ShrUDispatcher(I32ShrU(left, right, destination))
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
        is FusedNumericInstruction.F32Add -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f32AddDispatcher(F32Add(left, right, destination))
        }
        is FusedNumericInstruction.F32Sub -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f32SubDispatcher(F32Sub(left, right, destination))
        }
        is FusedNumericInstruction.F32Mul -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f32MulDispatcher(F32Mul(left, right, destination))
        }
        is FusedNumericInstruction.F32Div -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f32DivDispatcher(F32Div(left, right, destination))
        }
        is FusedNumericInstruction.F64Add -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f64AddDispatcher(F64Add(left, right, destination))
        }
        is FusedNumericInstruction.F64Sub -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f64SubDispatcher(F64Sub(left, right, destination))
        }
        is FusedNumericInstruction.F64Mul -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f64MulDispatcher(F64Mul(left, right, destination))
        }
        is FusedNumericInstruction.F64Div -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f64DivDispatcher(F64Div(left, right, destination))
        }
        is FusedNumericInstruction.I32LtS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32LtSDispatcher(I32LtS(left, right, destination))
        }
        is FusedNumericInstruction.I32LtU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32LtUDispatcher(I32LtU(left, right, destination))
        }
        is FusedNumericInstruction.I32GtS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32GtSDispatcher(I32GtS(left, right, destination))
        }
        is FusedNumericInstruction.I32GtU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32GtUDispatcher(I32GtU(left, right, destination))
        }
        is FusedNumericInstruction.I32LeS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32LeSDispatcher(I32LeS(left, right, destination))
        }
        is FusedNumericInstruction.I32LeU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32LeUDispatcher(I32LeU(left, right, destination))
        }
        is FusedNumericInstruction.I32GeS -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32GeSDispatcher(I32GeS(left, right, destination))
        }
        is FusedNumericInstruction.I32GeU -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            i32GeUDispatcher(I32GeU(left, right, destination))
        }
        is FusedNumericInstruction.F64Ge -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f64GeDispatcher(F64Ge(left, right, destination))
        }
        is FusedNumericInstruction.F64Lt -> {
            val left = loadFactory(context, instruction.left)
            val right = loadFactory(context, instruction.right)
            val destination = storeFactory(context, instruction.destination)
            f64LtDispatcher(F64Lt(left, right, destination))
        }

        is FusedNumericInstruction.F32Ceil -> TODO()
        is FusedNumericInstruction.F32ConvertI32S -> TODO()
        is FusedNumericInstruction.F32ConvertI32U -> TODO()
        is FusedNumericInstruction.F32ConvertI64S -> TODO()
        is FusedNumericInstruction.F32ConvertI64U -> TODO()
        is FusedNumericInstruction.F32Copysign -> TODO()
        is FusedNumericInstruction.F32DemoteF64 -> TODO()
        is FusedNumericInstruction.F32Eq -> TODO()
        is FusedNumericInstruction.F32Floor -> TODO()
        is FusedNumericInstruction.F32Ge -> TODO()
        is FusedNumericInstruction.F32Gt -> TODO()
        is FusedNumericInstruction.F32Le -> TODO()
        is FusedNumericInstruction.F32Lt -> TODO()
        is FusedNumericInstruction.F32Max -> TODO()
        is FusedNumericInstruction.F32Min -> TODO()
        is FusedNumericInstruction.F32Ne -> TODO()
        is FusedNumericInstruction.F32Nearest -> TODO()
        is FusedNumericInstruction.F32Neg -> TODO()
        is FusedNumericInstruction.F32ReinterpretI32 -> TODO()
        is FusedNumericInstruction.F32Sqrt -> TODO()
        is FusedNumericInstruction.F32Trunc -> TODO()
        is FusedNumericInstruction.F64Abs -> TODO()
        is FusedNumericInstruction.F64Ceil -> TODO()
        is FusedNumericInstruction.F64ConvertI32S -> TODO()
        is FusedNumericInstruction.F64ConvertI32U -> TODO()
        is FusedNumericInstruction.F64ConvertI64S -> TODO()
        is FusedNumericInstruction.F64ConvertI64U -> TODO()
        is FusedNumericInstruction.F64Copysign -> TODO()
        is FusedNumericInstruction.F64Eq -> TODO()
        is FusedNumericInstruction.F64Floor -> TODO()
        is FusedNumericInstruction.F64Gt -> TODO()
        is FusedNumericInstruction.F64Le -> TODO()
        is FusedNumericInstruction.F64Max -> TODO()
        is FusedNumericInstruction.F64Min -> TODO()
        is FusedNumericInstruction.F64Ne -> TODO()
        is FusedNumericInstruction.F64Nearest -> TODO()
        is FusedNumericInstruction.F64Neg -> TODO()
        is FusedNumericInstruction.F64PromoteF32 -> TODO()
        is FusedNumericInstruction.F64ReinterpretI64 -> TODO()
        is FusedNumericInstruction.F64Sqrt -> TODO()
        is FusedNumericInstruction.F64Trunc -> TODO()
        is FusedNumericInstruction.I32Clz -> TODO()
        is FusedNumericInstruction.I32Ctz -> TODO()
        is FusedNumericInstruction.I32Extend16S -> TODO()
        is FusedNumericInstruction.I32Extend8S -> TODO()
        is FusedNumericInstruction.I32Popcnt -> TODO()
        is FusedNumericInstruction.I32ReinterpretF32 -> TODO()
        is FusedNumericInstruction.I32RemS -> TODO()
        is FusedNumericInstruction.I32RemU -> TODO()
        is FusedNumericInstruction.I32Rotl -> TODO()
        is FusedNumericInstruction.I32Rotr -> TODO()
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
