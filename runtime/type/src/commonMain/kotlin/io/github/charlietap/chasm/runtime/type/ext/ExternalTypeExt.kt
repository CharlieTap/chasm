package io.github.charlietap.chasm.runtime.type.ext

import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ResultType

fun ExternalType.clone(): ExternalType {
    return when (this) {
        is ExternalType.Function -> ExternalType.Function(functionType.clone())
        is ExternalType.Table -> ExternalType.Table(tableType.copy(limits = tableType.limits.copy()))
        is ExternalType.Memory -> ExternalType.Memory(memoryType.copy(limits = memoryType.limits.copy()))
        is ExternalType.Global -> ExternalType.Global(globalType.copy())
        is ExternalType.Tag -> ExternalType.Tag(tagType.copy(functionType = tagType.functionType.clone()))
    }
}

private fun FunctionType.clone(): FunctionType {
    return FunctionType(ResultType(params.types.toList()), ResultType(results.types.toList()))
}
