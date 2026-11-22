package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.runtime.type.ext.clone

fun moduleInfo(module: Module): ModuleInfo {
    return ModuleInfo(
        imports = module.imports.map { import -> import.copy(type = import.type.clone()) },
        exports = module.exports.map { export -> export.copy(type = export.type.clone()) },
    )
}
