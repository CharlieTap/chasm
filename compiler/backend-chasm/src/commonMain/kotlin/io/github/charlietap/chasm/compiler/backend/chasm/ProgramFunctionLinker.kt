package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.compiler.backend.chasm.link.PendingWasmCallLinker
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.program.Program

internal typealias ProgramFunctionLinker = (LinkContext, CirFunction, Program) -> Unit

internal fun ProgramFunctionLinker(
    context: LinkContext,
    function: CirFunction,
    program: Program,
) = ProgramFunctionLinker(
    context = context,
    function = function,
    program = program,
    functionLinker = ::FunctionLinker,
    pendingWasmCallLinker = ::PendingWasmCallLinker,
)

internal inline fun ProgramFunctionLinker(
    context: LinkContext,
    function: CirFunction,
    program: Program,
    crossinline functionLinker: FunctionLinker,
    crossinline pendingWasmCallLinker: PendingWasmCallLinker,
) {
    val entryIp = program.size
    val output = arrayOfNulls<DispatchableInstruction>(context.programSize(function))
    val exceptions = functionLinker(context, function, entryIp, output, entryIp)
    for (call in context.pendingWasmCalls) output[call.ip - entryIp] = pendingWasmCallLinker(call)
    context.pendingWasmCalls.clear()
    @Suppress("UNCHECKED_CAST")
    program.append(output as Array<DispatchableInstruction>)
    exceptions?.let(program::registerExceptionTable)
}
