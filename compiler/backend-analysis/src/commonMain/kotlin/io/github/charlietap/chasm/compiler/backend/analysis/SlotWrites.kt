package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.ext.toType
import io.github.charlietap.chasm.compiler.backend.analysis.form.NumericForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.form
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.compiler.backend.analysis.form.operands
import io.github.charlietap.chasm.compiler.backend.analysis.form.select
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

interface SlotWriteVisitor {

    fun value(
        slot: Int,
        type: ValueType,
    )

    fun opaque(
        slot: Int,
        reference: Boolean,
    )

    fun copy(
        source: Operand,
        destination: Int,
    )
}

fun visitSlotWrites(
    instruction: Instruction,
    function: CirFunction,
    declarations: ModuleIndexSpaces,
    visitor: SlotWriteVisitor,
) {
    when (instruction) {
        is NumericInstruction -> when (val form = instruction.form()) {
            is NumericForm.Constant -> visitor.number(form.destination, form.type)
            is NumericForm.BitFieldExtract -> visitor.number(form.destination, form.resultType)
            is NumericForm.Unary -> visitor.number(form.destination, form.resultType)
            is NumericForm.Binary -> visitor.number(form.destination, form.resultType)
            is NumericForm.Compare -> visitor.number(form.destination, form.resultType)
            is NumericForm.Convert -> visitor.number(form.destination, form.resultType)
            is NumericForm.Wide -> {
                visitor.number(form.low, NumberType.I64)
                visitor.number(form.high, NumberType.I64)
            }
        }
        is MemoryInstruction -> {
            val access = instruction.access()
            if (access != null) {
                if (access.isLoad) visitor.number(access.destination, access.operation.type)
            } else {
                instruction.helperCall()?.destination?.let { destination -> visitor.opaque(destination, reference = false) }
            }
        }
        is ParametricInstruction -> {
            val select = instruction.select()
            visitor.copy(select.first, select.destination)
            visitor.copy(select.second, select.destination)
        }
        is AdminInstruction -> visitAdminWrites(instruction, visitor)
        is VariableInstruction.GlobalGetS -> visitor.value(instruction.destinationSlot, declarations.globalType(instruction.global))
        is VariableInstruction -> Unit
        is ControlInstruction -> visitControlWrites(instruction, function, declarations, visitor)
        is ReferenceInstruction -> visitReferenceWrites(instruction, visitor)
        is AggregateInstruction -> visitAggregateWrites(instruction, declarations, visitor)
        is TableInstruction -> instruction.helperCall()?.let { call ->
            call.destination?.let { destination -> visitor.opaque(destination, call.producesReference) }
        }
    }
}

private fun SlotWriteVisitor.number(
    slot: Int,
    type: NumberType,
) = value(slot, ValueType.Number(type))

private val I32 = ValueType.Number(NumberType.I32)

private fun visitAdminWrites(
    instruction: AdminInstruction,
    visitor: SlotWriteVisitor,
) {
    when (instruction) {
        is AdminInstruction.CopySlot -> visitor.copy(slot(instruction.sourceSlot), instruction.destinationSlot)
        is AdminInstruction.CopySlots -> instruction.sourceSlots.indices.forEach { index ->
            visitor.copy(slot(instruction.sourceSlots[index]), instruction.destinationSlots[index])
        }
        is AdminInstruction.CopySlotSequence -> instruction.sourceSlots.indices.forEach { index ->
            visitor.copy(slot(instruction.sourceSlots[index]), instruction.destinationSlots[index])
        }
        is AdminInstruction.JumpCopies -> instruction.operands.operands().forEachIndexed { index, source ->
            visitor.copy(source, instruction.operands.destinationSlotBase + index)
        }
        is AdminInstruction.JumpIfCopyI -> visitor.copy(slot(instruction.sourceSlot), instruction.destinationSlot)
        is AdminInstruction.JumpIfCopyS -> visitor.copy(slot(instruction.sourceSlot), instruction.destinationSlot)
        else -> Unit
    }
}

private fun visitControlWrites(
    instruction: ControlInstruction,
    function: CirFunction,
    declarations: ModuleIndexSpaces,
    visitor: SlotWriteVisitor,
) {
    val call = instruction.callSite(declarations)
    if (call != null) {
        call.type.results.types.forEachIndexed { index, type -> visitor.value(call.resultBase + index, type) }
        return
    }
    if (instruction is ControlInstruction.ReturnCall && instruction.function == function.index) {
        instruction.operands.wasm.operands().forEachIndexed { index, source -> visitor.copy(source, index) }
    }
}

private fun visitReferenceWrites(
    instruction: ReferenceInstruction,
    visitor: SlotWriteVisitor,
) {
    when (instruction) {
        is ReferenceInstruction.RefIsNullS -> visitor.value(instruction.destinationSlot, I32)
        is ReferenceInstruction.RefEqSs -> visitor.value(instruction.destinationSlot, I32)
        is ReferenceInstruction.RefTestS -> visitor.value(instruction.destinationSlot, I32)
        is ReferenceInstruction.RefNullS ->
            visitor.value(instruction.destinationSlot, ValueType.Reference(ReferenceType.RefNull(instruction.heapType.toType())))
        is ReferenceInstruction.RefFuncS -> visitor.opaque(instruction.destinationSlot, reference = false)
        is ReferenceInstruction.RefAsNonNullS -> visitor.opaque(instruction.destinationSlot, reference = true)
        is ReferenceInstruction.RefCastS -> visitor.opaque(instruction.destinationSlot, reference = true)
    }
}

private fun visitAggregateWrites(
    instruction: AggregateInstruction,
    declarations: ModuleIndexSpaces,
    visitor: SlotWriteVisitor,
) {
    when (instruction) {
        is AggregateInstruction.StructGetS ->
            visitor.value(instruction.destinationSlot, declarations.structFieldType(instruction.type, instruction.fieldIndex))
        is AggregateInstruction.StructGetSignedS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.StructGetUnsignedS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.RefCastStructGetS ->
            visitor.value(instruction.destinationSlot, declarations.structFieldType(instruction.type, instruction.fieldIndex))
        is AggregateInstruction.StructGetStructGetS ->
            visitor.value(instruction.destinationSlot, declarations.structFieldType(instruction.secondType, instruction.secondFieldIndex))
        is AggregateInstruction.LocalSetStructGetS -> {
            visitor.copy(slot(instruction.sourceSlot), instruction.localSlot)
            visitor.value(instruction.destinationSlot, declarations.structFieldType(instruction.type, instruction.fieldIndex))
        }
        is AggregateInstruction.ArrayGetI -> visitor.value(instruction.destinationSlot, declarations.arrayElementType(instruction.type))
        is AggregateInstruction.ArrayGetS -> visitor.value(instruction.destinationSlot, declarations.arrayElementType(instruction.type))
        is AggregateInstruction.ArrayGetSignedI -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.ArrayGetSignedS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.ArrayGetUnsignedI -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.ArrayGetUnsignedS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.ArrayLenS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.I31GetSignedS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.I31GetUnsignedS -> visitor.value(instruction.destinationSlot, I32)
        is AggregateInstruction.RefI31I -> visitor.opaque(instruction.destinationSlot, reference = false)
        is AggregateInstruction.RefI31S -> visitor.opaque(instruction.destinationSlot, reference = false)
        is AggregateInstruction.StructNewS -> visitor.opaque(instruction.destinationSlot, reference = true)
        is AggregateInstruction.StructNewDefaultS -> visitor.opaque(instruction.destinationSlot, reference = true)
        is AggregateInstruction.ArrayNewDefaultI -> visitor.opaque(instruction.destinationSlot, reference = true)
        is AggregateInstruction.ArrayNewDefaultS -> visitor.opaque(instruction.destinationSlot, reference = true)
        is AggregateInstruction.ArrayNewFixedS -> visitor.opaque(instruction.destinationSlot, reference = true)
        else -> instruction.helperCall()?.let { call ->
            call.destination?.let { destination -> visitor.opaque(destination, call.producesReference) }
        }
    }
}
