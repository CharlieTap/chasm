package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.referenceType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance

fun controlRuntimeInstruction(): ControlInstruction = unreachableRuntimeInstruction()

fun unreachableRuntimeInstruction() = ControlInstruction.Unreachable

fun nopRuntimeInstruction() = ControlInstruction.Nop

fun blockRuntimeInstruction(
    functionType: FunctionType = functionType(),
    instructions: Array<DispatchableInstruction> = emptyArray(),
) = ControlInstruction.Block(
    functionType = functionType,
    instructions = instructions,
)

fun loopRuntimeInstruction(
    functionType: FunctionType = functionType(),
    instructions: Array<DispatchableInstruction> = emptyArray(),
) = ControlInstruction.Loop(
    functionType = functionType,
    instructions = instructions,
)

fun ifRuntimeInstruction(
    functionType: FunctionType = functionType(),
    thenInstructions: Array<DispatchableInstruction> = emptyArray(),
    elseInstructions: Array<DispatchableInstruction>? = null,
) = ControlInstruction.If(
    functionType = functionType,
    thenInstructions = thenInstructions,
    elseInstructions = elseInstructions,
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
