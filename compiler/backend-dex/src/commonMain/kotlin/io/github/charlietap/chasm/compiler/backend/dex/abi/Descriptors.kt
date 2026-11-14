package io.github.charlietap.chasm.compiler.backend.dex.abi

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.argumentWords
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.signatureKey
import io.github.charlietap.chasm.compiler.backend.abi.staticParameters
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.ReferenceType
import io.github.charlietap.dexter.ValueType as DexValueType

internal val MethodRef.method: DexMethod
    get() = DexMethod(referenceType(owner), name, dexType(returns), parameters.map(::valueType))

internal data class Invocation(
    val method: DexMethod,
    val words: Int,
    val offsets: IntArray,
)

internal class FunctionSignature(
    type: FunctionType,
) {
    val parameterDescriptors: List<String> = type.params.types.map(::valueDescriptor)

    val parameterOffsets: IntArray = descriptorOffsets(parameterDescriptors)

    val argumentArray: Boolean = takesArgumentArray(type)

    val resultDescriptor: String = returnDescriptor(type)

    val key: String = signatureKey(type)

    val argumentWords: Int = argumentWords(type)
}

private fun descriptorOffsets(descriptors: List<String>): IntArray {

    val offsets = IntArray(descriptors.size)
    var word = 0
    descriptors.forEachIndexed { index, descriptor ->
        offsets[index] = word
        word += descriptorWords(descriptor)
    }

    return offsets
}

internal class DexReferences {
    private val methods = HashMap<MethodRef, Invocation>()
    private val fields = HashMap<String, HashMap<String, CachedField>>()
    private val types = HashMap<String, ReferenceType>()

    fun of(method: MethodRef): DexMethod {
        return invocation(method).method
    }

    fun invocation(method: MethodRef): Invocation {
        return methods.getOrPut(method) {
            val dexMethod = DexMethod(reference(method.owner), method.name, returnType(method.returns), method.parameters.map(::parameterType))
            Invocation(dexMethod, method.words, descriptorOffsets(method.parameters))
        }
    }

    private fun returnType(descriptor: String): DexType {
        return if (descriptor.length == 1) {
            dexType(descriptor)
        } else {
            reference(descriptor)
        }
    }

    private fun parameterType(descriptor: String): DexValueType {
        return if (descriptor.length == 1) {
            valueType(descriptor)
        } else {
            reference(descriptor)
        }
    }

    fun field(
        owner: String,
        name: String,
        type: String,
    ): DexField {

        val cached = fields.getOrPut(owner, ::HashMap).getOrPut(name) { CachedField(type, DexField(ClassType(owner), name, valueType(type))) }
        check(cached.type == type) { "field $owner.$name is ${cached.type}, not $type" }

        return cached.field
    }

    fun reference(descriptor: String): ReferenceType {
        return types.getOrPut(descriptor) { referenceType(descriptor) }
    }
}

private data class CachedField(
    val type: String,
    val field: DexField,
)

internal fun dexType(
    descriptor: String,
): DexType = if (descriptor == "V") DexType.Void else valueType(descriptor)

internal fun valueType(
    descriptor: String,
): DexValueType = when (descriptor) {
    "Z" -> DexType.Boolean
    "B" -> DexType.Byte
    "S" -> DexType.Short
    "C" -> DexType.Char
    "I" -> DexType.Int
    "J" -> DexType.Long
    "F" -> DexType.Float
    "D" -> DexType.Double
    else -> referenceType(descriptor)
}

internal fun referenceType(
    descriptor: String,
): ReferenceType =
    if (descriptor.startsWith("[")) valueType(descriptor.substring(1)).array() else ClassType(descriptor)

internal const val BINDING_TYPE = "[Ljava/lang/Object;"

internal fun staticParameters(
    type: FunctionType,
): List<String> = staticParameters(type, BINDING_TYPE)

internal fun typedCall(
    signature: String,
    type: FunctionType,
): MethodRef = MethodRef(signature, TYPED_CALL, staticParameters(type).drop(1), returnDescriptor(type))
