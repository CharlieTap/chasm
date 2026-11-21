package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT

internal val Type.isReference: Boolean
    get() {
        return this is StructViewType || this is ArrayReferenceType || this is AnyReferenceType
    }

internal fun wordToValue(
    word: CodeBlock,
    type: Type,
): CodeBlock {
    return when (type) {
        Scalar.Integer -> CodeBlock.of("%L.toInt()", word)
        Scalar.Long -> word
        Scalar.Float -> CodeBlock.of("%T.fromBits(%L.toInt())", FLOAT, word)
        Scalar.Double -> CodeBlock.of("%T.fromBits(%L)", DOUBLE, word)
        else -> error("Cannot read $type from a word")
    }
}

internal fun valueToWord(
    value: CodeBlock,
    type: Type,
): CodeBlock {
    return when (type) {
        Scalar.Integer -> CodeBlock.of("%L.toLong()", value)
        Scalar.Long -> value
        Scalar.Float -> CodeBlock.of("%L.toRawBits().toLong()", value)
        Scalar.Double -> CodeBlock.of("%L.toRawBits()", value)
        else -> error("Cannot write $type to a word")
    }
}

internal fun referenceToValue(
    reference: CodeBlock,
    type: Type,
): CodeBlock {
    return when (type) {
        is StructViewType -> if (type.nullable) {
            CodeBlock.of("(%L as %T?)?.let { structReference -> %T(structReference) }", reference, STRUCT_REFERENCE_CLASS_NAME, type.className)
        } else {
            CodeBlock.of("%T(%L as %T)", type.className, reference, STRUCT_REFERENCE_CLASS_NAME)
        }
        is ArrayReferenceType -> CodeBlock.of("%L as %T", reference, ARRAY_REFERENCE_CLASS_NAME.copy(nullable = type.nullable))
        is AnyReferenceType -> if (type.nullable) {
            reference
        } else {
            CodeBlock.of("%L as %T", reference, ANY)
        }
        else -> error("Cannot read $type from a reference")
    }
}

internal fun valueToReference(
    value: CodeBlock,
    type: Type,
): CodeBlock {
    return when (type) {
        is StructViewType -> if (type.nullable) {
            CodeBlock.of("%L?.reference", value)
        } else {
            CodeBlock.of("%L.reference", value)
        }
        is ArrayReferenceType,
        is AnyReferenceType,
        -> value
        else -> error("Cannot write $type to a reference")
    }
}
