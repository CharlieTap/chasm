package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction

class CompilerInstructionTagTranslator {

    fun translate(instruction: LinkedInstruction): String = when (instruction) {
        is AdminInstruction -> admin(instruction)
        is ControlInstruction -> control(instruction)
        is ControlSuperInstruction -> control(instruction)
        is VariableSuperInstruction -> variable(instruction)
        is ParametricSuperInstruction -> variant(instruction, "parametric", parametricOperations)
        is NumericSuperInstruction -> variant(instruction, "numeric", numericOperations)
        is MemorySuperInstruction -> variant(instruction, "memory", memoryOperations)
        is MemoryInstruction.DataDrop -> "memory.data_drop"
        is TableSuperInstruction -> variant(instruction, "table", tableOperations)
        is TableInstruction.ElemDrop -> "table.elem_drop"
        is ReferenceSuperInstruction -> reference(instruction)
        is AggregateSuperInstruction -> variant(instruction, "aggregate", aggregateOperations)
        else -> error("unsupported compiler instruction: ${instruction.javaClass.name}")
    }

    internal fun translateVariant(instructionClass: Class<*>): String = when {
        ParametricSuperInstruction::class.java.isAssignableFrom(instructionClass) ->
            variant(instructionClass.simpleName, "parametric", parametricOperations)
        NumericSuperInstruction::class.java.isAssignableFrom(instructionClass) ->
            variant(instructionClass.simpleName, "numeric", numericOperations)
        MemorySuperInstruction::class.java.isAssignableFrom(instructionClass) ->
            variant(instructionClass.simpleName, "memory", memoryOperations)
        TableSuperInstruction::class.java.isAssignableFrom(instructionClass) ->
            variant(instructionClass.simpleName, "table", tableOperations)
        AggregateSuperInstruction::class.java.isAssignableFrom(instructionClass) ->
            variant(instructionClass.simpleName, "aggregate", aggregateOperations)
        else -> error("unsupported compiler instruction variant: ${instructionClass.name}")
    }

    private fun admin(instruction: AdminInstruction): String = when (instruction) {
        AdminInstruction.EndFunction -> "admin.end_function"
        is AdminInstruction.CopySlot -> "admin.copy_slot"
        is AdminInstruction.CopySlots -> "admin.copy_slots"
        is AdminInstruction.Jump -> "admin.jump"
        is AdminInstruction.JumpIfI -> "admin.jump_if.i"
        is AdminInstruction.JumpIfS -> "admin.jump_if.s"
        is AdminInstruction.JumpIfV -> "admin.jump_if.v"
        is AdminInstruction.JumpIfZeroI -> "admin.jump_if_zero.i"
        is AdminInstruction.JumpIfZeroS -> "admin.jump_if_zero.s"
        is AdminInstruction.JumpIfCopyI -> "admin.jump_if_copy.i"
        is AdminInstruction.JumpIfCopyS -> "admin.jump_if_copy.s"
        is AdminInstruction.JumpIfCopyV -> "admin.jump_if_copy.v"
        is AdminInstruction.JumpIfCondition -> numericCondition(instruction.condition, "match")
        is AdminInstruction.JumpIfConditionMismatch -> numericCondition(instruction.condition, "mismatch")
        is AdminInstruction.JumpTableI -> "admin.jump_table.i"
        is AdminInstruction.JumpTableS -> "admin.jump_table.s"
        is AdminInstruction.JumpTableV -> "admin.jump_table.v"
        is AdminInstruction.JumpOnNullI -> "admin.jump_on_null.i"
        is AdminInstruction.JumpOnNullS -> "admin.jump_on_null.s"
        is AdminInstruction.JumpOnNullV -> "admin.jump_on_null.v"
        is AdminInstruction.JumpOnNonNullI -> "admin.jump_on_non_null.i"
        is AdminInstruction.JumpOnNonNullS -> "admin.jump_on_non_null.s"
        is AdminInstruction.JumpOnNonNullV -> "admin.jump_on_non_null.v"
        is AdminInstruction.JumpOnCastI -> "admin.jump_on_cast.i"
        is AdminInstruction.JumpOnCastS -> "admin.jump_on_cast.s"
        is AdminInstruction.JumpOnCastV -> "admin.jump_on_cast.v"
        is AdminInstruction.JumpOnCastFailI -> "admin.jump_on_cast_fail.i"
        is AdminInstruction.JumpOnCastFailS -> "admin.jump_on_cast_fail.s"
        is AdminInstruction.JumpOnCastFailV -> "admin.jump_on_cast_fail.v"
        is AdminInstruction.PushHandler -> "admin.push_handler"
        AdminInstruction.PopHandler -> "admin.pop_handler"
        AdminInstruction.PauseIf -> "admin.pause_if"
    }

    private fun control(instruction: ControlInstruction): String = when (instruction) {
        ControlInstruction.Unreachable -> "control.unreachable"
        ControlInstruction.Nop -> "control.nop"
        is ControlInstruction.Throw -> "control.throw"
        ControlInstruction.ThrowRef -> "control.throw_ref"
        ControlInstruction.Return -> "control.return"
        is ControlInstruction.ReturnWasmFunctionCall -> "control.return_call.wasm"
        is ControlInstruction.ReturnHostFunctionCall -> "control.return_call.host"
        is ControlInstruction.ReturnCallRef -> "control.return_call_ref"
        is ControlInstruction.WasmFunctionCall -> "control.call.wasm"
        is ControlInstruction.HostFunctionCall -> "control.call.host"
        is ControlInstruction.CallRef -> "control.call_ref"
        is ControlInstruction.CallIndirect -> "control.call_indirect"
        is ControlInstruction.ReturnCallIndirect -> "control.return_call_indirect"
    }

    private fun control(instruction: ControlSuperInstruction): String = when (instruction) {
        is ControlSuperInstruction.WasmCall -> if (instruction.plan.locals.isEmpty()) {
            "control.call.wasm.no_locals"
        } else {
            "control.call.wasm.locals"
        }
        is ControlSuperInstruction.HostCall -> "control.call.host"
        is ControlSuperInstruction.ReturnWasmCall -> "control.return_call.wasm"
        is ControlSuperInstruction.ReturnHostCall -> "control.return_call.host"
        is ControlSuperInstruction.CallIndirectI -> "control.call_indirect.i"
        is ControlSuperInstruction.CallIndirectS -> "control.call_indirect.s"
        is ControlSuperInstruction.CallRefS -> "control.call_ref"
        is ControlSuperInstruction.ReturnCallIndirectI -> "control.return_call_indirect.i"
        is ControlSuperInstruction.ReturnCallIndirectS -> "control.return_call_indirect.s"
        is ControlSuperInstruction.ReturnCallRefS -> "control.return_call_ref"
        is ControlSuperInstruction.Throw -> "control.throw"
        is ControlSuperInstruction.ThrowRefS -> "control.throw_ref"
    }

    private fun variable(instruction: VariableSuperInstruction): String = when (instruction) {
        is VariableSuperInstruction.GlobalGetS -> "variable.global_get"
        is VariableSuperInstruction.GlobalSetI -> "variable.global_set.i"
        is VariableSuperInstruction.GlobalSetS -> "variable.global_set.s"
        is VariableSuperInstruction.LocalSetI -> "variable.local_set.i"
        is VariableSuperInstruction.LocalSetS -> "variable.local_set.s"
    }

    private fun reference(instruction: ReferenceSuperInstruction): String = when (instruction) {
        is ReferenceSuperInstruction.RefCastS -> "reference.ref_cast"
        is ReferenceSuperInstruction.RefEqSs -> "reference.ref_eq"
        is ReferenceSuperInstruction.RefIsNullS -> "reference.ref_is_null"
        is ReferenceSuperInstruction.RefAsNonNullS -> "reference.ref_as_non_null"
        is ReferenceSuperInstruction.RefNullS -> "reference.ref_null"
        is ReferenceSuperInstruction.RefFuncS -> "reference.ref_func"
        is ReferenceSuperInstruction.RefTestS -> "reference.ref_test"
    }

    private fun numericCondition(instruction: NumericCondition, polarity: String): String {
        val (condition, operands) = when (instruction) {
            is NumericCondition.I32Eqz -> "i32.eqz" to listOf(instruction.operand)
            is NumericCondition.I64Eqz -> "i64.eqz" to listOf(instruction.operand)
            is NumericCondition.I32Eq -> "i32.eq" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32Ne -> "i32.ne" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32LtS -> "i32.lt_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32LtU -> "i32.lt_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32GtS -> "i32.gt_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32GtU -> "i32.gt_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32LeS -> "i32.le_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32LeU -> "i32.le_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32GeS -> "i32.ge_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I32GeU -> "i32.ge_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64Eq -> "i64.eq" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64Ne -> "i64.ne" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64LtS -> "i64.lt_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64LtU -> "i64.lt_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64GtS -> "i64.gt_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64GtU -> "i64.gt_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64LeS -> "i64.le_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64LeU -> "i64.le_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64GeS -> "i64.ge_s" to listOf(instruction.left, instruction.right)
            is NumericCondition.I64GeU -> "i64.ge_u" to listOf(instruction.left, instruction.right)
            is NumericCondition.F32Eq -> "f32.eq" to listOf(instruction.left, instruction.right)
            is NumericCondition.F32Ne -> "f32.ne" to listOf(instruction.left, instruction.right)
            is NumericCondition.F32Lt -> "f32.lt" to listOf(instruction.left, instruction.right)
            is NumericCondition.F32Gt -> "f32.gt" to listOf(instruction.left, instruction.right)
            is NumericCondition.F32Le -> "f32.le" to listOf(instruction.left, instruction.right)
            is NumericCondition.F32Ge -> "f32.ge" to listOf(instruction.left, instruction.right)
            is NumericCondition.F64Eq -> "f64.eq" to listOf(instruction.left, instruction.right)
            is NumericCondition.F64Ne -> "f64.ne" to listOf(instruction.left, instruction.right)
            is NumericCondition.F64Lt -> "f64.lt" to listOf(instruction.left, instruction.right)
            is NumericCondition.F64Gt -> "f64.gt" to listOf(instruction.left, instruction.right)
            is NumericCondition.F64Le -> "f64.le" to listOf(instruction.left, instruction.right)
            is NumericCondition.F64Ge -> "f64.ge" to listOf(instruction.left, instruction.right)
        }
        val shape = operands.joinToString(separator = "") { operand -> operandShape(operand).toString() }
        return "admin.jump_condition.$condition.$shape.$polarity"
    }

    private fun operandShape(operand: FusedOperand): Char = when (operand) {
        is FusedOperand.I32Const,
        is FusedOperand.I64Const,
        is FusedOperand.F32Const,
        is FusedOperand.F64Const,
        -> 'i'
        is FusedOperand.FrameSlot -> 's'
        else -> error("unsupported fused condition operand: $operand")
    }

    private fun variant(
        instruction: LinkedInstruction,
        namespace: String,
        operations: List<Operation>,
    ): String = variant(instruction.javaClass.simpleName, namespace, operations)

    private fun variant(
        className: String,
        namespace: String,
        operations: List<Operation>,
    ): String {
        val operation = operations.firstOrNull { operation -> operation.matches(className) }
            ?: error("unsupported $namespace compiler instruction: $className")
        val suffix = className.substring(operation.className.length).lowercase()
        val shape = suffix.take(operation.shapeLength)
        check(shape.all { character -> character == 'i' || character == 's' }) {
            "invalid $namespace compiler instruction shape: $className"
        }
        return if (shape.isEmpty()) {
            "$namespace.${operation.tag}"
        } else {
            "$namespace.${operation.tag}.$shape"
        }
    }
}

private data class Operation(
    val className: String,
    val tag: String,
    val shapeLength: Int,
    val classSuffixLength: Int = shapeLength,
) {
    fun matches(name: String): Boolean {
        if (!name.startsWith(className) || name.length != className.length + classSuffixLength) return false
        return name.substring(className.length).all { character ->
            character == 'I' || character == 'i' || character == 'S' || character == 's'
        }
    }
}

private fun operation(
    className: String,
    tag: String,
    shapeLength: Int,
    classSuffixLength: Int = shapeLength,
) = Operation(className, tag, shapeLength, classSuffixLength)

private val parametricOperations = listOf(
    operation("Select", "select", 3),
)

private val numericOperations = listOf(
    operation("I32Const", "i32.const", 0, 1),
    operation("I64Const", "i64.const", 0, 1),
    operation("F32Const", "f32.const", 0, 1),
    operation("F64Const", "f64.const", 0, 1),
    operation("I64Add128", "i64.add128", 4),
    operation("I64Sub128", "i64.sub128", 4),
    operation("I64MulWideS", "i64.mul_wide_s", 2),
    operation("I64MulWideU", "i64.mul_wide_u", 2),
    operation("I32TruncSatF32S", "i32.trunc_sat_f32_s", 1),
    operation("I32TruncSatF32U", "i32.trunc_sat_f32_u", 1),
    operation("I32TruncSatF64S", "i32.trunc_sat_f64_s", 1),
    operation("I32TruncSatF64U", "i32.trunc_sat_f64_u", 1),
    operation("I64TruncSatF32S", "i64.trunc_sat_f32_s", 1),
    operation("I64TruncSatF32U", "i64.trunc_sat_f32_u", 1),
    operation("I64TruncSatF64S", "i64.trunc_sat_f64_s", 1),
    operation("I64TruncSatF64U", "i64.trunc_sat_f64_u", 1),
    operation("I32Extend16S", "i32.extend16_s", 1),
    operation("I32Extend8S", "i32.extend8_s", 1),
    operation("I32ReinterpretF32", "i32.reinterpret_f32", 1),
    operation("I32TruncF32S", "i32.trunc_f32_s", 1),
    operation("I32TruncF32U", "i32.trunc_f32_u", 1),
    operation("I32TruncF64S", "i32.trunc_f64_s", 1),
    operation("I32TruncF64U", "i32.trunc_f64_u", 1),
    operation("I32WrapI64", "i32.wrap_i64", 1),
    operation("I64Extend16S", "i64.extend16_s", 1),
    operation("I64Extend32S", "i64.extend32_s", 1),
    operation("I64Extend8S", "i64.extend8_s", 1),
    operation("I64ExtendI32S", "i64.extend_i32_s", 1),
    operation("I64ExtendI32U", "i64.extend_i32_u", 1),
    operation("I64ReinterpretF64", "i64.reinterpret_f64", 1),
    operation("I64TruncF32S", "i64.trunc_f32_s", 1),
    operation("I64TruncF32U", "i64.trunc_f32_u", 1),
    operation("I64TruncF64S", "i64.trunc_f64_s", 1),
    operation("I64TruncF64U", "i64.trunc_f64_u", 1),
    operation("F32ConvertI32S", "f32.convert_i32_s", 1),
    operation("F32ConvertI32U", "f32.convert_i32_u", 1),
    operation("F32ConvertI64S", "f32.convert_i64_s", 1),
    operation("F32ConvertI64U", "f32.convert_i64_u", 1),
    operation("F32DemoteF64", "f32.demote_f64", 1),
    operation("F32ReinterpretI32", "f32.reinterpret_i32", 1),
    operation("F64ConvertI32S", "f64.convert_i32_s", 1),
    operation("F64ConvertI32U", "f64.convert_i32_u", 1),
    operation("F64ConvertI64S", "f64.convert_i64_s", 1),
    operation("F64ConvertI64U", "f64.convert_i64_u", 1),
    operation("F64PromoteF32", "f64.promote_f32", 1),
    operation("F64ReinterpretI64", "f64.reinterpret_i64", 1),
    operation("I32DivS", "i32.div_s", 2),
    operation("I32DivU", "i32.div_u", 2),
    operation("I32RemS", "i32.rem_s", 2),
    operation("I32RemU", "i32.rem_u", 2),
    operation("I32ShrS", "i32.shr_s", 2),
    operation("I32ShrU", "i32.shr_u", 2),
    operation("I64DivS", "i64.div_s", 2),
    operation("I64DivU", "i64.div_u", 2),
    operation("I64RemS", "i64.rem_s", 2),
    operation("I64RemU", "i64.rem_u", 2),
    operation("I64ShrS", "i64.shr_s", 2),
    operation("I64ShrU", "i64.shr_u", 2),
) + listOf(
    "I32Add" to "i32.add",
    "I32Sub" to "i32.sub",
    "I32Mul" to "i32.mul",
    "I32And" to "i32.and",
    "I32Or" to "i32.or",
    "I32Xor" to "i32.xor",
    "I32Shl" to "i32.shl",
    "I32Rotl" to "i32.rotl",
    "I32Rotr" to "i32.rotr",
    "I64Add" to "i64.add",
    "I64Sub" to "i64.sub",
    "I64Mul" to "i64.mul",
    "I64And" to "i64.and",
    "I64Or" to "i64.or",
    "I64Xor" to "i64.xor",
    "I64Shl" to "i64.shl",
    "I64Rotl" to "i64.rotl",
    "I64Rotr" to "i64.rotr",
    "I32Eq" to "i32.eq",
    "I32Ne" to "i32.ne",
    "I32LtS" to "i32.lt_s",
    "I32LtU" to "i32.lt_u",
    "I32GtS" to "i32.gt_s",
    "I32GtU" to "i32.gt_u",
    "I32LeS" to "i32.le_s",
    "I32LeU" to "i32.le_u",
    "I32GeS" to "i32.ge_s",
    "I32GeU" to "i32.ge_u",
    "I64Eq" to "i64.eq",
    "I64Ne" to "i64.ne",
    "I64LtS" to "i64.lt_s",
    "I64LtU" to "i64.lt_u",
    "I64GtS" to "i64.gt_s",
    "I64GtU" to "i64.gt_u",
    "I64LeS" to "i64.le_s",
    "I64LeU" to "i64.le_u",
    "I64GeS" to "i64.ge_s",
    "I64GeU" to "i64.ge_u",
    "F32Add" to "f32.add",
    "F32Sub" to "f32.sub",
    "F32Mul" to "f32.mul",
    "F32Div" to "f32.div",
    "F32Min" to "f32.min",
    "F32Max" to "f32.max",
    "F32Copysign" to "f32.copysign",
    "F32Eq" to "f32.eq",
    "F32Ne" to "f32.ne",
    "F32Lt" to "f32.lt",
    "F32Gt" to "f32.gt",
    "F32Le" to "f32.le",
    "F32Ge" to "f32.ge",
    "F64Add" to "f64.add",
    "F64Sub" to "f64.sub",
    "F64Mul" to "f64.mul",
    "F64Div" to "f64.div",
    "F64Min" to "f64.min",
    "F64Max" to "f64.max",
    "F64Copysign" to "f64.copysign",
    "F64Eq" to "f64.eq",
    "F64Ne" to "f64.ne",
    "F64Lt" to "f64.lt",
    "F64Gt" to "f64.gt",
    "F64Le" to "f64.le",
    "F64Ge" to "f64.ge",
).map { (className, tag) -> operation(className, tag, 2) } + listOf(
    "I32Eqz" to "i32.eqz",
    "I64Eqz" to "i64.eqz",
    "I32Clz" to "i32.clz",
    "I32Ctz" to "i32.ctz",
    "I32Popcnt" to "i32.popcnt",
    "I64Clz" to "i64.clz",
    "I64Ctz" to "i64.ctz",
    "I64Popcnt" to "i64.popcnt",
    "F32Abs" to "f32.abs",
    "F32Neg" to "f32.neg",
    "F32Ceil" to "f32.ceil",
    "F32Floor" to "f32.floor",
    "F32Trunc" to "f32.trunc",
    "F32Nearest" to "f32.nearest",
    "F32Sqrt" to "f32.sqrt",
    "F64Abs" to "f64.abs",
    "F64Neg" to "f64.neg",
    "F64Ceil" to "f64.ceil",
    "F64Floor" to "f64.floor",
    "F64Trunc" to "f64.trunc",
    "F64Nearest" to "f64.nearest",
    "F64Sqrt" to "f64.sqrt",
).map { (className, tag) -> operation(className, tag, 1) }

private val memoryOperations = listOf(
    operation("MemorySize", "size", 0, 1),
    operation("MemoryGrow", "grow", 1),
    operation("MemoryInit", "init", 3),
    operation("MemoryCopy", "copy", 3),
    operation("MemoryFill", "fill", 3),
) + listOf(
    "I32Load" to "i32.load",
    "I64Load" to "i64.load",
    "F32Load" to "f32.load",
    "F64Load" to "f64.load",
    "I32Load8S" to "i32.load8_s",
    "I32Load8U" to "i32.load8_u",
    "I32Load16S" to "i32.load16_s",
    "I32Load16U" to "i32.load16_u",
    "I64Load8S" to "i64.load8_s",
    "I64Load8U" to "i64.load8_u",
    "I64Load16S" to "i64.load16_s",
    "I64Load16U" to "i64.load16_u",
    "I64Load32S" to "i64.load32_s",
    "I64Load32U" to "i64.load32_u",
).map { (className, tag) -> operation(className, tag, 1) } + listOf(
    "I32Store" to "i32.store",
    "I64Store" to "i64.store",
    "F32Store" to "f32.store",
    "F64Store" to "f64.store",
    "I32Store8" to "i32.store8",
    "I32Store16" to "i32.store16",
    "I64Store8" to "i64.store8",
    "I64Store16" to "i64.store16",
    "I64Store32" to "i64.store32",
).map { (className, tag) -> operation(className, tag, 2) }

private val tableOperations = listOf(
    operation("TableCopy", "copy", 3),
    operation("TableFill", "fill", 3),
    operation("TableInit", "init", 3),
    operation("TableGrow", "grow", 2),
    operation("TableSet", "set", 2),
    operation("TableGet", "get", 1),
    operation("TableSize", "size", 0, 1),
)

private val aggregateOperations = listOf(
    operation("ArrayCopy", "array.copy", 3),
    operation("ArrayFill", "array.fill", 3),
    operation("ArrayInitData", "array.init_data", 3),
    operation("ArrayInitElement", "array.init_element", 3),
    operation("ArrayNewData", "array.new_data", 2),
    operation("ArrayNewElement", "array.new_element", 2),
    operation("ArrayNewDefault", "array.new_default", 1),
    operation("ArrayNewFixed", "array.new_fixed", 0, 1),
    operation("ArrayNew", "array.new", 2),
    operation("ArrayGetSigned", "array.get_signed", 1),
    operation("ArrayGetUnsigned", "array.get_unsigned", 1),
    operation("ArrayGet", "array.get", 1),
    operation("ArraySet", "array.set", 2),
    operation("ArrayLen", "array.len", 0, 1),
    operation("RefI31", "ref_i31", 1),
    operation("I31GetSigned", "i31.get_signed", 0, 1),
    operation("I31GetUnsigned", "i31.get_unsigned", 0, 1),
    operation("AnyConvertExtern", "any.convert_extern", 0, 1),
    operation("ExternConvertAny", "extern.convert_any", 0, 1),
    operation("RefCastStructGet", "ref_cast_struct_get", 0, 1),
    operation("StructGetStructGet", "struct_get_struct_get", 0, 1),
    operation("LocalSetStructGet", "local_set_struct_get", 0, 1),
    operation("StructGetSigned", "struct.get_signed", 0, 1),
    operation("StructGetUnsigned", "struct.get_unsigned", 0, 1),
    operation("StructGet", "struct.get", 0, 1),
    operation("StructNewDefault", "struct.new_default", 0, 1),
    operation("StructNew", "struct.new", 0, 1),
    operation("StructSet", "struct.set", 1),
)
