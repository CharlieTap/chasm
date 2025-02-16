package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction as IRAggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction as IRAtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction as IRControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction as IRInstruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction as IRMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction as IRNumericInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction as IRParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction as IRReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction as IRTableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction as IRVariableInstruction

internal fun InstructionFactory(
    instruction: Instruction,
): IRInstruction = InstructionFactory(
    instruction = instruction,
    aggregateInstructionFactory = ::AggregateInstructionFactory,
    atomicInstructionFactory = ::AtomicMemoryInstructionFactory,
    controlInstructionFactory = ::ControlInstructionFactory,
    memoryInstructionFactory = ::MemoryInstructionFactory,
    numericInstructionFactory = ::NumericInstructionFactory,
    parametricInstructionFactory = ::ParametricInstructionFactory,
    referenceInstructionFactory = ::ReferenceInstructionFactory,
    tableInstructionFactory = ::TableInstructionFactory,
    variableInstructionFactory = ::VariableInstructionFactory,
)

internal inline fun InstructionFactory(
    instruction: Instruction,
    aggregateInstructionFactory: IRFactory<AggregateInstruction, IRAggregateInstruction>,
    atomicInstructionFactory: IRFactory<AtomicMemoryInstruction, IRAtomicMemoryInstruction>,
    controlInstructionFactory: IRFactory<ControlInstruction, IRControlInstruction>,
    memoryInstructionFactory: IRFactory<MemoryInstruction, IRMemoryInstruction>,
    numericInstructionFactory: IRFactory<NumericInstruction, IRNumericInstruction>,
    parametricInstructionFactory: IRFactory<ParametricInstruction, IRParametricInstruction>,
    referenceInstructionFactory: IRFactory<ReferenceInstruction, IRReferenceInstruction>,
    tableInstructionFactory: IRFactory<TableInstruction, IRTableInstruction>,
    variableInstructionFactory: IRFactory<VariableInstruction, IRVariableInstruction>,
): IRInstruction {
    return when (instruction) {
        is AggregateInstruction -> aggregateInstructionFactory(instruction)
        is AtomicMemoryInstruction -> atomicInstructionFactory(instruction)
        is ControlInstruction -> controlInstructionFactory(instruction)
        is MemoryInstruction -> memoryInstructionFactory(instruction)
        is NumericInstruction -> numericInstructionFactory(instruction)
        is ParametricInstruction -> parametricInstructionFactory(instruction)
        is ReferenceInstruction -> referenceInstructionFactory(instruction)
        is TableInstruction -> tableInstructionFactory(instruction)
        is VariableInstruction -> variableInstructionFactory(instruction)
        is VectorInstruction -> error("VectorInstructions are not supported")
    }
}
