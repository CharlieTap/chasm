package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName

internal class RuntimeEmbeddingApi(
    override val functionCallConvention: FunctionCallConvention = BoxedFunctionCallConvention(
        executionValue = EXECUTION_VALUE,
        numberValue = NUMBER_VALUE,
    ),
) : EmbeddingApi {

    override val store = ClassName(SHAPES_PACKAGE, "Store")

    override val module = ClassName(SHAPES_PACKAGE, "Module")

    override val instance = ClassName(SHAPES_PACKAGE, "Instance")

    override val import = ClassName(SHAPES_PACKAGE, "Import")

    override val memory = ClassName(SHAPES_PACKAGE, "Memory")

    override val global = ClassName(SHAPES_PACKAGE, "Global")

    override val preparedFunction = ClassName(SHAPES_PACKAGE, "PreparedFunction")

    override val codegenImport = ClassName(CODEGEN_PACKAGE, "CodegenImport")

    override val allocator = ClassName(SHAPES_PACKAGE, "Wasm32Allocator")

    override val storeFactory = MemberName(EMBEDDING_PACKAGE, "store")

    override val allocateImports = MemberName(CODEGEN_PACKAGE, "allocateImports")

    override val prepareFunction = MemberName(EMBEDDING_PACKAGE, "prepareFunction")

    override val invoke = MemberName(EMBEDDING_PACKAGE, "invoke")

    override val readBytes = MemberName(MEMORY_PACKAGE, "readBytes")

    override val writeBytes = MemberName(MEMORY_PACKAGE, "writeBytes")

    override val readInt = MemberName(MEMORY_PACKAGE, "readInt")

    override val writeInt = MemberName(MEMORY_PACKAGE, "writeInt")

    override val readUtf8String = MemberName(MEMORY_PACKAGE, "readUtf8String")

    override val readNullTerminatedUtf8String = MemberName(MEMORY_PACKAGE, "readNullTerminatedUtf8String")

    override val memoryAccessParameters = listOf(ParameterSpec("store", store))

    override val moduleFactoryArguments = ""

    override fun suspendingFactories(config: CodegenConfig): Boolean {
        return config.generateSuspendingFactories
    }

    override fun moduleFactory(suspending: Boolean): MemberName {
        return if (suspending) SUSPENDING_MODULE else MODULE
    }

    override fun instanceFactory(suspending: Boolean): MemberName {
        return if (suspending) SUSPENDING_INSTANCE else INSTANCE
    }

    override fun moduleFactoryOverrideType(suspending: Boolean): TypeName {
        return if (suspending) SUSPEND_MODULE_FACTORY else MODULE_FACTORY
    }

    override fun instanceFactoryOverrideType(suspending: Boolean): TypeName {
        return if (suspending) SUSPEND_INSTANCE_FACTORY else INSTANCE_FACTORY
    }

    override fun storeFunction(function: StoreFunction): MemberName {
        return MemberName(EMBEDDING_PACKAGE, function.functionName)
    }

    override fun storeControlCall(
        call: CodeBlock,
        failure: String,
    ): CodeBlock {
        return CodeBlock.of("%L.%M(%S)", call, CHASM_EXPECT, failure)
    }

    override fun accessorCall(
        function: MemberName,
        arguments: CodeBlock,
        failure: String,
    ): CodeBlock {
        return CodeBlock.of("%M(store, %L).%M(%S)", function, arguments, CHASM_EXPECT, failure)
    }

    override fun writeNullTerminator(
        memory: String,
        pointer: CodeBlock,
    ): CodeBlock {
        return accessorCall(writeBytes, CodeBlock.of("%L, %L, byteArrayOf(0)", memory, pointer), "Failed to write null terminator")
    }

    override fun exportLookup(
        kind: String,
        exportsAccessor: String,
        name: String,
        type: ClassName,
    ): CodeBlock {
        return CodeBlock.of(
            "(instance.exports.firstOrNull { export -> export.name == %S }?.value as? %T) ?: error(%S)",
            name,
            type,
            "Failed to find $kind export with name $name",
        )
    }

    override fun globalRead(
        global: String,
        type: Type,
        failure: String,
    ): CodeBlock {

        val value = accessorCall(READ_GLOBAL, CodeBlock.of("%N", global), failure)

        return CodeBlock.of("(%L as %T).value", value, NUMBER_VALUE.nestedClass(numberValueName(type)))
    }

    override fun globalWrite(
        global: String,
        type: Type,
        value: String,
        failure: String,
    ): CodeBlock {
        return accessorCall(WRITE_GLOBAL, CodeBlock.of("%N, %T(%L)", global, NUMBER_VALUE.nestedClass(numberValueName(type)), value), failure)
    }
}

private const val EMBEDDING_PACKAGE = "io.github.charlietap.chasm.embedding"
private const val SHAPES_PACKAGE = "io.github.charlietap.chasm.embedding.shapes"
private const val MEMORY_PACKAGE = "io.github.charlietap.chasm.embedding.memory"
private const val CODEGEN_PACKAGE = "io.github.charlietap.chasm.embedding.codegen"
private const val COROUTINES_PACKAGE = "io.github.charlietap.chasm.coroutines"
private const val VALUE_PACKAGE = "io.github.charlietap.chasm.runtime.value"

private val EXECUTION_VALUE = ClassName(VALUE_PACKAGE, "ExecutionValue")
private val NUMBER_VALUE = ClassName(VALUE_PACKAGE, "NumberValue")
private val MODULE = MemberName(EMBEDDING_PACKAGE, "module")
private val INSTANCE = MemberName(EMBEDDING_PACKAGE, "instance")
private val SUSPENDING_MODULE = MemberName(COROUTINES_PACKAGE, "module")
private val SUSPENDING_INSTANCE = MemberName(COROUTINES_PACKAGE, "instance")
private val MODULE_FACTORY = ClassName(CODEGEN_PACKAGE, "ModuleFactory")
private val SUSPEND_MODULE_FACTORY = ClassName(CODEGEN_PACKAGE, "SuspendModuleFactory")
private val INSTANCE_FACTORY = ClassName(CODEGEN_PACKAGE, "InstanceFactory")
private val SUSPEND_INSTANCE_FACTORY = ClassName(CODEGEN_PACKAGE, "SuspendInstanceFactory")
private val READ_GLOBAL = MemberName("io.github.charlietap.chasm.embedding.global", "readGlobal")
private val WRITE_GLOBAL = MemberName("io.github.charlietap.chasm.embedding.global", "writeGlobal")
