package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperArgument
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperCall
import io.github.charlietap.chasm.type.NumberType

internal fun LowerContext.lowerHelperCall(
    call: HelperCall,
) {
    for (argument in call.arguments) {
        when (argument) {
            HelperArgument.Context -> pushContext()
            is HelperArgument.Object -> pushResource(argument.resource)
            is HelperArgument.RuntimeType -> pushResource(Resource.RuntimeType(argument.type.index))
            is HelperArgument.I32 -> push(NumberType.I32, argument.operand)
            is HelperArgument.Encoded -> pushLong(argument.operand)
        }
    }
    emit.invokeStatic(call.method)
    val destination = call.destination
    if (destination != null) storeLong(destination)
    if (call.growsMemory) refreshMemory()
}
