package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName

internal interface EmbeddingApi {

    val store: ClassName

    val module: ClassName

    val instance: ClassName

    val import: ClassName

    val memory: ClassName

    val global: ClassName

    val preparedFunction: ClassName

    val executionValue: ClassName

    val codegenImport: ClassName

    val allocator: ClassName

    val storeFactory: MemberName

    val allocateImports: MemberName

    val prepareFunction: MemberName

    val invoke: MemberName

    val readBytes: MemberName

    val writeBytes: MemberName

    val readInt: MemberName

    val writeInt: MemberName

    val readUtf8String: MemberName

    val readNullTerminatedUtf8String: MemberName

    val memoryAccessParameters: List<ParameterSpec>

    val moduleFactoryArguments: String

    fun suspendingFactories(config: CodegenConfig): Boolean

    fun moduleFactory(suspending: Boolean): MemberName

    fun instanceFactory(suspending: Boolean): MemberName

    fun moduleFactoryOverrideType(suspending: Boolean): TypeName

    fun instanceFactoryOverrideType(suspending: Boolean): TypeName

    fun numberValue(type: Type): ClassName

    fun storeFunction(function: StoreFunction): MemberName

    fun preparedFunctionCall(
        preparedFunction: String,
        arguments: String,
    ): CodeBlock

    fun storeControlCall(
        call: CodeBlock,
        failure: String,
    ): CodeBlock

    fun accessorCall(
        function: MemberName,
        arguments: CodeBlock,
        failure: String,
    ): CodeBlock

    fun writeNullTerminator(
        memory: String,
        pointer: CodeBlock,
    ): CodeBlock

    fun exportLookup(
        kind: String,
        exportsAccessor: String,
        name: String,
        type: ClassName,
    ): CodeBlock

    fun globalRead(
        global: String,
        type: Type,
        failure: String,
    ): CodeBlock

    fun globalWrite(
        global: String,
        type: Type,
        value: String,
        failure: String,
    ): CodeBlock
}

internal enum class StoreFunction(
    val functionName: String,
    val alias: String,
) {
    AddFuel("addFuel", "chasmAddFuel"),
    RemainingFuel("remainingFuel", "chasmRemainingFuel"),
    Interrupt("interrupt", "chasmInterrupt"),
}

internal fun numberValueName(type: Type): String {
    return when (type) {
        Scalar.Integer -> "I32"
        Scalar.Long -> "I64"
        Scalar.Float -> "F32"
        Scalar.Double -> "F64"
        else -> error("Cannot convert $type to a Chasm execution value")
    }
}
