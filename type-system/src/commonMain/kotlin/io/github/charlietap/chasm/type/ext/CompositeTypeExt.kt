@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType

inline fun CompositeType.functionType(): FunctionType? {
    return (this as? CompositeType.Function)?.functionType
}

inline fun CompositeType.structType(): StructType? {
    return (this as? CompositeType.Struct)?.structType
}

inline fun CompositeType.arrayType(): ArrayType? {
    return (this as? CompositeType.Array)?.arrayType
}
