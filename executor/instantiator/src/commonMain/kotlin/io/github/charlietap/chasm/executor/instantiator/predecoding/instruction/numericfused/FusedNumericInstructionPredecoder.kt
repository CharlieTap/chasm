package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.LoadFactory
import io.github.charlietap.chasm.executor.instantiator.predecoding.StoreFactory
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32AbsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32AddDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32DivUDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32MulDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32SubDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Mul
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction

internal fun FusedNumericInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedNumericInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedNumericInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        i32AddDispatcher = ::I32AddDispatcher,
        i32SubDispatcher = ::I32SubDispatcher,
        i32MulDispatcher = ::I32MulDispatcher,
        i32DivSDispatcher = ::I32DivSDispatcher,
        i32DivUDispatcher = ::I32DivUDispatcher,
        f32AbsDispatcher = ::F32AbsDispatcher,
    )

internal inline fun FusedNumericInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedNumericInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline i32AddDispatcher: Dispatcher<I32Add>,
    crossinline i32SubDispatcher: Dispatcher<I32Sub>,
    crossinline i32MulDispatcher: Dispatcher<I32Mul>,
    crossinline i32DivSDispatcher: Dispatcher<I32DivS>,
    crossinline i32DivUDispatcher: Dispatcher<I32DivU>,
    crossinline f32AbsDispatcher: Dispatcher<F32Abs>,
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
        is FusedNumericInstruction.I32And -> TODO()
        is FusedNumericInstruction.I32Clz -> TODO()
        is FusedNumericInstruction.I32Ctz -> TODO()
        is FusedNumericInstruction.I32Eq -> TODO()
        is FusedNumericInstruction.I32Eqz -> TODO()
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
        is FusedNumericInstruction.I32Or -> TODO()
        is FusedNumericInstruction.I32Popcnt -> TODO()
        is FusedNumericInstruction.I32ReinterpretF32 -> TODO()
        is FusedNumericInstruction.I32RemS -> TODO()
        is FusedNumericInstruction.I32RemU -> TODO()
        is FusedNumericInstruction.I32Rotl -> TODO()
        is FusedNumericInstruction.I32Rotr -> TODO()
        is FusedNumericInstruction.I32Shl -> TODO()
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
        is FusedNumericInstruction.I32Xor -> TODO()
        is FusedNumericInstruction.I64Add -> TODO()
        is FusedNumericInstruction.I64And -> TODO()
        is FusedNumericInstruction.I64Clz -> TODO()
        is FusedNumericInstruction.I64Ctz -> TODO()
        is FusedNumericInstruction.I64DivS -> TODO()
        is FusedNumericInstruction.I64DivU -> TODO()
        is FusedNumericInstruction.I64Eq -> TODO()
        is FusedNumericInstruction.I64Eqz -> TODO()
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
        is FusedNumericInstruction.I64Mul -> TODO()
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
        is FusedNumericInstruction.I64Sub -> TODO()
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
