package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException

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
