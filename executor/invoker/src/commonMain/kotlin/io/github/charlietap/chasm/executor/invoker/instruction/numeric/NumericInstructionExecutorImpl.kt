package io.github.charlietap.chasm.executor.invoker.instruction.numeric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.ext.binaryOperation
import io.github.charlietap.chasm.executor.invoker.ext.constOperation
import io.github.charlietap.chasm.executor.invoker.ext.convertOperation
import io.github.charlietap.chasm.executor.invoker.ext.countLeadingZero
import io.github.charlietap.chasm.executor.invoker.ext.countTrailingZero
import io.github.charlietap.chasm.executor.invoker.ext.eqz
import io.github.charlietap.chasm.executor.invoker.ext.ge
import io.github.charlietap.chasm.executor.invoker.ext.gt
import io.github.charlietap.chasm.executor.invoker.ext.le
import io.github.charlietap.chasm.executor.invoker.ext.lt
import io.github.charlietap.chasm.executor.invoker.ext.relationalOperation
import io.github.charlietap.chasm.executor.invoker.ext.testOperation
import io.github.charlietap.chasm.executor.invoker.ext.unaryOperation
import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

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

        is NumericInstruction.I32DivS -> stack.binaryOperation(Int::div).bind()
        is NumericInstruction.I64DivS -> stack.binaryOperation(Long::div).bind()
        is NumericInstruction.F32Div -> stack.binaryOperation(Float::div).bind()
        is NumericInstruction.F64Div -> stack.binaryOperation(Double::div).bind()

        is NumericInstruction.I32RemS -> stack.binaryOperation(Int::rem).bind()
        is NumericInstruction.I64RemS -> stack.binaryOperation(Long::rem).bind()

        is NumericInstruction.I32Clz -> stack.unaryOperation(Int::countLeadingZeroBits).bind()
        is NumericInstruction.I64Clz -> stack.unaryOperation(Long::countLeadingZero).bind()

        is NumericInstruction.I32Ctz -> stack.unaryOperation(Int::countTrailingZeroBits).bind()
        is NumericInstruction.I64Ctz -> stack.unaryOperation(Long::countTrailingZero).bind()

        is NumericInstruction.I32Eqz -> stack.testOperation(Int::eqz).bind()
        is NumericInstruction.I64Eqz -> stack.testOperation(Long::eqz).bind()

        is NumericInstruction.I32GeS -> stack.relationalOperation(Int::ge).bind()
        is NumericInstruction.I32GtS -> stack.relationalOperation(Int::gt).bind()

        is NumericInstruction.I32LeS -> stack.relationalOperation(Int::le).bind()
        is NumericInstruction.I32LtS -> stack.relationalOperation(Int::lt).bind()

        is NumericInstruction.I32WrapI64 -> stack.convertOperation(::I32, Long::wrap).bind()

        else -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
