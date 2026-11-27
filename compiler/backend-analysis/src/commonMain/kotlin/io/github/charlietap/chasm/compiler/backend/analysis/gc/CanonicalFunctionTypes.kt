package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

class CanonicalFunctionTypes(
    private val rttKeys: RttKeys,
) {
    fun key(type: FunctionType): String {
        return buildString {
            type.params.types.forEach { parameter -> append(valueKey(parameter)).append(',') }
            append('>')
            type.results.types.forEach { result -> append(valueKey(result)).append(',') }
        }
    }

    private fun valueKey(type: ValueType): String {
        return when (type) {
            is ValueType.Reference -> referenceKey(type.referenceType)
            else -> type.toString()
        }
    }

    private fun referenceKey(type: ReferenceType): String {

        val nullability = if (type is ReferenceType.RefNull) "null " else ""
        val heapKey = when (val heapType = type.heapType) {
            is ConcreteHeapType.Defined -> rttKeys.key(heapType.definedType)
            else -> heapType.toString()
        }

        return nullability + heapKey
    }
}
