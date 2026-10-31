package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction

/** Whether [this] allocates a guest object, the only way code starts a collection without a call. */
fun Instruction.allocatesGuestObject(): Boolean = when (this) {
    is AggregateInstruction -> allocatesGuestObject()
    is ControlInstruction -> allocatesGuestObject()
    is AdminInstruction,
    is MemoryInstruction,
    is NumericInstruction,
    is ParametricInstruction,
    is ReferenceInstruction,
    is TableInstruction,
    is VariableInstruction,
    -> false
}

/**
 * Whether a collection may run while the frame executing [this] is live. Tail calls never do: the frame ends before
 * their callee runs.
 */
fun Instruction.mayCollectWhileFrameLive(
    mayCollect: BooleanArray,
): Boolean = when (this) {
    is ControlInstruction.Call -> mayCollect[function.index]
    is ControlInstruction.CallIndirectI,
    is ControlInstruction.CallIndirectS,
    is ControlInstruction.CallRefS,
    -> true
    else -> allocatesGuestObject()
}

private fun ControlInstruction.allocatesGuestObject(): Boolean = when (this) {
    is ControlInstruction.Throw -> true
    is ControlInstruction.Call,
    is ControlInstruction.CallIndirectI,
    is ControlInstruction.CallIndirectS,
    is ControlInstruction.CallRefS,
    is ControlInstruction.FunctionReturn,
    is ControlInstruction.ReturnCall,
    is ControlInstruction.ReturnCallIndirectI,
    is ControlInstruction.ReturnCallIndirectS,
    is ControlInstruction.ReturnCallRefS,
    is ControlInstruction.ThrowRefS,
    is ControlInstruction.Unreachable,
    -> false
}

private fun AggregateInstruction.allocatesGuestObject(): Boolean = when (this) {
    is AggregateInstruction.ArrayNewDataIi,
    is AggregateInstruction.ArrayNewDataIs,
    is AggregateInstruction.ArrayNewDataSi,
    is AggregateInstruction.ArrayNewDataSs,
    is AggregateInstruction.ArrayNewDefaultI,
    is AggregateInstruction.ArrayNewDefaultS,
    is AggregateInstruction.ArrayNewElementIi,
    is AggregateInstruction.ArrayNewElementIs,
    is AggregateInstruction.ArrayNewElementSi,
    is AggregateInstruction.ArrayNewElementSs,
    is AggregateInstruction.ArrayNewFixedS,
    is AggregateInstruction.ArrayNewIi,
    is AggregateInstruction.ArrayNewIs,
    is AggregateInstruction.ArrayNewSi,
    is AggregateInstruction.ArrayNewSs,
    is AggregateInstruction.StructNewDefaultS,
    is AggregateInstruction.StructNewS,
    -> true
    is AggregateInstruction.AnyConvertExternS,
    is AggregateInstruction.ArrayCopyIii,
    is AggregateInstruction.ArrayCopyIis,
    is AggregateInstruction.ArrayCopyIsi,
    is AggregateInstruction.ArrayCopyIss,
    is AggregateInstruction.ArrayCopySii,
    is AggregateInstruction.ArrayCopySis,
    is AggregateInstruction.ArrayCopySsi,
    is AggregateInstruction.ArrayCopySss,
    is AggregateInstruction.ArrayFillIii,
    is AggregateInstruction.ArrayFillIis,
    is AggregateInstruction.ArrayFillIsi,
    is AggregateInstruction.ArrayFillIss,
    is AggregateInstruction.ArrayFillSii,
    is AggregateInstruction.ArrayFillSis,
    is AggregateInstruction.ArrayFillSsi,
    is AggregateInstruction.ArrayFillSss,
    is AggregateInstruction.ArrayGetI,
    is AggregateInstruction.ArrayGetS,
    is AggregateInstruction.ArrayGetSignedI,
    is AggregateInstruction.ArrayGetSignedS,
    is AggregateInstruction.ArrayGetUnsignedI,
    is AggregateInstruction.ArrayGetUnsignedS,
    is AggregateInstruction.ArrayInitDataIii,
    is AggregateInstruction.ArrayInitDataIis,
    is AggregateInstruction.ArrayInitDataIsi,
    is AggregateInstruction.ArrayInitDataIss,
    is AggregateInstruction.ArrayInitDataSii,
    is AggregateInstruction.ArrayInitDataSis,
    is AggregateInstruction.ArrayInitDataSsi,
    is AggregateInstruction.ArrayInitDataSss,
    is AggregateInstruction.ArrayInitElementIii,
    is AggregateInstruction.ArrayInitElementIis,
    is AggregateInstruction.ArrayInitElementIsi,
    is AggregateInstruction.ArrayInitElementIss,
    is AggregateInstruction.ArrayInitElementSii,
    is AggregateInstruction.ArrayInitElementSis,
    is AggregateInstruction.ArrayInitElementSsi,
    is AggregateInstruction.ArrayInitElementSss,
    is AggregateInstruction.ArrayLenS,
    is AggregateInstruction.ArraySetIi,
    is AggregateInstruction.ArraySetIs,
    is AggregateInstruction.ArraySetSi,
    is AggregateInstruction.ArraySetSs,
    is AggregateInstruction.ExternConvertAnyS,
    is AggregateInstruction.I31GetSignedS,
    is AggregateInstruction.I31GetUnsignedS,
    is AggregateInstruction.LocalSetStructGetS,
    is AggregateInstruction.RefCastStructGetS,
    is AggregateInstruction.RefI31I,
    is AggregateInstruction.RefI31S,
    is AggregateInstruction.StructGetS,
    is AggregateInstruction.StructGetSignedS,
    is AggregateInstruction.StructGetStructGetS,
    is AggregateInstruction.StructGetUnsignedS,
    is AggregateInstruction.StructSetI,
    is AggregateInstruction.StructSetS,
    -> false
}
