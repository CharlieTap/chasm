package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

private const val EMBEDDING_PACKAGE = "io.github.charlietap.chasm.embedding"
private const val SHAPES_PACKAGE = "$EMBEDDING_PACKAGE.shapes"
private const val CODEGEN_PACKAGE = "$EMBEDDING_PACKAGE.codegen"
private const val MEMORY_PACKAGE = "$EMBEDDING_PACKAGE.memory"
private const val GLOBAL_PACKAGE = "$EMBEDDING_PACKAGE.global"
private const val RUNTIME_COMPILED_PACKAGE = "io.github.charlietap.chasm.compiled"

internal val CHASM_STORE_CLASS_NAME = ClassName(SHAPES_PACKAGE, "Store")
internal val CHASM_MODULE_CLASS_NAME = ClassName(SHAPES_PACKAGE, "Module")
internal val CHASM_INSTANCE_CLASS_NAME = ClassName(SHAPES_PACKAGE, "Instance")
internal val CHASM_IMPORT_CLASS_NAME = ClassName(SHAPES_PACKAGE, "Import")
internal val CHASM_MEMORY_CLASS_NAME = ClassName(SHAPES_PACKAGE, "Memory")
internal val CHASM_GLOBAL_CLASS_NAME = ClassName(SHAPES_PACKAGE, "Global")
internal val CHASM_PREPARED_FUNCTION_CLASS_NAME = ClassName(SHAPES_PACKAGE, "PreparedFunction")
internal val CHASM_CODEGEN_IMPORT_CLASS_NAME = ClassName(CODEGEN_PACKAGE, "CodegenImport")
internal val CHASM_ALLOCATOR_CLASS_NAME = ClassName(CODEGEN_PACKAGE, "Wasm32Allocator")

internal val CHASM_ALLOCATE_IMPORTS_FUNCTION = MemberName(CODEGEN_PACKAGE, "allocateImports")
internal val CHASM_STORE_FUNCTION = MemberName(EMBEDDING_PACKAGE, "store")
internal val CHASM_MODULE_FUNCTION = MemberName(EMBEDDING_PACKAGE, "module")
internal val CHASM_INSTANCE_FUNCTION = MemberName(EMBEDDING_PACKAGE, "instance")
internal val CHASM_COMPILED_STORE_FUNCTION = MemberName(RUNTIME_COMPILED_PACKAGE, "store")
internal val CHASM_COMPILED_MODULE_FUNCTION = MemberName(RUNTIME_COMPILED_PACKAGE, "module")
internal val CHASM_PREPARE_FUNCTION = MemberName(EMBEDDING_PACKAGE, "prepareFunction")
internal val CHASM_INVOKE_FUNCTION = MemberName(EMBEDDING_PACKAGE, "invoke")
internal val CHASM_EXPORTS_FUNCTION = MemberName(EMBEDDING_PACKAGE, "exports")
internal val CHASM_READ_GLOBAL_FUNCTION = MemberName(GLOBAL_PACKAGE, "readGlobal")
internal val CHASM_WRITE_GLOBAL_FUNCTION = MemberName(GLOBAL_PACKAGE, "writeGlobal")
internal val CHASM_READ_BYTES_FUNCTION = MemberName(MEMORY_PACKAGE, "readBytes")
internal val CHASM_WRITE_BYTES_FUNCTION = MemberName(MEMORY_PACKAGE, "writeBytes")
internal val CHASM_WRITE_BYTE_FUNCTION = MemberName(MEMORY_PACKAGE, "writeByte")
internal val CHASM_READ_INT_FUNCTION = MemberName(MEMORY_PACKAGE, "readInt")
internal val CHASM_WRITE_INT_FUNCTION = MemberName(MEMORY_PACKAGE, "writeInt")
internal val CHASM_READ_UTF8_STRING_FUNCTION = MemberName(MEMORY_PACKAGE, "readUtf8String")
internal val CHASM_READ_NULL_TERMINATED_UTF8_STRING_FUNCTION = MemberName(MEMORY_PACKAGE, "readNullTerminatedUtf8String")

internal fun chasmStoreFunction(function: StoreFunction): MemberName {
    return MemberName(EMBEDDING_PACKAGE, function.functionName)
}
