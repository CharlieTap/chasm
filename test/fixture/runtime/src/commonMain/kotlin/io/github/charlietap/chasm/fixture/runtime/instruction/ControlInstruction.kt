package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.type.RTT

fun controlRuntimeInstruction(): ControlInstruction = unreachableRuntimeInstruction()

fun unreachableRuntimeInstruction() = ControlInstruction.Unreachable

fun nopRuntimeInstruction() = ControlInstruction.Nop

fun returnRuntimeInstruction() = ControlInstruction.Return

fun returnWasmFunctionCallRuntimeInstruction(
    instance: FunctionInstance.WasmFunction = wasmFunctionInstance(),
) = ControlInstruction.ReturnWasmFunctionCall(
    plan = instance.callPlan,
)

fun returnCallRefRuntimeInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.ReturnCallRef(
    typeIndex = typeIndex,
)

fun wasmFunctionCallRuntimeInstruction(
    instance: FunctionInstance.WasmFunction = wasmFunctionInstance(),
) = ControlInstruction.WasmFunctionCall(
    plan = instance.callPlan,
)

fun callRefRuntimeInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.CallRef(
    typeIndex = typeIndex,
)

fun callIndirectRuntimeInstruction(
    type: RTT = rtt(),
    table: TableInstance = tableInstance(),
) = ControlInstruction.CallIndirect(
    type = type,
    table = table,
)

fun returnCallIndirectRuntimeInstruction(
    type: RTT = rtt(),
    table: TableInstance = tableInstance(),
) = ControlInstruction.ReturnCallIndirect(
    type = type,
    table = table,
)
