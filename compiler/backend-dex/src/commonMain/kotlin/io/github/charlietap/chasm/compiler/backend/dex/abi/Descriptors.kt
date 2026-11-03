package io.github.charlietap.chasm.compiler.backend.dex.abi

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.staticParameters
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.ReferenceType
import io.github.charlietap.dexter.ValueType as DexValueType

internal val MethodRef.method: DexMethod
    get() = DexMethod(referenceType(owner), name, dexType(returns), parameters.map(::valueType))

internal class DexMethods {
    private val methods = HashMap<MethodRef, DexMethod>()

    fun of(
        method: MethodRef,
    ): DexMethod = methods.getOrPut(method) { method.method }
}

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
