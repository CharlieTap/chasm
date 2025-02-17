// package io.github.charlietap.chasm.embedding.transform
//
// import io.github.charlietap.chasm.ir.module.Import
// import io.github.charlietap.chasm.executor.runtime.type.ExternalType
// import io.github.charlietap.chasm.type.ext.functionType
//
// internal class ImportDescriptorMapper : Mapper<Import.Descriptor, ExternalType> {
//
//    override fun map(input: Import.Descriptor): ExternalType {
//        return when (input) {
//            is Import.Descriptor.Function -> {
//                ExternalType.Function(input.type.functionType()!!)
//            }
//            is Import.Descriptor.Global -> {
//                ExternalType.Global(input.type)
//            }
//            is Import.Descriptor.Memory -> {
//                ExternalType.Memory(input.type)
//            }
//            is Import.Descriptor.Table -> {
//                ExternalType.Table(input.type)
//            }
//            is Import.Descriptor.Tag -> {
//                ExternalType.Tag(input.type)
//            }
//        }
//    }
//
//    companion object {
//        val instance = ImportDescriptorMapper()
//    }
// }
