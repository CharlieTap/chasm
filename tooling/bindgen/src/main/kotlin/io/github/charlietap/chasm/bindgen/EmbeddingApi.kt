package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName

internal interface EmbeddingApi {

    val store: ClassName

    val module: ClassName

    val instance: ClassName

    val import: ClassName

    val memory: ClassName

    val global: ClassName

    val preparedFunction: ClassName

    val codegenImport: ClassName

    val allocator: ClassName

    val allocateImports: MemberName

    val moduleFactory: MemberName

    val instanceFactory: MemberName

    val compiledModuleFactory: MemberName

    val compiledModuleMetadata: MemberName

    val prepareFunction: MemberName

    val invoke: MemberName

    val readBytes: MemberName

    val writeBytes: MemberName

    val readInt: MemberName

    val writeInt: MemberName

    val readUtf8String: MemberName

    val readNullTerminatedUtf8String: MemberName

    val moduleFactoryOverrideType: TypeName

    val instanceFactoryOverrideType: TypeName

    val functionCallConvention: FunctionCallConvention

    fun storeFactory(tier: ExecutionTier): MemberName

    fun storeFunction(function: StoreFunction): MemberName

    fun writeNullTerminator(
        memory: String,
        pointer: CodeBlock,
    ): CodeBlock

    fun exportLookup(
        kind: ExportKind,
        name: String,
    ): CodeBlock

    fun globalRead(
        global: String,
        type: Type,
    ): CodeBlock

    fun globalWrite(
        global: String,
        type: Type,
        value: String,
    ): CodeBlock
}

internal enum class ExportKind(
    val accessor: String,
) {
    Memory("memory"),
    Global("global"),
}

internal enum class StoreFunction(
    val functionName: String,
    val alias: String,
) {
    AddFuel("addFuel", "chasmAddFuel"),
    RemainingFuel("remainingFuel", "chasmRemainingFuel"),
    Interrupt("interrupt", "chasmInterrupt"),
}
