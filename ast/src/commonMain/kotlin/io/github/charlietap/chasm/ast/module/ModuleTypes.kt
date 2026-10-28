package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.declarations.ModuleTypeResolver

fun Module.typeResolver(): ModuleTypeResolver = ModuleTypeResolver(
    definedTypes = definedTypes,
    functionTypeIndices = ModuleTypeResolver.functionTypeIndices(
        imports = imports,
        definedFunctionTypeIndices = IntArray(functions.size) { index -> functions[index].typeIndex.idx.toInt() },
    ),
)
