package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.ir.type.ArrayType as IRArrayType
import io.github.charlietap.chasm.ir.type.CompositeType as IRCompositeType
import io.github.charlietap.chasm.ir.type.FunctionType as IRFunctionType
import io.github.charlietap.chasm.ir.type.StructType as IRStructType

internal fun CompositeTypeFactory(
    compositeType: CompositeType,
): IRCompositeType = CompositeTypeFactory(
    compositeType = compositeType,
    functionTypeFactory = ::FunctionTypeFactory,
    structTypeFactory = ::StructTypeFactory,
    arrayTypeFactory = ::ArrayTypeFactory,
)

internal inline fun CompositeTypeFactory(
    compositeType: CompositeType,
    functionTypeFactory: IRFactory<FunctionType, IRFunctionType>,
    structTypeFactory: IRFactory<StructType, IRStructType>,
    arrayTypeFactory: IRFactory<ArrayType, IRArrayType>,
): IRCompositeType {
    return when (compositeType) {
        is CompositeType.Function -> IRCompositeType.Function(
            functionType = functionTypeFactory(compositeType.functionType),
        )

        is CompositeType.Struct -> IRCompositeType.Struct(
            structType = structTypeFactory(compositeType.structType),
        )

        is CompositeType.Array -> IRCompositeType.Array(
            arrayType = arrayTypeFactory(compositeType.arrayType),
        )
    }
}
