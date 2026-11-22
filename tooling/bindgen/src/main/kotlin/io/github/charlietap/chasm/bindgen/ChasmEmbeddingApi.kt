package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.BYTE_ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

internal class ChasmEmbeddingApi(
    override val functionCallConvention: FunctionCallConvention = UnboxedFunctionCallConvention(),
) : EmbeddingApi {

    override val store = ClassName(SHAPES_PACKAGE, "Store")

    override val module = ClassName(SHAPES_PACKAGE, "Module")

    override val instance = ClassName(SHAPES_PACKAGE, "Instance")

    override val import = ClassName(SHAPES_PACKAGE, "Import")

    override val memory = ClassName(SHAPES_PACKAGE, "Memory")

    override val global = ClassName(SHAPES_PACKAGE, "Global")

    override val preparedFunction = ClassName(SHAPES_PACKAGE, "PreparedFunction")

    override val codegenImport = ClassName(CODEGEN_PACKAGE, "CodegenImport")

    override val allocator = ClassName(CODEGEN_PACKAGE, "Wasm32Allocator")

    override val allocateImports = MemberName(CODEGEN_PACKAGE, "allocateImports")

    override val moduleFactory = MemberName(EMBEDDING_PACKAGE, "module")

    override val instanceFactory = MemberName(EMBEDDING_PACKAGE, "instance")

    override val compiledModuleFactory = MemberName(AOT_PACKAGE, "compiledModule")

    override val compiledModuleMetadata = MemberName(AOT_PACKAGE, "compiledModuleMetadata")

    override val prepareFunction = MemberName(EMBEDDING_PACKAGE, "prepareFunction")

    override val invoke = MemberName(EMBEDDING_PACKAGE, "invoke")

    override val readBytes = MemberName(MEMORY_PACKAGE, "readBytes")

    override val writeBytes = MemberName(MEMORY_PACKAGE, "writeBytes")

    override val readInt = MemberName(MEMORY_PACKAGE, "readInt")

    override val writeInt = MemberName(MEMORY_PACKAGE, "writeInt")

    override val readUtf8String = MemberName(MEMORY_PACKAGE, "readUtf8String")

    override val readNullTerminatedUtf8String = MemberName(MEMORY_PACKAGE, "readNullTerminatedUtf8String")

    override val moduleFactoryOverrideType: TypeName = LambdaTypeName.get(null, BYTE_ARRAY, returnType = module)
        .copy(suspending = true)

    override val instanceFactoryOverrideType: TypeName = LambdaTypeName.get(
        null,
        store,
        module,
        List::class.asClassName().parameterizedBy(import),
        returnType = instance,
    ).copy(suspending = true)

    override fun storeFactory(tier: ExecutionTier): MemberName {
        return when (tier) {
            is ExecutionTier.Interpreter -> MemberName(EMBEDDING_PACKAGE, "store")
            is ExecutionTier.Jit -> MemberName(JIT_PACKAGE, "store")
            is ExecutionTier.Aot -> MemberName(AOT_PACKAGE, "store")
        }
    }

    override fun storeFunction(function: StoreFunction): MemberName {
        return MemberName(EMBEDDING_PACKAGE, function.functionName)
    }

    override fun writeNullTerminator(
        memory: String,
        pointer: CodeBlock,
    ): CodeBlock {
        return CodeBlock.of("%M(%L, %L, 0)", WRITE_BYTE, memory, pointer)
    }

    override fun exportLookup(
        kind: ExportKind,
        name: String,
    ): CodeBlock {
        return CodeBlock.of(
            "%M(instance).%N(%S) ?: error(%S)",
            EXPORTS,
            kind.accessor,
            name,
            "Failed to find ${kind.accessor} export with name $name",
        )
    }

    override fun globalRead(
        global: String,
        type: Type,
    ): CodeBlock {

        val bits = CodeBlock.of("%M(%N)", READ_GLOBAL, global)

        return when (type) {
            Scalar.Integer -> CodeBlock.of("%L.toInt()", bits)
            Scalar.Long -> bits
            Scalar.Float -> CodeBlock.of("%T.fromBits(%L.toInt())", FLOAT, bits)
            Scalar.Double -> CodeBlock.of("%T.fromBits(%L)", DOUBLE, bits)
            else -> error("Cannot read $type from a global")
        }
    }

    override fun globalWrite(
        global: String,
        type: Type,
        value: String,
    ): CodeBlock {

        val bits = when (type) {
            Scalar.Integer -> CodeBlock.of("%L.toLong()", value)
            Scalar.Long -> CodeBlock.of("%L", value)
            Scalar.Float -> CodeBlock.of("%L.toRawBits().toLong()", value)
            Scalar.Double -> CodeBlock.of("%L.toRawBits()", value)
            else -> error("Cannot write $type to a global")
        }

        return CodeBlock.of("%M(%N, %L)", WRITE_GLOBAL, global, bits)
    }
}

private const val EMBEDDING_PACKAGE = "io.github.charlietap.chasm.embedding"
private const val SHAPES_PACKAGE = "$EMBEDDING_PACKAGE.shapes"
private const val CODEGEN_PACKAGE = "$EMBEDDING_PACKAGE.codegen"
private const val MEMORY_PACKAGE = "$EMBEDDING_PACKAGE.memory"
private const val GLOBAL_PACKAGE = "$EMBEDDING_PACKAGE.global"
private const val JIT_PACKAGE = "io.github.charlietap.chasm.jit"
private const val AOT_PACKAGE = "io.github.charlietap.chasm.aot"

private val EXPORTS = MemberName(EMBEDDING_PACKAGE, "exports")
private val READ_GLOBAL = MemberName(GLOBAL_PACKAGE, "readGlobal")
private val WRITE_GLOBAL = MemberName(GLOBAL_PACKAGE, "writeGlobal")
private val WRITE_BYTE = MemberName(MEMORY_PACKAGE, "writeByte")
