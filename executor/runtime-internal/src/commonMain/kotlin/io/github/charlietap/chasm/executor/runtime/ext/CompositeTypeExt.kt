package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType

inline fun CompositeType.functionType(): FunctionType = try {
    (this as CompositeType.Function).functionType
} catch (_: ClassCastException) {
    throw InvocationException(InvocationError.FunctionCompositeTypeExpected)
}

inline fun CompositeType.structType(): StructType = try {
    (this as CompositeType.Struct).structType
} catch (_: ClassCastException) {
    throw InvocationException(InvocationError.StructCompositeTypeExpected)
}

inline fun CompositeType.arrayType(): ArrayType = try {
    (this as CompositeType.Array).arrayType
} catch (_: ClassCastException) {
    throw InvocationException(InvocationError.ArrayCompositeTypeExpected)
}
