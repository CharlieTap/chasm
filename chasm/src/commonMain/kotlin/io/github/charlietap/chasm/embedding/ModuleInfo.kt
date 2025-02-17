// package io.github.charlietap.chasm.embedding
//
// import io.github.charlietap.chasm.ir.module.Export
// import io.github.charlietap.chasm.ir.module.Import
// import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
// import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
// import io.github.charlietap.chasm.embedding.shapes.Module
// import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
// import io.github.charlietap.chasm.embedding.transform.ExportDescriptorMapper
// import io.github.charlietap.chasm.embedding.transform.ImportDescriptorMapper
// import io.github.charlietap.chasm.embedding.transform.Mapper
// import io.github.charlietap.chasm.executor.runtime.type.ExternalType
//
// fun moduleInfo(
//    module: Module,
// ): ModuleInfo = moduleInfo(
//    module = module,
//    importDescriptorMapper = ImportDescriptorMapper.instance,
//    exportDescriptorMapper = ExportDescriptorMapper(module.module),
// )
//
// internal fun moduleInfo(
//    module: Module,
//    importDescriptorMapper: Mapper<Import.Descriptor, ExternalType>,
//    exportDescriptorMapper: Mapper<Export.Descriptor, ExternalType>,
// ): ModuleInfo {
//
//    val internalModule = module.module
//
//    val imports = internalModule.imports.map { import ->
//        val type = importDescriptorMapper.map(import.descriptor)
//        ImportDefinition(import.moduleName.name, import.entityName.name, type)
//    }
//
//    val exports = internalModule.exports.map { export ->
//        val type = exportDescriptorMapper.map(export.descriptor)
//        ExportDefinition(export.name.name, type)
//    }
//
//    return ModuleInfo(imports, exports)
// }
