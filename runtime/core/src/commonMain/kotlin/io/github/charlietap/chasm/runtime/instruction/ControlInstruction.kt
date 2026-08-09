package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.RTT
import kotlin.jvm.JvmInline

sealed interface ControlInstruction : LinkedInstruction {

    data object Unreachable : ControlInstruction

    data object Nop : ControlInstruction

    @JvmInline
    value class Throw(val tagIndex: Index.TagIndex) : ControlInstruction

    data object ThrowRef : ControlInstruction

    data object Return : ControlInstruction

    @JvmInline
    value class ReturnWasmFunctionCall(val instance: FunctionInstance.WasmFunction) : ControlInstruction

    @JvmInline
    value class ReturnHostFunctionCall(val instance: FunctionInstance.HostFunction) : ControlInstruction

    @JvmInline
    value class ReturnCallRef(val typeIndex: Index.TypeIndex) : ControlInstruction

    @JvmInline
    value class WasmFunctionCall(val instance: FunctionInstance.WasmFunction) : ControlInstruction

    @JvmInline
    value class HostFunctionCall(val instance: FunctionInstance.HostFunction) : ControlInstruction

    @JvmInline
    value class CallRef(val typeIndex: Index.TypeIndex) : ControlInstruction

    data class CallIndirect(
        val type: RTT,
        val table: TableInstance,
    ) : ControlInstruction

    data class ReturnCallIndirect(
        val type: RTT,
        val table: TableInstance,
    ) : ControlInstruction
}
