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
            is HelperArgument.I32 -> loadArgument(window + word, argument.operand, "I")
            is HelperArgument.Encoded -> loadArgument(window + word, argument.operand, "J")
        }
        word += argument.words
    }
    emit.invokeRange(checkNotNull(call.method) { "the helper form has no runtime call" }, window, word)

    val destination = call.destination
    if (destination == null) {
        restoreWindow()
    } else {
        writeWide(destination) { target ->
            emit.moveResultWide(target)
            restoreWindow()
        }
    }

    if (call.growsMemory) refreshMemory()
}
