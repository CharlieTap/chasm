package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

const val OBJECT_TYPE = "Ljava/lang/Object;"

const val MAX_PARAMETERS = 100

private const val DEPTH = "I"
private const val ARGUMENTS = "[J"

fun descriptorWords(
    descriptor: String,
) = if (descriptor == "J" || descriptor == "D") 2 else 1

fun internalName(
    descriptor: String,
) = if (descriptor.startsWith("[")) descriptor else descriptor.removePrefix("L").removeSuffix(";")

fun binaryName(
    descriptor: String,
) = internalName(descriptor).replace('/', '.')

fun valueDescriptor(
    type: ValueType,
): String = when ((type as? ValueType.Number)?.numberType) {
    NumberType.I32 -> "I"
    NumberType.F32 -> "F"
    NumberType.F64 -> "D"
    else -> "J"
}

fun returnDescriptor(
    type: FunctionType,
): String = when (type.results.types.size) {
    0 -> "V"
    1 -> valueDescriptor(type.results.types[0])
    else -> "[J"
}

fun signatureKey(
    type: FunctionType,
): String = functionParameters(type).joinToString("") + returnDescriptor(type)

fun functionParameters(
    type: FunctionType,
): List<String> = listOf(CONTEXT_TYPE, DEPTH) + type.params.types.map(::valueDescriptor)

fun takesArgumentArray(
    type: FunctionType,
): Boolean = type.params.types.size > MAX_PARAMETERS

fun staticParameters(
    type: FunctionType,
    binding: String,
): List<String> = if (takesArgumentArray(type)) {
    listOf(binding, CONTEXT_TYPE, DEPTH, ARGUMENTS)
} else {
    listOf(binding) + functionParameters(type)
}
