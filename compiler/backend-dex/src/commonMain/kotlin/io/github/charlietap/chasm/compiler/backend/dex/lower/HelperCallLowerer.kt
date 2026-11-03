package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperArgument
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperCall

internal fun LowerContext.lowerHelperCall(
    call: HelperCall,
) {
    val window = registers.window + 1
    var word = 0
    for (argument in call.arguments) {
        when (argument) {
            HelperArgument.Context -> emit.moveObject(window + word, Registers.CONTEXT)
            is HelperArgument.Object -> {
                loadObject(Registers.W1, argument.resource, argument.type)
                emit.moveObject(window + word, Registers.W1)
            }
            is HelperArgument.RuntimeType -> {
                loadValueInt(Registers.I0, Resource.RuntimeType(argument.type.index))
                emit.move(window + word, Registers.I0)
            }
            is HelperArgument.I32 -> {
                loadInt(Registers.I0, Registers.W0, argument.operand)
                emit.move(window + word, Registers.I0)
            }
            is HelperArgument.Encoded -> {
                loadWide(Registers.W0, argument.operand)
                emit.moveWide(window + word, Registers.W0)
            }
        }
        word += argument.words
    }
    emit.invokeRange(call.method, window, word)
    val destination = call.destination
    val target = if (destination == null) Registers.W0 else wideTarget(destination)
    if (destination != null) emit.moveResultWide(target)
    restoreWindow()
    if (destination != null) finishWide(destination, target)
    if (call.growsMemory) refreshMemory()
}
