package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
const val OBJECT_TYPE = "Ljava/lang/Object;"

internal const val MAX_PARAMETERS = 100

private const val DEPTH = "I"
private const val ARGUMENTS = "[J"
private const val REFERENCE_ARGUMENTS = "[Ljava/lang/Object;"

@InternalChasmApi
fun descriptorWords(
    descriptor: String,
) = if (descriptor == "J" || descriptor == "D") 2 else 1

@InternalChasmApi
fun internalName(
    descriptor: String,
) = if (descriptor.startsWith("[")) descriptor else descriptor.removePrefix("L").removeSuffix(";")

@InternalChasmApi
fun binaryName(
    descriptor: String,
) = internalName(descriptor).replace('/', '.')

@InternalChasmApi
fun valueDescriptor(
    type: ValueType,
    reference: String = "J",
): String = when ((type as? ValueType.Number)?.numberType) {
    NumberType.I32 -> "I"
    NumberType.F32 -> "F"
    NumberType.F64 -> "D"
    NumberType.I64 -> "J"
    null -> if (type is ValueType.Reference) reference else "J"
}

@InternalChasmApi
fun returnDescriptor(
    type: FunctionType,
    reference: String = "J",
): String = when (type.results.types.size) {
    0 -> "V"
    1 -> valueDescriptor(type.results.types[0], reference)
    else -> "[J"
}

@InternalChasmApi
fun signatureKey(
    type: FunctionType,
    reference: String = "J",
): String = functionParameters(type, reference).joinToString("") + returnDescriptor(type, reference)

internal fun functionParameters(
    type: FunctionType,
    reference: String = "J",
): List<String> = parameters(type, leading = 0, reference)

@InternalChasmApi
fun takesArgumentArray(
    type: FunctionType,
): Boolean = type.params.types.size > MAX_PARAMETERS

@InternalChasmApi
fun takesReferenceArray(
    type: FunctionType,
    reference: String,
): Boolean = reference != "J" && takesArgumentArray(type) && type.params.types.any { parameter -> parameter is ValueType.Reference }

@InternalChasmApi
fun argumentWords(
    type: FunctionType,
    reference: String = "J",
): Int {

    if (takesArgumentArray(type)) {
        return if (takesReferenceArray(type, reference)) 2 else 1
    }

    val parameters = type.params.types
    var words = 0
    for (index in parameters.indices) {
        words += descriptorWords(valueDescriptor(parameters[index], reference))
    }
    return words
}

@InternalChasmApi
fun staticParameters(
    type: FunctionType,
    binding: String,
    reference: String = "J",
): List<String> = if (takesReferenceArray(type, reference)) {
    listOf(binding, CONTEXT_TYPE, DEPTH, ARGUMENTS, REFERENCE_ARGUMENTS)
} else if (takesArgumentArray(type)) {
    listOf(binding, CONTEXT_TYPE, DEPTH, ARGUMENTS)
} else {
    parameters(type, leading = 1, reference).also { parameters -> parameters[0] = binding }
}

private fun parameters(
    type: FunctionType,
    leading: Int,
    reference: String,
): MutableList<String> {
    val types = type.params.types
    val parameters = ArrayList<String>(leading + 2 + types.size)
    repeat(leading) { parameters.add("") }
    parameters.add(CONTEXT_TYPE)
    parameters.add(DEPTH)
    for (index in types.indices) parameters.add(valueDescriptor(types[index], reference))
    return parameters
}
