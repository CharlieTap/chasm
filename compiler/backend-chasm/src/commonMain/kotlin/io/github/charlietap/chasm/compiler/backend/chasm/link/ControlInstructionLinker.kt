package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.chasm.ext.toRuntime
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallIndirectIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallIndirectSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallRefSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.FunctionReturnDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.HostCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallIndirectIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallIndirectSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallRefSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnHostCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.UnreachableDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance

internal fun linkControlInstruction(
    instruction: ControlInstruction,
    context: LinkContext,
): DispatchableInstruction = when (instruction) {
    is ControlInstruction.Call -> linkCall(instruction, context)
    is ControlInstruction.FunctionReturn -> FunctionReturnDispatcher(
        instruction.results.toRuntime(),
        instruction.activationHeaderSlot,
    )
    is ControlInstruction.Unreachable -> UnreachableDispatcher()
    is ControlInstruction.CallIndirectS -> CallIndirectSDispatcher(
        instruction.elementIndexSlot,
        instruction.operands.toRuntime(),
        context.runtimeTypes[instruction.type.index],
        context.tables[instruction.table.index],
        context.instance,
        instruction.callFrameOffset,
        context.nextIp,
        instruction.resultDestinationSlot,
    )
    is ControlInstruction.CallIndirectI -> CallIndirectIDispatcher(
        instruction.elementIndex,
        instruction.operands.toRuntime(),
        context.runtimeTypes[instruction.type.index],
        context.tables[instruction.table.index],
        context.instance,
        instruction.callFrameOffset,
        context.nextIp,
        instruction.resultDestinationSlot,
    )
    is ControlInstruction.CallRefS -> CallRefSDispatcher(
        instruction.functionSlot,
        instruction.operands.toRuntime(),
        context.instance,
        instruction.callFrameOffset,
        context.nextIp,
        instruction.resultDestinationSlot,
    )
    is ControlInstruction.ReturnCall -> linkReturnCall(instruction, context)
    is ControlInstruction.ReturnCallIndirectS -> ReturnCallIndirectSDispatcher(
        instruction.elementIndexSlot,
        instruction.operands.toRuntime(),
        context.runtimeTypes[instruction.type.index],
        context.tables[instruction.table.index],
        context.instance,
        instruction.callFrameOffset,
        instruction.callerActivationHeaderSlot,
    )
    is ControlInstruction.ReturnCallIndirectI -> ReturnCallIndirectIDispatcher(
        instruction.elementIndex,
        instruction.operands.toRuntime(),
        context.runtimeTypes[instruction.type.index],
        context.tables[instruction.table.index],
        context.instance,
        instruction.callFrameOffset,
        instruction.callerActivationHeaderSlot,
    )
    is ControlInstruction.ReturnCallRefS -> ReturnCallRefSDispatcher(
        instruction.functionSlot,
        instruction.operands.toRuntime(),
        context.instance,
        instruction.callFrameOffset,
        instruction.callerActivationHeaderSlot,
    )
    is ControlInstruction.Throw -> ThrowDispatcher(
        context.instance.tagAddresses[instruction.exceptionTag.index],
        instruction.firstPayloadSlot,
    )
    is ControlInstruction.ThrowRefS -> ThrowRefSDispatcher(instruction.exceptionSlot)
}

private fun linkCall(
    instruction: ControlInstruction.Call,
    context: LinkContext,
): DispatchableInstruction = when (val function = context.functions[instruction.function.index]) {
    is FunctionInstance.WasmFunction -> {
        checkWasmCallFrame(instruction.callFrameOffset)
        context.deferWasmCall(
            PendingWasmCall.Call(
                ip = context.nextIp,
                callee = function,
                operands = instruction.operands.toRuntime(),
                callFrameOffset = instruction.callFrameOffset,
                resultDestinationSlot = instruction.resultDestinationSlot,
            ),
        )
    }
    is FunctionInstance.HostFunction -> HostCallDispatcher(
        function,
        context.instance,
        instruction.operands.toRuntime(),
        instruction.callFrameOffset,
        instruction.resultDestinationSlot,
    )
}

private fun linkReturnCall(
    instruction: ControlInstruction.ReturnCall,
    context: LinkContext,
): DispatchableInstruction = when (val function = context.functions[instruction.function.index]) {
    is FunctionInstance.WasmFunction -> context.deferWasmCall(
        PendingWasmCall.ReturnCall(
            ip = context.nextIp,
            callee = function,
            operands = instruction.operands.wasm.toRuntime(),
            callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
        ),
    )
    is FunctionInstance.HostFunction -> {
        val parameterCount = function.functionType.params.types.size
        context.frameSlots = maxOf(context.frameSlots, instruction.callFrameOffset + parameterCount + 1)
        ReturnHostCallDispatcher(
            function,
            context.instance,
            instruction.operands.host.toRuntime(),
            instruction.callFrameOffset,
            instruction.callerActivationHeaderSlot,
        )
    }
}

private fun checkWasmCallFrame(
    callFrameOffset: Int,
) {
    check(callFrameOffset in 0 until CALLER_FRAME_DELTA_LIMIT) {
        "Wasm caller-frame displacement exceeds the activation-header representation"
    }
}

private const val CALLER_FRAME_DELTA_LIMIT = 1 shl 25
