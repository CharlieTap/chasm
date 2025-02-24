package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType

inline fun CompositeType.functionType(): FunctionType? {
    return (this as? CompositeType.Function)?.functionType
}

inline fun CompositeType.structType(): StructType? {
    return (this as? CompositeType.Struct)?.structType
}

inline fun CompositeType.arrayType(): ArrayType? {
    return (this as? CompositeType.Array)?.arrayType
}
