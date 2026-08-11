package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCallWithoutLocals
import io.github.charlietap.chasm.executor.invoker.function.copyOperands
import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.CallExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.ReturnCallExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.ThrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.ThrowRefExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.CopyOperand

fun CallDispatcher(
    instruction: ControlSuperInstruction.WasmCall,
): DispatchableInstruction {
    val plan = instruction.plan
    val operands = instruction.operands
    val resultSlotBase = instruction.resultSlotBase
    val callFrameSlot = instruction.callFrameSlot
    return if (plan.locals.isEmpty()) {
        DispatchableInstruction { vstack, cstack, _, _, nextIp ->
            WasmFunctionCallWithoutLocals(vstack, cstack, plan, operands, resultSlotBase, callFrameSlot, nextIp)
        }
    } else {
        DispatchableInstruction { vstack, cstack, _, _, nextIp ->
            WasmFunctionCall(vstack, cstack, plan, operands, resultSlotBase, callFrameSlot, nextIp)
        }
    }
}

fun CallDispatcher(
    instruction: ControlSuperInstruction.HostCall,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, nextIp ->
    CallExecutor(vstack, cstack, store, context, instruction, nextIp)
}

fun CallDispatcher(
    instruction: ControlSuperInstruction.CallIndirectI,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, nextIp ->
    CallExecutor(vstack, cstack, store, context, instruction, nextIp)
}

fun CallDispatcher(
    instruction: ControlSuperInstruction.CallIndirectS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, nextIp ->
    CallExecutor(vstack, cstack, store, context, instruction, nextIp)
}

fun CallDispatcher(
    instruction: ControlSuperInstruction.CallRefS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, nextIp ->
    CallExecutor(vstack, cstack, store, context, instruction, nextIp)
}

fun ReturnCallDispatcher(
    instruction: ControlSuperInstruction.ReturnWasmCall,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnCallExecutor(vstack, cstack, store, context, instruction)
}

fun FunctionReturnDispatcher(
    instruction: ControlSuperInstruction.FunctionReturn,
): DispatchableInstruction {
    val results = instruction.results
    val result = results.operands.singleOrNull()
    return when (result) {
        is CopyOperand.Immediate -> {
            val value = result.value
            DispatchableInstruction { vstack, cstack, _, _, _ ->
                vstack.setFrameSlot(0, value)
                ReturnExecutor(vstack, cstack)
            }
        }
        is CopyOperand.Slot -> {
            val sourceSlot = result.slot
            DispatchableInstruction { vstack, cstack, _, _, _ ->
                vstack.setFrameSlot(0, vstack.getFrameSlot(sourceSlot))
                ReturnExecutor(vstack, cstack)
            }
        }
        null -> DispatchableInstruction { vstack, cstack, _, _, _ ->
            val framePointer = vstack.framePointer
            copyOperands(
                vstack = vstack,
                currentFramePointer = framePointer,
                destinationFramePointer = framePointer,
                operands = results.operands,
                order = results.order,
            )
            ReturnExecutor(vstack, cstack)
        }
    }
}

fun ReturnCallDispatcher(
    instruction: ControlSuperInstruction.ReturnHostCall,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnCallExecutor(vstack, cstack, store, context, instruction)
}

fun ReturnCallDispatcher(
    instruction: ControlSuperInstruction.ReturnCallIndirectI,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnCallExecutor(vstack, cstack, store, context, instruction)
}

fun ReturnCallDispatcher(
    instruction: ControlSuperInstruction.ReturnCallIndirectS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnCallExecutor(vstack, cstack, store, context, instruction)
}

fun ReturnCallDispatcher(
    instruction: ControlSuperInstruction.ReturnCallRefS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnCallExecutor(vstack, cstack, store, context, instruction)
}

fun ThrowDispatcher(
    instruction: ControlSuperInstruction.Throw,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, _, _ ->
    ThrowExecutor(vstack, cstack, store, instruction)
}

fun ThrowRefDispatcher(
    instruction: ControlSuperInstruction.ThrowRefS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, _, _ ->
    ThrowRefExecutor(vstack, cstack, store, instruction)
}
