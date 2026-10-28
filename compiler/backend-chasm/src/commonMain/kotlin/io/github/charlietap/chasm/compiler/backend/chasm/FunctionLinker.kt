package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.chasm.link.CheckpointLinker
import io.github.charlietap.chasm.compiler.backend.chasm.link.ExceptionTableLinker
import io.github.charlietap.chasm.compiler.backend.chasm.link.InstructionLinker
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable

internal typealias FunctionLinker = (LinkContext, CirFunction, Int, Array<DispatchableInstruction?>, Int) -> FunctionExceptionTable?

internal fun FunctionLinker(
    context: LinkContext,
    function: CirFunction,
    entryIp: Int,
    output: Array<DispatchableInstruction?>,
    outputBaseIp: Int,
): FunctionExceptionTable? = FunctionLinker(
    context = context,
    function = function,
    entryIp = entryIp,
    output = output,
    outputBaseIp = outputBaseIp,
    instructionLinker = ::InstructionLinker,
    checkpointLinker = ::CheckpointLinker,
    exceptionTableLinker = ::ExceptionTableLinker,
)

internal inline fun FunctionLinker(
    context: LinkContext,
    function: CirFunction,
    entryIp: Int,
    output: Array<DispatchableInstruction?>,
    outputBaseIp: Int,
    crossinline instructionLinker: InstructionLinker,
    crossinline checkpointLinker: CheckpointLinker,
    crossinline exceptionTableLinker: ExceptionTableLinker,
): FunctionExceptionTable? {
    val instructions = function.instructions
    context.beginFunction(entryIp, function)
    var ip = entryIp
    for (index in instructions.indices) {
        val instruction = instructions[index]
        val dispatchable = if (instruction === AdminInstruction.Checkpoint) {
            checkpointLinker(context) ?: continue
        } else {
            context.nextIp = ip
            instructionLinker(instruction, instruction.opcode, context)
        }
        output[ip - outputBaseIp] = dispatchable
        ip++
    }
    val instructionCount = ip - entryIp
    val exceptions = function.exceptions ?: return null
    val functionType = context.functions[function.index.index].functionType
    return exceptionTableLinker(
        exceptions,
        context,
        instructionCount,
        function.frame.activationHeaderSlot,
        functionType.results.types.size,
    )
}
