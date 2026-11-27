package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

enum class ArgumentPassing {
    Direct,
    ValueArray,
    ValueAndReferenceArrays,
}

data class EntryShape(
    val passing: ArgumentPassing,
    val arguments: List<String>,
    val returns: String,
    val referenceResults: List<Boolean>,
) {
    constructor(
        type: FunctionType,
        reference: String,
    ) : this(
        passing = argumentPassing(type, reference),
        arguments = staticParameters(type, "", reference).drop(LEADING_PARAMETERS),
        returns = returnDescriptor(type, reference),
        referenceResults = referenceResults(type),
    )

    fun typedCall(owner: String): MethodRef {
        return MethodRef(owner, TYPED_CALL, listOf(CONTEXT_TYPE, "I") + arguments, returns)
    }
}

const val TYPED_CALL = "typed"

fun typedCall(
    owner: String,
    type: FunctionType,
    reference: String,
): MethodRef {
    return MethodRef(owner, TYPED_CALL, staticParameters(type, "", reference).drop(1), returnDescriptor(type, reference))
}

private const val LEADING_PARAMETERS = 3

private fun argumentPassing(
    type: FunctionType,
    reference: String,
): ArgumentPassing {
    return when {
        takesReferenceArray(type, reference) -> ArgumentPassing.ValueAndReferenceArrays
        takesArgumentArray(type) -> ArgumentPassing.ValueArray
        else -> ArgumentPassing.Direct
    }
}

private fun referenceResults(type: FunctionType): List<Boolean> {

    val results = type.results.types
    if (results.size < 2) {
        return emptyList()
    }

    return results.map { result -> result is ValueType.Reference }
}
