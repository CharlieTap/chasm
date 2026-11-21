package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.embedding.compiled.ExternalType
import io.github.charlietap.chasm.embedding.compiled.Module
import io.github.charlietap.chasm.embedding.compiled.ModuleExport
import io.github.charlietap.chasm.embedding.compiled.ModuleImport
import io.github.charlietap.chasm.embedding.shapes._declarations
import io.github.charlietap.chasm.embedding.shapes.Module as ClassicModule
import io.github.charlietap.chasm.runtime.type.ExternalType as RuntimeExternalType

@InternalChasmApi
fun _compiledModule(classic: ClassicModule): Module {

    val info = moduleInfo(classic._declarations())

    return Module(
        imports = info.imports.map { import -> ModuleImport(import.moduleName, import.entityName, externalType(import.type)) },
        exports = info.exports.map { export -> ModuleExport(export.name, externalType(export.type)) },
        payload = classic,
    )
}

@InternalChasmApi
fun _classicModule(module: Module): ClassicModule {
    return module.payload as ClassicModule
}

private fun externalType(type: RuntimeExternalType): ExternalType {
    return when (type) {
        is RuntimeExternalType.Function -> ExternalType.Function(type.functionType)
        is RuntimeExternalType.Memory -> ExternalType.Memory(type.memoryType)
        is RuntimeExternalType.Table -> ExternalType.Table(type.tableType)
        is RuntimeExternalType.Global -> ExternalType.Global(type.globalType)
        is RuntimeExternalType.Tag -> ExternalType.Tag(type.tagType)
    }
}
