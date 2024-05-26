package io.github.charlietap.chasm.executor.invoker.instruction.numeric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.ext.*
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.*
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.*
import kotlin.math.absoluteValue

internal fun NumericInstructionExecutorImpl(
    instruction: NumericInstruction,
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is NumericInstruction.I32Const -> stack.constOperation(instruction.value)
        is NumericInstruction.I64Const -> stack.constOperation(instruction.value)
        is NumericInstruction.F32Const -> stack.constOperation(instruction.value)
        is NumericInstruction.F64Const -> stack.constOperation(instruction.value)

        is NumericInstruction.I32Add -> stack.binaryOperation(Int::plus).bind()
        is NumericInstruction.I64Add -> stack.binaryOperation(Long::plus).bind()
        is NumericInstruction.F32Add -> stack.binaryOperation(Float::plus).bind()
        is NumericInstruction.F64Add -> stack.binaryOperation(Double::plus).bind()

        is NumericInstruction.I32Sub -> stack.binaryOperation(Int::minus).bind()
        is NumericInstruction.I64Sub -> stack.binaryOperation(Long::minus).bind()
        is NumericInstruction.F32Sub -> stack.binaryOperation(Float::minus).bind()
        is NumericInstruction.F64Sub -> stack.binaryOperation(Double::minus).bind()

        is NumericInstruction.I32Mul -> stack.binaryOperation(Int::times).bind()
        is NumericInstruction.I64Mul -> stack.binaryOperation(Long::times).bind()
        is NumericInstruction.F32Mul -> stack.binaryOperation(Float::times).bind()
        is NumericInstruction.F64Mul -> stack.binaryOperation(Double::times).bind()

        is NumericInstruction.I32DivS -> {

            val operand1 = stack.peekNthValue(1).bind().value as I32
            val operand2 = stack.peekNthValue(0).bind().value as I32

            if (operand2.value == 0) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            } else if (operand1.value == Int.MIN_VALUE && operand2.value == -1) {
                Err(InvocationError.IntegerOverflow).bind<Unit>()
            }

            stack.binaryOperation(Int::div).bind()
        }
        is NumericInstruction.I64DivS -> {

            val operand1 = stack.peekNthValue(1).bind().value as I64
            val operand2 = stack.peekNthValue(0).bind().value as I64

            if (operand2.value == 0L) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            } else if (operand1.value == Long.MIN_VALUE && operand2.value == -1L) {
                Err(InvocationError.IntegerOverflow).bind<Unit>()
            }

            stack.binaryOperation(Long::div).bind()
        }
        is NumericInstruction.F32Div -> stack.binaryOperation(Float::div).bind()
        is NumericInstruction.F64Div -> stack.binaryOperation(Double::div).bind()

        is NumericInstruction.I32DivU -> {

            val operand2 = stack.peekNthValue(0).bind().value as I32

            if (operand2.value.toUInt() == 0u) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            }

            stack.binaryOperation(Int::divu).bind()
        }
        is NumericInstruction.I64DivU -> {

            val operand2 = stack.peekNthValue(0).bind().value as I64

            if (operand2.value.toULong() == 0uL) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            }

            stack.binaryOperation(Long::divu).bind()
        }

        is NumericInstruction.I32And -> stack.binaryOperation(Int::and).bind()
        is NumericInstruction.I64And -> stack.binaryOperation(Long::and).bind()

        is NumericInstruction.I32Or -> stack.binaryOperation(Int::or).bind()
        is NumericInstruction.I64Or -> stack.binaryOperation(Long::or).bind()

        is NumericInstruction.I32Xor -> stack.binaryOperation(Int::xor).bind()
        is NumericInstruction.I64Xor -> stack.binaryOperation(Long::xor).bind()

        is NumericInstruction.I32RemS -> {

            val operand2 = stack.peekNthValue(0).bind().value as I32

            if (operand2.value == 0) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            }

            stack.binaryOperation(Int::rem).bind()
        }
        is NumericInstruction.I64RemS -> {

            val operand2 = stack.peekNthValue(0).bind().value as I64

            if (operand2.value == 0L) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            }

            stack.binaryOperation(Long::rem).bind()
        }

        is NumericInstruction.I32RemU -> {

            val operand2 = stack.peekNthValue(0).bind().value as I32

            if (operand2.value.toUInt() == 0u) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            }

            stack.binaryOperation(Int::remu).bind()
        }
        is NumericInstruction.I64RemU -> {

            val operand2 = stack.peekNthValue(0).bind().value as I64

            if (operand2.value.toULong() == 0uL) {
                Err(InvocationError.CannotDivideIntegerByZero).bind<Unit>()
            }

            stack.binaryOperation(Long::remu).bind()
        }

        is NumericInstruction.I32Shl -> stack.binaryOperation(Int::shl).bind()
        is NumericInstruction.I64Shl -> stack.binaryOperation(Long::shl).bind()

        is NumericInstruction.I32ShrS -> stack.binaryOperation(Int::shr).bind()
        is NumericInstruction.I64ShrS -> stack.binaryOperation(Long::shr).bind()

        is NumericInstruction.I32ShrU -> stack.binaryOperation(Int::shru).bind()
        is NumericInstruction.I64ShrU -> stack.binaryOperation(Long::shru).bind()

        is NumericInstruction.I32Rotl -> stack.binaryOperation(Int::rotateLeft).bind()
        is NumericInstruction.I64Rotl -> stack.binaryOperation(Long::rotateLeft).bind()

        is NumericInstruction.I32Rotr -> stack.binaryOperation(Int::rotateRight).bind()
        is NumericInstruction.I64Rotr -> stack.binaryOperation(Long::rotateRight).bind()

        is NumericInstruction.I32Popcnt -> stack.unaryOperation(Int::countOneBits).bind()
        is NumericInstruction.I64Popcnt -> stack.unaryOperation(Long::countOnePopulation).bind()

        is NumericInstruction.I32Clz -> stack.unaryOperation(Int::countLeadingZeroBits).bind()
        is NumericInstruction.I64Clz -> stack.unaryOperation(Long::countLeadingZero).bind()

        is NumericInstruction.I32Ctz -> stack.unaryOperation(Int::countTrailingZeroBits).bind()
        is NumericInstruction.I64Ctz -> stack.unaryOperation(Long::countTrailingZero).bind()

        is NumericInstruction.F32Abs -> stack.unaryOperation(Float::absoluteValue).bind()
        is NumericInstruction.F64Abs -> stack.unaryOperation(Double::absoluteValue).bind()

        is NumericInstruction.F32Neg -> stack.unaryOperation(Float::unaryMinus).bind()
        is NumericInstruction.F64Neg -> stack.unaryOperation(Double::unaryMinus).bind()

        is NumericInstruction.F32Ceil -> stack.unaryOperation(Float::ceil).bind()
        is NumericInstruction.F64Ceil -> stack.unaryOperation(Double::ceil).bind()

        is NumericInstruction.F32Floor -> stack.unaryOperation(Float::floor).bind()
        is NumericInstruction.F64Floor -> stack.unaryOperation(Double::floor).bind()

        is NumericInstruction.F32Trunc -> stack.unaryOperation(Float::trunc).bind()
        is NumericInstruction.F64Trunc -> stack.unaryOperation(Double::trunc).bind()

        is NumericInstruction.F32Nearest -> stack.unaryOperation(Float::nearest).bind()
        is NumericInstruction.F64Nearest -> stack.unaryOperation(Double::nearest).bind()

        is NumericInstruction.F32Sqrt -> stack.unaryOperation(Float::sqrt).bind()
        is NumericInstruction.F64Sqrt -> stack.unaryOperation(Double::sqrt).bind()

        is NumericInstruction.F32Min -> stack.binaryOperation(Float::min).bind()
        is NumericInstruction.F64Min -> stack.binaryOperation(Double::min).bind()

        is NumericInstruction.F32Max -> stack.binaryOperation(Float::max).bind()
        is NumericInstruction.F64Max -> stack.binaryOperation(Double::max).bind()

        is NumericInstruction.F32Copysign -> stack.binaryOperation(Float::copySign).bind()
        is NumericInstruction.F64Copysign -> stack.binaryOperation(Double::copySign).bind()

        is NumericInstruction.I32TruncF32S -> stack.convertOperation(::I32, Float::truncI32sTrapping).bind()
        is NumericInstruction.I32TruncF64S -> stack.convertOperation(::I32, Double::truncI32sTrapping).bind()

        is NumericInstruction.I32TruncF32U -> stack.convertOperation(::I32, Float::truncI32uTrapping).bind()
        is NumericInstruction.I32TruncF64U -> stack.convertOperation(::I32, Double::truncI32uTrapping).bind()

        is NumericInstruction.I64TruncF32S -> stack.convertOperation(::I64, Float::truncI64sTrapping).bind()
        is NumericInstruction.I64TruncF64S -> stack.convertOperation(::I64, Double::truncI64sTrapping).bind()

        is NumericInstruction.I64TruncF32U -> stack.convertOperation(::I64, Float::truncI64uTrapping).bind()
        is NumericInstruction.I64TruncF64U -> stack.convertOperation(::I64, Double::truncI64uTrapping).bind()

        is NumericInstruction.I64ExtendI32S -> stack.convertOperation(::I64, Int::extendI64s).bind()
        is NumericInstruction.I64ExtendI32U -> stack.convertOperation(::I64, Int::extendI64u).bind()

        is NumericInstruction.F32ConvertI32S -> stack.convertOperation(::F32, Int::convertF32s).bind()
        is NumericInstruction.F32ConvertI32U -> stack.convertOperation(::F32, Int::convertF32u).bind()

        is NumericInstruction.F64ConvertI32S -> stack.convertOperation(::F64, Int::convertF64s).bind()
        is NumericInstruction.F64ConvertI32U -> stack.convertOperation(::F64, Int::convertF64u).bind()

        is NumericInstruction.F32ConvertI64S -> stack.convertOperation(::F32, Long::convertF32s).bind()
        is NumericInstruction.F32ConvertI64U -> stack.convertOperation(::F32, Long::convertF32u).bind()

        is NumericInstruction.F64ConvertI64S -> stack.convertOperation(::F64, Long::convertF64s).bind()
        is NumericInstruction.F64ConvertI64U -> stack.convertOperation(::F64, Long::convertF64u).bind()

        is NumericInstruction.F32DemoteF64 -> stack.convertOperation(::F32, Double::toFloat).bind()
        is NumericInstruction.F64PromoteF32 -> stack.convertOperation(::F64, Float::toDouble).bind()

        is NumericInstruction.F32ReinterpretI32 -> stack.convertOperation(::F32, Float::fromBits).bind()
        is NumericInstruction.F64ReinterpretI64 -> stack.convertOperation(::F64, Double::fromBits).bind()

        is NumericInstruction.I32ReinterpretF32 -> stack.convertOperation(::I32, Float::toRawBits).bind()
        is NumericInstruction.I64ReinterpretF64 -> stack.convertOperation(::I64, Double::toRawBits).bind()

        is NumericInstruction.I32Extend8S -> stack.unaryOperation(Int::extend8s).bind()
        is NumericInstruction.I32Extend16S -> stack.unaryOperation(Int::extend16s).bind()

        is NumericInstruction.I64Extend8S -> stack.unaryOperation(Long::extend8s).bind()
        is NumericInstruction.I64Extend16S -> stack.unaryOperation(Long::extend16s).bind()
        is NumericInstruction.I64Extend32S -> stack.unaryOperation(Long::extend32s).bind()

        is NumericInstruction.I32TruncSatF32S -> stack.convertOperation(::I32, Float::truncI32s).bind()
        is NumericInstruction.I32TruncSatF32U -> stack.convertOperation(::I32, Float::truncI32u).bind()

        is NumericInstruction.I64TruncSatF32S -> stack.convertOperation(::I64, Float::truncI64s).bind()
        is NumericInstruction.I64TruncSatF32U -> stack.convertOperation(::I64, Float::truncI64u).bind()

        is NumericInstruction.I32TruncSatF64S -> stack.convertOperation(::I32, Double::truncI32s).bind()
        is NumericInstruction.I32TruncSatF64U -> stack.convertOperation(::I32, Double::truncI32u).bind()

        is NumericInstruction.I64TruncSatF64S -> stack.convertOperation(::I64, Double::truncI64s).bind()
        is NumericInstruction.I64TruncSatF64U -> stack.convertOperation(::I64, Double::truncI64u).bind()

        is NumericInstruction.I32Eq -> stack.relationalOperation(Int::eq).bind()
        is NumericInstruction.I64Eq -> stack.relationalOperation(Long::eq).bind()
        is NumericInstruction.F32Eq -> stack.relationalOperation(Float::eq).bind()
        is NumericInstruction.F64Eq -> stack.relationalOperation(Double::eq).bind()

        is NumericInstruction.I32Ne -> stack.relationalOperation(Int::ne).bind()
        is NumericInstruction.I64Ne -> stack.relationalOperation(Long::ne).bind()
        is NumericInstruction.F32Ne -> stack.relationalOperation(Float::ne).bind()
        is NumericInstruction.F64Ne -> stack.relationalOperation(Double::ne).bind()

        is NumericInstruction.I32Eqz -> stack.testOperation(Int::eqz).bind()
        is NumericInstruction.I64Eqz -> stack.testOperation(Long::eqz).bind()

        is NumericInstruction.I32GeS -> stack.relationalOperation(Int::ge).bind()
        is NumericInstruction.I64GeS -> stack.relationalOperation(Long::ge).bind()
        is NumericInstruction.F32Ge -> stack.relationalOperation(Float::ge).bind()
        is NumericInstruction.F64Ge -> stack.relationalOperation(Double::ge).bind()

        is NumericInstruction.I32GeU -> stack.relationalOperation(Int::geu).bind()
        is NumericInstruction.I64GeU -> stack.relationalOperation(Long::geu).bind()

        is NumericInstruction.I32GtS -> stack.relationalOperation(Int::gt).bind()
        is NumericInstruction.I64GtS -> stack.relationalOperation(Long::gt).bind()
        is NumericInstruction.F32Gt -> stack.relationalOperation(Float::gt).bind()
        is NumericInstruction.F64Gt -> stack.relationalOperation(Double::gt).bind()

        is NumericInstruction.I32GtU -> stack.relationalOperation(Int::gtu).bind()
        is NumericInstruction.I64GtU -> stack.relationalOperation(Long::gtu).bind()

        is NumericInstruction.I32LeS -> stack.relationalOperation(Int::le).bind()
        is NumericInstruction.I64LeS -> stack.relationalOperation(Long::le).bind()
        is NumericInstruction.F32Le -> stack.relationalOperation(Float::le).bind()
        is NumericInstruction.F64Le -> stack.relationalOperation(Double::le).bind()

        is NumericInstruction.I32LeU -> stack.relationalOperation(Int::leu).bind()
        is NumericInstruction.I64LeU -> stack.relationalOperation(Long::leu).bind()

        is NumericInstruction.I32LtS -> stack.relationalOperation(Int::lt).bind()
        is NumericInstruction.I64LtS -> stack.relationalOperation(Long::lt).bind()
        is NumericInstruction.F32Lt -> stack.relationalOperation(Float::lt).bind()
        is NumericInstruction.F64Lt -> stack.relationalOperation(Double::lt).bind()

        is NumericInstruction.I32LtU -> stack.relationalOperation(Int::ltu).bind()
        is NumericInstruction.I64LtU -> stack.relationalOperation(Long::ltu).bind()

        is NumericInstruction.I32WrapI64 -> stack.convertOperation(::I32, Long::wrap).bind()

        else -> Err(InvocationError.UnimplementedInstruction(ModuleInstruction(instruction))).bind<Unit>()
    }
}
