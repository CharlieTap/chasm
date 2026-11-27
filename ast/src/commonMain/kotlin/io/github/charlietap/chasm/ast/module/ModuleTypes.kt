package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

@InternalChasmApi
fun Module.typeResolver(): ModuleTypeResolver = ModuleTypeResolver(
    definedTypes = definedTypes,
    functionTypeIndices = ModuleTypeResolver.functionTypeIndices(
        imports = imports,
        definedFunctionTypeIndices = IntArray(functions.size) { index -> functions[index].typeIndex.idx.toInt() },
    ),
)
