package io.github.charlietap.chasm.compiler.backend.jvm.abi

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.staticParameters
import io.github.charlietap.chasm.compiler.backend.jvm.DATA_EXTENSION
import io.github.charlietap.chasm.type.FunctionType

internal fun bindingClassDescriptor(
    packagePath: String,
) = "L$packagePath/Binding;"

internal fun codeClassDescriptor(
    packagePath: String,
    batch: Int,
) = "L$packagePath/Code$batch;"

internal fun chunkClassDescriptor(
    packagePath: String,
    index: Int,
    group: Int,
) = "L$packagePath/Chunks$index\$$group;"

internal fun entriesClassDescriptor(
    packagePath: String,
    group: Int,
) = "L$packagePath/Entries$group;"

internal fun signatureClassDescriptor(
    packagePath: String,
    index: Int,
) = "L$packagePath/Sig$index;"

internal fun moduleClassDescriptor(
    packagePath: String,
) = "L$packagePath/WasmModule;"

internal fun dataResource(
    packagePath: String,
) = "$packagePath/data.$DATA_EXTENSION"

internal fun installClassDescriptor(
    packagePath: String,
    chunk: Int,
) = "L$packagePath/Install$chunk;"

internal fun functionName(
    index: Int,
) = "f$index"

internal fun functionMethod(
    owner: String,
    binding: String,
    index: Int,
    type: FunctionType,
) =
    MethodRef(owner, functionName(index), staticParameters(type, binding), returnDescriptor(type))

internal const val TYPED_CALL = "typed"

internal const val SOURCE_FILE = "module.wasm"
