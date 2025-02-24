package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ReferenceType

fun controlRuntimeInstruction(): ControlInstruction = unreachableRuntimeInstruction()

fun unreachableRuntimeInstruction() = ControlInstruction.Unreachable

fun nopRuntimeInstruction() = ControlInstruction.Nop

fun blockRuntimeInstruction(
    params: Int = 0,
    results: Int = 0,
    instructions: Array<DispatchableInstruction> = emptyArray(),
) = ControlInstruction.Block(
    params = params,
    results = results,
    instructions = instructions,
)

fun loopRuntimeInstruction(
    params: Int = 0,
    instructions: Array<DispatchableInstruction> = emptyArray(),
) = ControlInstruction.Loop(
    params = params,
    instructions = instructions,
)

fun ifRuntimeInstruction(
    params: Int = 0,
    results: Int = 0,
    instructions: Array<Array<DispatchableInstruction>> = emptyArray(),
) = ControlInstruction.If(
    params = params,
    results = results,
    instructions = instructions,
)

fun brRuntimeInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.Br(
    labelIndex = labelIndex,
)

fun brIfRuntimeInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrIf(
    labelIndex = labelIndex,
)

fun brTableRuntimeInstruction(
    labelIndices: List<Index.LabelIndex> = emptyList(),
    defaultLabelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrTable(
    labelIndices = labelIndices,
    defaultLabelIndex = defaultLabelIndex,
)

fun brOnNullRuntimeInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrOnNull(
    labelIndex = labelIndex,
)

fun brOnNonNullRuntimeInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrOnNonNull(
    labelIndex = labelIndex,
)

fun brOnCastRuntimeInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = ControlInstruction.BrOnCast(
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)

fun brOnCastFailRuntimeInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = ControlInstruction.BrOnCastFail(
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)

fun returnRuntimeInstruction() = ControlInstruction.Return

fun returnWasmFunctionCallRuntimeInstruction(
    instance: FunctionInstance.WasmFunction = wasmFunctionInstance(),
) = ControlInstruction.ReturnWasmFunctionCall(
    instance = instance,
)

fun returnCallRefRuntimeInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.ReturnCallRef(
    typeIndex = typeIndex,
)

fun wasmFunctionCallRuntimeInstruction(
    instance: FunctionInstance.WasmFunction = wasmFunctionInstance(),
) = ControlInstruction.WasmFunctionCall(
    instance = instance,
)

fun callRefRuntimeInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.CallRef(
    typeIndex = typeIndex,
)

fun callIndirectRuntimeInstruction(
    type: DefinedType = definedType(),
    table: TableInstance = tableInstance(),
) = ControlInstruction.CallIndirect(
    type = type,
    table = table,
)

fun returnCallIndirectRuntimeInstruction(
    type: DefinedType = definedType(),
    table: TableInstance = tableInstance(),
) = ControlInstruction.ReturnCallIndirect(
    type = type,
    table = table,
)
