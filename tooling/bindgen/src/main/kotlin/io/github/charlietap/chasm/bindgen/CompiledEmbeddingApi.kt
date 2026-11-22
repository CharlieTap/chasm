package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.BYTE_ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

internal class CompiledEmbeddingApi(
    override val functionCallConvention: FunctionCallConvention = UnboxedFunctionCallConvention(),
) : EmbeddingApi {

    override val store = ClassName(COMPILED_PACKAGE, "Store")

    override val module = ClassName(COMPILED_PACKAGE, "Module")

    override val instance = ClassName(COMPILED_PACKAGE, "Instance")

    override val import = ClassName(COMPILED_PACKAGE, "Import")

    override val memory = ClassName(COMPILED_PACKAGE, "Memory")

    override val global = ClassName(COMPILED_PACKAGE, "Global")

    override val preparedFunction = ClassName(COMPILED_PACKAGE, "PreparedFunction")

    override val codegenImport = ClassName(CODEGEN_PACKAGE, "CodegenImport")

    override val allocator = ClassName(CODEGEN_PACKAGE, "Wasm32Allocator")

    override val storeFactory = MemberName(JIT_PACKAGE, "store")

    override val allocateImports = MemberName(CODEGEN_PACKAGE, "allocateImports")

    override val prepareFunction = MemberName(COMPILED_PACKAGE, "prepareFunction")

    override val invoke = MemberName(COMPILED_PACKAGE, "invoke", isExtension = true)

    override val readBytes = MemberName(COMPILED_PACKAGE, "readBytes")

    override val writeBytes = MemberName(COMPILED_PACKAGE, "writeBytes")

    override val readInt = MemberName(COMPILED_PACKAGE, "readInt")

    override val writeInt = MemberName(COMPILED_PACKAGE, "writeInt")

    override val readUtf8String = MemberName(COMPILED_PACKAGE, "readUtf8String")

    override val readNullTerminatedUtf8String = MemberName(COMPILED_PACKAGE, "readNullTerminatedUtf8String")

    override val memoryAccessParameters = emptyList<ParameterSpec>()

    override val moduleFactoryArguments = ", validate = true"

    override fun suspendingFactories(config: CodegenConfig): Boolean {
        return true
    }

    override fun moduleFactory(suspending: Boolean): MemberName {
        return MODULE
    }

    override fun instanceFactory(suspending: Boolean): MemberName {
        return INSTANCE
    }

    override fun moduleFactoryOverrideType(suspending: Boolean): TypeName {
        return LambdaTypeName.get(null, BYTE_ARRAY, returnType = module).copy(suspending = true)
    }

    override fun instanceFactoryOverrideType(suspending: Boolean): TypeName {

        val imports = List::class.asClassName().parameterizedBy(import)

        return LambdaTypeName.get(null, store, module, imports, returnType = instance).copy(suspending = true)
    }

    override fun storeFunction(function: StoreFunction): MemberName {
        return MemberName(COMPILED_PACKAGE, function.functionName)
    }

    override fun accessorCall(
        function: MemberName,
        arguments: CodeBlock,
    ): CodeBlock {
        return CodeBlock.of("%M(%L)", function, arguments)
    }

    override fun writeNullTerminator(
        memory: String,
        pointer: CodeBlock,
    ): CodeBlock {
        return CodeBlock.of("%M(%L, %L, 0)", WRITE_BYTE, memory, pointer)
    }

    override fun exportLookup(
        kind: String,
        exportsAccessor: String,
        name: String,
        type: ClassName,
    ): CodeBlock {
        return CodeBlock.of("%M(instance).%N(%S) ?: error(%S)", EXPORTS, exportsAccessor, name, "Failed to find $kind export with name $name")
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

private const val CODEGEN_PACKAGE = "$COMPILED_PACKAGE.codegen"
private const val JIT_PACKAGE = "io.github.charlietap.chasm.jit"

private val MODULE = MemberName(JIT_PACKAGE, "module")
private val INSTANCE = MemberName(COMPILED_PACKAGE, "instance")
private val EXPORTS = MemberName(COMPILED_PACKAGE, "exports")
private val READ_GLOBAL = MemberName(COMPILED_PACKAGE, "readGlobal")
private val WRITE_GLOBAL = MemberName(COMPILED_PACKAGE, "writeGlobal")
private val WRITE_BYTE = MemberName(COMPILED_PACKAGE, "writeByte")
