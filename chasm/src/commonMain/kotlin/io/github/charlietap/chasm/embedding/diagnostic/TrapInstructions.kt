package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction

/** Used to pick which instruction of a fused compiled instruction trapped. */
internal fun Instruction.canTrap(): Boolean = when (this) {
    ControlInstruction.Unreachable,
    is ControlInstruction.Call,
    is ControlInstruction.CallIndirect,
    is ControlInstruction.CallRef,
    is ControlInstruction.ReturnCall,
    is ControlInstruction.ReturnCallIndirect,
    is ControlInstruction.ReturnCallRef,
    is ControlInstruction.Throw,
    ControlInstruction.ThrowRef,
    -> true

    is MemoryInstruction.Load,
    is MemoryInstruction.Store,
    is MemoryInstruction.MemoryCopy,
    is MemoryInstruction.MemoryFill,
    is MemoryInstruction.MemoryInit,
    -> true

    is TableInstruction.TableGet,
    is TableInstruction.TableSet,
    is TableInstruction.TableCopy,
    is TableInstruction.TableFill,
    is TableInstruction.TableInit,
    -> true

    ReferenceInstruction.RefAsNonNull,
    is ReferenceInstruction.RefCast,
    -> true

    is AggregateInstruction -> true

    NumericInstruction.I32DivS,
    NumericInstruction.I32DivU,
    NumericInstruction.I32RemS,
    NumericInstruction.I32RemU,
    NumericInstruction.I64DivS,
    NumericInstruction.I64DivU,
    NumericInstruction.I64RemS,
    NumericInstruction.I64RemU,
    NumericInstruction.I32TruncF32S,
    NumericInstruction.I32TruncF32U,
    NumericInstruction.I32TruncF64S,
    NumericInstruction.I32TruncF64U,
    NumericInstruction.I64TruncF32S,
    NumericInstruction.I64TruncF32U,
    NumericInstruction.I64TruncF64S,
    NumericInstruction.I64TruncF64U,
    -> true

    else -> false
}
