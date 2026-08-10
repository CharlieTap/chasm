package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.f32Immediate
import io.github.charlietap.chasm.compiler.operand.f64Immediate
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.NumericSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

internal fun FunctionCompilationContext.emitNumericInstruction(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
) {
    val linkedInstruction = when (opcode) {
        NumericOpcode.I32Eqz,
        NumericOpcode.I32Eq,
        NumericOpcode.I32Ne,
        NumericOpcode.I32LtS,
        NumericOpcode.I32LtU,
        NumericOpcode.I32GtS,
        NumericOpcode.I32GtU,
        NumericOpcode.I32LeS,
        NumericOpcode.I32LeU,
        NumericOpcode.I32GeS,
        NumericOpcode.I32GeU,
        -> emitI32Comparison(opcode, first, second, destinationSlot)
        NumericOpcode.I64Eqz,
        NumericOpcode.I64Eq,
        NumericOpcode.I64Ne,
        NumericOpcode.I64LtS,
        NumericOpcode.I64LtU,
        NumericOpcode.I64GtS,
        NumericOpcode.I64GtU,
        NumericOpcode.I64LeS,
        NumericOpcode.I64LeU,
        NumericOpcode.I64GeS,
        NumericOpcode.I64GeU,
        -> emitI64Comparison(opcode, first, second, destinationSlot)
        NumericOpcode.F32Eq,
        NumericOpcode.F32Ne,
        NumericOpcode.F32Lt,
        NumericOpcode.F32Gt,
        NumericOpcode.F32Le,
        NumericOpcode.F32Ge,
        -> emitF32Comparison(opcode, first, second, destinationSlot)
        NumericOpcode.F64Eq,
        NumericOpcode.F64Ne,
        NumericOpcode.F64Lt,
        NumericOpcode.F64Gt,
        NumericOpcode.F64Le,
        NumericOpcode.F64Ge,
        -> emitF64Comparison(opcode, first, second, destinationSlot)
        NumericOpcode.I32Clz,
        NumericOpcode.I32Ctz,
        NumericOpcode.I32Popcnt,
        -> emitI32Unary(opcode, first, destinationSlot)
        NumericOpcode.I32Add,
        NumericOpcode.I32Sub,
        NumericOpcode.I32Mul,
        NumericOpcode.I32DivS,
        NumericOpcode.I32DivU,
        NumericOpcode.I32RemS,
        NumericOpcode.I32RemU,
        NumericOpcode.I32And,
        NumericOpcode.I32Or,
        NumericOpcode.I32Xor,
        NumericOpcode.I32Shl,
        NumericOpcode.I32ShrS,
        NumericOpcode.I32ShrU,
        NumericOpcode.I32Rotl,
        NumericOpcode.I32Rotr,
        -> emitI32Binary(opcode, first, second, destinationSlot)
        NumericOpcode.I64Clz,
        NumericOpcode.I64Ctz,
        NumericOpcode.I64Popcnt,
        -> emitI64Unary(opcode, first, destinationSlot)
        NumericOpcode.I64Add,
        NumericOpcode.I64Sub,
        NumericOpcode.I64Mul,
        NumericOpcode.I64DivS,
        NumericOpcode.I64DivU,
        NumericOpcode.I64RemS,
        NumericOpcode.I64RemU,
        NumericOpcode.I64And,
        NumericOpcode.I64Or,
        NumericOpcode.I64Xor,
        NumericOpcode.I64Shl,
        NumericOpcode.I64ShrS,
        NumericOpcode.I64ShrU,
        NumericOpcode.I64Rotl,
        NumericOpcode.I64Rotr,
        -> emitI64Binary(opcode, first, second, destinationSlot)
        NumericOpcode.F32Abs,
        NumericOpcode.F32Neg,
        NumericOpcode.F32Ceil,
        NumericOpcode.F32Floor,
        NumericOpcode.F32Trunc,
        NumericOpcode.F32Nearest,
        NumericOpcode.F32Sqrt,
        -> emitF32Unary(opcode, first, destinationSlot)
        NumericOpcode.F32Add,
        NumericOpcode.F32Sub,
        NumericOpcode.F32Mul,
        NumericOpcode.F32Div,
        NumericOpcode.F32Min,
        NumericOpcode.F32Max,
        NumericOpcode.F32Copysign,
        -> emitF32Binary(opcode, first, second, destinationSlot)
        NumericOpcode.F64Abs,
        NumericOpcode.F64Neg,
        NumericOpcode.F64Ceil,
        NumericOpcode.F64Floor,
        NumericOpcode.F64Trunc,
        NumericOpcode.F64Nearest,
        NumericOpcode.F64Sqrt,
        -> emitF64Unary(opcode, first, destinationSlot)
        NumericOpcode.F64Add,
        NumericOpcode.F64Sub,
        NumericOpcode.F64Mul,
        NumericOpcode.F64Div,
        NumericOpcode.F64Min,
        NumericOpcode.F64Max,
        NumericOpcode.F64Copysign,
        -> emitF64Binary(opcode, first, second, destinationSlot)
        NumericOpcode.I32WrapI64,
        NumericOpcode.I32TruncF32S,
        NumericOpcode.I32TruncF32U,
        NumericOpcode.I32TruncF64S,
        NumericOpcode.I32TruncF64U,
        NumericOpcode.I32ReinterpretF32,
        NumericOpcode.I32Extend8S,
        NumericOpcode.I32Extend16S,
        NumericOpcode.I32TruncSatF32S,
        NumericOpcode.I32TruncSatF32U,
        NumericOpcode.I32TruncSatF64S,
        NumericOpcode.I32TruncSatF64U,
        -> emitI32Conversion(opcode, first, destinationSlot)
        NumericOpcode.I64ExtendI32S,
        NumericOpcode.I64ExtendI32U,
        NumericOpcode.I64TruncF32S,
        NumericOpcode.I64TruncF32U,
        NumericOpcode.I64TruncF64S,
        NumericOpcode.I64TruncF64U,
        NumericOpcode.I64ReinterpretF64,
        NumericOpcode.I64Extend8S,
        NumericOpcode.I64Extend16S,
        NumericOpcode.I64Extend32S,
        NumericOpcode.I64TruncSatF32S,
        NumericOpcode.I64TruncSatF32U,
        NumericOpcode.I64TruncSatF64S,
        NumericOpcode.I64TruncSatF64U,
        -> emitI64Conversion(opcode, first, destinationSlot)
        NumericOpcode.F32ConvertI32S,
        NumericOpcode.F32ConvertI32U,
        NumericOpcode.F32ConvertI64S,
        NumericOpcode.F32ConvertI64U,
        NumericOpcode.F32DemoteF64,
        NumericOpcode.F32ReinterpretI32,
        -> emitF32Conversion(opcode, first, destinationSlot)
        NumericOpcode.F64ConvertI32S,
        NumericOpcode.F64ConvertI32U,
        NumericOpcode.F64ConvertI64S,
        NumericOpcode.F64ConvertI64U,
        NumericOpcode.F64PromoteF32,
        NumericOpcode.F64ReinterpretI64,
        -> emitF64Conversion(opcode, first, destinationSlot)
        NumericOpcode.I64Add128,
        NumericOpcode.I64MulWideS,
        NumericOpcode.I64MulWideU,
        NumericOpcode.I64Sub128,
        -> error("numeric instruction requires dedicated lowering: $opcode")
    }
    program.append(NumericSuperInstructionDispatcher(linkedInstruction))
}

private fun FunctionCompilationContext.emitI32Comparison(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I32Eqz -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32EqzI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32EqzS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32Eq -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32EqIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32EqIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32EqSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32EqSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Ne -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32NeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32NeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32NeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32NeSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32LtS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32LtSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32LtSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32LtSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32LtSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32LtU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32LtUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32LtUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32LtUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32LtUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32GtS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32GtSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32GtSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32GtSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32GtSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32GtU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32GtUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32GtUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32GtUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32GtUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32LeS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32LeSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32LeSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32LeSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32LeSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32LeU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32LeUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32LeUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32LeUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32LeUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32GeS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32GeSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32GeSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32GeSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32GeSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32GeU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32GeUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32GeUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32GeUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32GeUSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI64Comparison(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I64Eqz -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64EqzI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64EqzS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64Eq -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64EqIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64EqIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64EqSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64EqSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Ne -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64NeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64NeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64NeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64NeSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64LtS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64LtSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64LtSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64LtSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64LtSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64LtU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64LtUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64LtUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64LtUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64LtUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64GtS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64GtSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64GtSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64GtSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64GtSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64GtU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64GtUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64GtUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64GtUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64GtUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64LeS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64LeSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64LeSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64LeSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64LeSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64LeU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64LeUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64LeUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64LeUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64LeUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64GeS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64GeSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64GeSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64GeSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64GeSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64GeU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64GeUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64GeUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64GeUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64GeUSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF32Comparison(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F32Eq -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32EqIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32EqIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32EqSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32EqSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Ne -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32NeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32NeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32NeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32NeSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Lt -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32LtIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32LtIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32LtSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32LtSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Gt -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32GtIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32GtIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32GtSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32GtSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Le -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32LeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32LeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32LeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32LeSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Ge -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32GeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32GeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32GeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32GeSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF64Comparison(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F64Eq -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64EqIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64EqIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64EqSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64EqSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Ne -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64NeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64NeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64NeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64NeSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Lt -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64LtIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64LtIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64LtSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64LtSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Gt -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64GtIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64GtIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64GtSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64GtSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Le -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64LeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64LeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64LeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64LeSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Ge -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64GeIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64GeIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64GeSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64GeSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI32Unary(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I32Clz -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32ClzI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32ClzS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32Ctz -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32CtzI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32CtzS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32Popcnt -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32PopcntI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32PopcntS(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI32Binary(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I32Add -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32AddIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32AddIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32AddSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32AddSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Sub -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32SubIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32SubIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32SubSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32SubSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Mul -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32MulIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32MulIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32MulSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32MulSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32DivS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32DivSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32DivSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32DivSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32DivSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32DivU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32DivUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32DivUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32DivUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32DivUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32RemS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32RemSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32RemSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32RemSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32RemSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32RemU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32RemUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32RemUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32RemUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32RemUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32And -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32AndIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32AndIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32AndSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32AndSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Or -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32OrIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32OrIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32OrSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32OrSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Xor -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32XorIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32XorIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32XorSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32XorSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Shl -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32ShlIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32ShlIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32ShlSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32ShlSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32ShrS -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32ShrSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32ShrSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32ShrSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32ShrSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32ShrU -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32ShrUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32ShrUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32ShrUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32ShrUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Rotl -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32RotlIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32RotlIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32RotlSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32RotlSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I32Rotr -> strictI32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I32RotrIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I32RotrIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I32RotrSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I32RotrSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI64Unary(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I64Clz -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64ClzI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64ClzS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64Ctz -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64CtzI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64CtzS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64Popcnt -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64PopcntI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64PopcntS(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI64Binary(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I64Add -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64AddIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64AddIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64AddSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64AddSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Sub -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64SubIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64SubIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64SubSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64SubSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Mul -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64MulIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64MulIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64MulSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64MulSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64DivS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64DivSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64DivSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64DivSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64DivSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64DivU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64DivUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64DivUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64DivUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64DivUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64RemS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64RemSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64RemSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64RemSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64RemSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64RemU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64RemUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64RemUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64RemUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64RemUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64And -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64AndIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64AndIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64AndSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64AndSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Or -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64OrIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64OrIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64OrSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64OrSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Xor -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64XorIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64XorIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64XorSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64XorSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Shl -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64ShlIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64ShlIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64ShlSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64ShlSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64ShrS -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64ShrSIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64ShrSIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64ShrSSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64ShrSSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64ShrU -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64ShrUIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64ShrUIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64ShrUSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64ShrUSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Rotl -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64RotlIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64RotlIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64RotlSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64RotlSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.I64Rotr -> strictI64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.I64RotrIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.I64RotrIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.I64RotrSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64RotrSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF32Unary(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F32Abs -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32AbsI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32AbsS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32Neg -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32NegI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32NegS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32Ceil -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32CeilI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32CeilS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32Floor -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32FloorI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32FloorS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32Trunc -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32TruncI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32TruncS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32Nearest -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32NearestI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32NearestS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32Sqrt -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32SqrtI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32SqrtS(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF32Binary(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F32Add -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32AddIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32AddIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32AddSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32AddSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Sub -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32SubIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32SubIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32SubSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32SubSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Mul -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32MulIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32MulIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32MulSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32MulSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Div -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32DivIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32DivIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32DivSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32DivSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Min -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32MinIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32MinIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32MinSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32MinSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Max -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32MaxIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32MaxIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32MaxSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32MaxSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F32Copysign -> strictF32Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F32CopysignIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F32CopysignIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F32CopysignSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F32CopysignSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF64Unary(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F64Abs -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64AbsI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64AbsS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64Neg -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64NegI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64NegS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64Ceil -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64CeilI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64CeilS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64Floor -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64FloorI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64FloorS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64Trunc -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64TruncI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64TruncS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64Nearest -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64NearestI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64NearestS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64Sqrt -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64SqrtI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64SqrtS(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF64Binary(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F64Add -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64AddIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64AddIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64AddSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64AddSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Sub -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64SubIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64SubIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64SubSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64SubSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Mul -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64MulIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64MulIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64MulSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64MulSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Div -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64DivIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64DivIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64DivSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64DivSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Min -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64MinIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64MinIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64MinSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64MinSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Max -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64MaxIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64MaxIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64MaxSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64MaxSs(leftSlot, rightSlot, destinationSlot) },
    )
    NumericOpcode.F64Copysign -> strictF64Binary(
        left = first,
        right = second,
        ii = { left, right -> NumericSuperInstruction.F64CopysignIi(left, right, destinationSlot) },
        `is` = { left, rightSlot -> NumericSuperInstruction.F64CopysignIs(left, rightSlot, destinationSlot) },
        si = { leftSlot, right -> NumericSuperInstruction.F64CopysignSi(leftSlot, right, destinationSlot) },
        ss = { leftSlot, rightSlot -> NumericSuperInstruction.F64CopysignSs(leftSlot, rightSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI32Conversion(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I32WrapI64 -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32WrapI64I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32WrapI64S(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncF32S -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncF32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncF32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncF32U -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncF32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncF32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncF64S -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncF64SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncF64SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncF64U -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncF64UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncF64US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32ReinterpretF32 -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32ReinterpretF32I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32ReinterpretF32S(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32Extend8S -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32Extend8SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32Extend8SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32Extend16S -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32Extend16SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32Extend16SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncSatF32S -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncSatF32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncSatF32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncSatF32U -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncSatF32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncSatF32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncSatF64S -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncSatF64SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncSatF64SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I32TruncSatF64U -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I32TruncSatF64UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I32TruncSatF64US(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitI64Conversion(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.I64ExtendI32S -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64ExtendI32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64ExtendI32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64ExtendI32U -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64ExtendI32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64ExtendI32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncF32S -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncF32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncF32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncF32U -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncF32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncF32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncF64S -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncF64SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncF64SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncF64U -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncF64UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncF64US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64ReinterpretF64 -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64ReinterpretF64I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64ReinterpretF64S(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64Extend8S -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64Extend8SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64Extend8SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64Extend16S -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64Extend16SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64Extend16SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64Extend32S -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64Extend32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64Extend32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncSatF32S -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncSatF32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncSatF32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncSatF32U -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncSatF32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncSatF32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncSatF64S -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncSatF64SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncSatF64SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.I64TruncSatF64U -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.I64TruncSatF64UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.I64TruncSatF64US(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF32Conversion(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F32ConvertI32S -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32ConvertI32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32ConvertI32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32ConvertI32U -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32ConvertI32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32ConvertI32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32ConvertI64S -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32ConvertI64SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32ConvertI64SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32ConvertI64U -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32ConvertI64UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32ConvertI64US(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32DemoteF64 -> strictF64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32DemoteF64I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32DemoteF64S(operandSlot, destinationSlot) },
    )
    NumericOpcode.F32ReinterpretI32 -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F32ReinterpretI32I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F32ReinterpretI32S(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private fun FunctionCompilationContext.emitF64Conversion(
    opcode: NumericOpcode,
    first: OperandSource,
    destinationSlot: Int,
): NumericSuperInstruction = when (opcode) {
    NumericOpcode.F64ConvertI32S -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64ConvertI32SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64ConvertI32SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64ConvertI32U -> strictI32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64ConvertI32UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64ConvertI32US(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64ConvertI64S -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64ConvertI64SI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64ConvertI64SS(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64ConvertI64U -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64ConvertI64UI(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64ConvertI64US(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64PromoteF32 -> strictF32Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64PromoteF32I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64PromoteF32S(operandSlot, destinationSlot) },
    )
    NumericOpcode.F64ReinterpretI64 -> strictI64Unary(
        operand = first,
        i = { operand -> NumericSuperInstruction.F64ReinterpretI64I(operand, destinationSlot) },
        s = { operandSlot -> NumericSuperInstruction.F64ReinterpretI64S(operandSlot, destinationSlot) },
    )
    else -> error("unexpected numeric opcode: $opcode")
}

private inline fun strictI32Unary(
    operand: OperandSource,
    i: (Int) -> NumericSuperInstruction,
    s: (Int) -> NumericSuperInstruction,
): NumericSuperInstruction = when (operand.sourceKind) {
    OperandSourceKind.I32Immediate -> i(operand.i32Immediate)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> s(operand.sourceSlot)
    else -> error("unexpected operand source: $operand")
}

private inline fun strictI32Binary(
    left: OperandSource,
    right: OperandSource,
    ii: (Int, Int) -> NumericSuperInstruction,
    `is`: (Int, Int) -> NumericSuperInstruction,
    si: (Int, Int) -> NumericSuperInstruction,
    ss: (Int, Int) -> NumericSuperInstruction,
): NumericSuperInstruction {
    return if (left.sourceKind == OperandSourceKind.I32Immediate) {
        if (right.sourceKind == OperandSourceKind.I32Immediate) {
            ii(left.i32Immediate, right.i32Immediate)
        } else {
            `is`(left.i32Immediate, right.requireSlot())
        }
    } else if (right.sourceKind == OperandSourceKind.I32Immediate) {
        si(left.requireSlot(), right.i32Immediate)
    } else {
        ss(left.requireSlot(), right.requireSlot())
    }
}

private inline fun strictI64Unary(
    operand: OperandSource,
    i: (Long) -> NumericSuperInstruction,
    s: (Int) -> NumericSuperInstruction,
): NumericSuperInstruction = when (operand.sourceKind) {
    OperandSourceKind.I64Immediate -> i(operand.i64Immediate)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> s(operand.sourceSlot)
    else -> error("unexpected operand source: $operand")
}

private inline fun strictI64Binary(
    left: OperandSource,
    right: OperandSource,
    ii: (Long, Long) -> NumericSuperInstruction,
    `is`: (Long, Int) -> NumericSuperInstruction,
    si: (Int, Long) -> NumericSuperInstruction,
    ss: (Int, Int) -> NumericSuperInstruction,
): NumericSuperInstruction {
    return if (left.sourceKind == OperandSourceKind.I64Immediate) {
        if (right.sourceKind == OperandSourceKind.I64Immediate) {
            ii(left.i64Immediate, right.i64Immediate)
        } else {
            `is`(left.i64Immediate, right.requireSlot())
        }
    } else if (right.sourceKind == OperandSourceKind.I64Immediate) {
        si(left.requireSlot(), right.i64Immediate)
    } else {
        ss(left.requireSlot(), right.requireSlot())
    }
}

private inline fun strictF32Unary(
    operand: OperandSource,
    i: (Float) -> NumericSuperInstruction,
    s: (Int) -> NumericSuperInstruction,
): NumericSuperInstruction = when (operand.sourceKind) {
    OperandSourceKind.F32Immediate -> i(operand.f32Immediate)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> s(operand.sourceSlot)
    else -> error("unexpected operand source: $operand")
}

private inline fun strictF32Binary(
    left: OperandSource,
    right: OperandSource,
    ii: (Float, Float) -> NumericSuperInstruction,
    `is`: (Float, Int) -> NumericSuperInstruction,
    si: (Int, Float) -> NumericSuperInstruction,
    ss: (Int, Int) -> NumericSuperInstruction,
): NumericSuperInstruction {
    return if (left.sourceKind == OperandSourceKind.F32Immediate) {
        if (right.sourceKind == OperandSourceKind.F32Immediate) {
            ii(left.f32Immediate, right.f32Immediate)
        } else {
            `is`(left.f32Immediate, right.requireSlot())
        }
    } else if (right.sourceKind == OperandSourceKind.F32Immediate) {
        si(left.requireSlot(), right.f32Immediate)
    } else {
        ss(left.requireSlot(), right.requireSlot())
    }
}

private inline fun strictF64Unary(
    operand: OperandSource,
    i: (Double) -> NumericSuperInstruction,
    s: (Int) -> NumericSuperInstruction,
): NumericSuperInstruction = when (operand.sourceKind) {
    OperandSourceKind.F64Immediate -> i(operand.f64Immediate)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> s(operand.sourceSlot)
    else -> error("unexpected operand source: $operand")
}

private inline fun strictF64Binary(
    left: OperandSource,
    right: OperandSource,
    ii: (Double, Double) -> NumericSuperInstruction,
    `is`: (Double, Int) -> NumericSuperInstruction,
    si: (Int, Double) -> NumericSuperInstruction,
    ss: (Int, Int) -> NumericSuperInstruction,
): NumericSuperInstruction {
    return if (left.sourceKind == OperandSourceKind.F64Immediate) {
        if (right.sourceKind == OperandSourceKind.F64Immediate) {
            ii(left.f64Immediate, right.f64Immediate)
        } else {
            `is`(left.f64Immediate, right.requireSlot())
        }
    } else if (right.sourceKind == OperandSourceKind.F64Immediate) {
        si(left.requireSlot(), right.f64Immediate)
    } else {
        ss(left.requireSlot(), right.requireSlot())
    }
}

private fun OperandSource.requireSlot(): Int = when (sourceKind) {
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> sourceSlot
    else -> error("operand source does not reference a slot: $this")
}
