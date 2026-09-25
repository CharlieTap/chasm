package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.gradle.ext.asTypeName

internal class ChasmClassImplementationGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: ImplementationVisibility,
        wasmInterface: WasmInterface,
    ): TypeSpec = TypeSpec.classBuilder(interfaceName + "Impl").apply {
        addModifiers(visibility.modifier())
        addSuperinterface(ClassName(packageName, interfaceName))
        addConstructor()
        addRuntimeProperties(wasmInterface)
        addBindings(packageName, interfaceName, wasmInterface)

        if (wasmInterface.initializers.isNotEmpty()) {
            addInitializerBlock(initializerBlock(wasmInterface.initializers))
        }
        if (wasmInterface.memories.any { memory -> memory.exposed }) {
            addType(memoryImplementation(packageName, interfaceName))
        }
        wasmInterface.functions.forEach { function ->
            addFunction(functionImplementation(packageName, function))
        }
    }.build()

    private fun TypeSpec.Builder.addConstructor() {
        primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("store", CHASM_STORE_CLASS_NAME)
                .addParameter("instance", CHASM_INSTANCE_CLASS_NAME)
                .build(),
        )
        addProperty(
            PropertySpec.builder("store", CHASM_STORE_CLASS_NAME)
                .addModifiers(KModifier.PRIVATE)
                .initializer("store")
                .build(),
        )
        addProperty(
            PropertySpec.builder("instance", CHASM_INSTANCE_CLASS_NAME)
                .addModifiers(KModifier.PRIVATE)
                .initializer("instance")
                .build(),
        )
    }

    private fun TypeSpec.Builder.addRuntimeProperties(wasmInterface: WasmInterface) {
        wasmInterface.functions.forEach { function ->
            val proxy = function.implementation as FunctionProxy
            addProperty(
                PropertySpec.builder(preparedFunctionPropertyName(function), CHASM_PREPARED_FUNCTION_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(
                        "%M(store, instance, %S).%M(%S)",
                        CHASM_PREPARE_FUNCTION,
                        proxy.name,
                        CHASM_EXPECT,
                        "Failed to prepare function ${proxy.name}",
                    ).build(),
            )
        }

        val inputCounts = wasmInterface.functions.map(Function::inputCount)
            .filter { count -> count > 0 }
            .distinct()
            .sorted()
        inputCounts.maxOrNull()?.let { maximumInputCount ->
            addProperty(
                PropertySpec.builder(FUNCTION_INPUT_BUFFER_NAME, CHASM_EXECUTION_VALUE_MUTABLE_LIST)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("MutableList<%T>(%L) { %T(0) }", CHASM_EXECUTION_VALUE_CLASS_NAME, maximumInputCount, CHASM_I32_VALUE_CLASS_NAME)
                    .build(),
            )
            inputCounts.forEach { inputCount ->
                val initializer = if (inputCount == maximumInputCount) {
                    CodeBlock.of("%L", FUNCTION_INPUT_BUFFER_NAME)
                } else {
                    CodeBlock.of("%L.subList(0, %L)", FUNCTION_INPUT_BUFFER_NAME, inputCount)
                }
                addProperty(
                    PropertySpec.builder(functionInputBufferName(inputCount), CHASM_EXECUTION_VALUE_MUTABLE_LIST)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer(initializer)
                        .build(),
                )
            }
        }
    }

    private fun TypeSpec.Builder.addBindings(
        packageName: String,
        interfaceName: String,
        wasmInterface: WasmInterface,
    ) {
        wasmInterface.memories.forEach { memory ->
            addProperty(importableProperty(memory.backingName, memory.source, CHASM_MEMORY_CLASS_NAME, "memory"))
        }

        wasmInterface.allocator?.let { allocator ->
            addProperty(
                PropertySpec.builder("allocator", CHASM_WASM32_ALLOCATOR_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(
                        "%T(instance, store, %S, %S)",
                        CHASM_WASM32_ALLOCATOR_CLASS_NAME,
                        allocator.allocationFunction,
                        allocator.deallocationFunction,
                    ).build(),
            )
        }

        wasmInterface.properties.forEach { property ->
            val proxy = property.implementation as GlobalProxy
            addProperty(importableProperty("_${property.name}", proxy.name, CHASM_GLOBAL_CLASS_NAME, "global"))
            addProperty(globalProperty(property, proxy))
        }

        wasmInterface.memories.filter { memory -> memory.exposed }.forEach { memory ->
            addProperty(
                PropertySpec.builder(memory.name, ClassName(packageName, interfaceName, "Memory"))
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%T(store, %N)", ClassName(packageName, interfaceName + "Impl", "MemoryImpl"), memory.backingName)
                    .build(),
            )
        }
    }

    private fun importableProperty(
        propertyName: String,
        exportName: String,
        type: ClassName,
        kind: String,
    ): PropertySpec = PropertySpec.builder(propertyName, type)
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            "(instance.exports.firstOrNull { export -> export.name == %S }?.value as? %T)" +
                " ?: error(%S)",
            exportName,
            type,
            "Failed to find $kind export with name $exportName",
        ).build()

    private fun globalProperty(
        property: Property,
        proxy: GlobalProxy,
    ): PropertySpec = PropertySpec.builder(property.name, property.type.asTypeName()).apply {
        addModifiers(KModifier.OVERRIDE)
        mutable(property.const.not())
        getter(
            FunSpec.getterBuilder()
                .addStatement(
                    "return (%M(store, %N).%M(%S) as %T).value",
                    CHASM_READ_GLOBAL,
                    "_${property.name}",
                    CHASM_EXPECT,
                    "Failed to read global ${proxy.name}",
                    property.type.chasmValueClass(),
                ).build(),
        )
        if (!property.const) {
            setter(
                FunSpec.setterBuilder()
                    .addParameter("newValue", property.type.asTypeName())
                    .addStatement(
                        "%M(store, %N, %T(newValue)).%M(%S)",
                        CHASM_WRITE_GLOBAL,
                        "_${property.name}",
                        property.type.chasmValueClass(),
                        CHASM_EXPECT,
                        "Failed to write global ${proxy.name}",
                    ).build(),
            )
        }
    }.build()

    private fun initializerBlock(initializers: Set<String>): CodeBlock = CodeBlock.builder().apply {
        initializers.forEach { name ->
            addStatement(
                "%M(store, instance, %S).%M(%S)",
                CHASM_INVOKE,
                name,
                CHASM_EXPECT,
                "Initializer function $name failed",
            )
        }
    }.build()

    private fun functionImplementation(
        packageName: String,
        function: Function,
    ): FunSpec = FunSpec.builder(function.name).apply {
        addModifiers(KModifier.OVERRIDE)
        val returnType = functionReturnType(packageName, function)
        returns(returnType)
        function.params.forEach { param -> addParameter(param.name, param.type.asTypeName()) }

        val allocationsToFree = writeStringParameters(function)
        writeArguments(function)
        writeReturn(function, returnType, allocationsToFree)
    }.build()

    private fun FunSpec.Builder.writeStringParameters(function: Function): List<String> {
        val allocationsToFree = mutableListOf<String>()
        function.params.filter { param -> param.type == Scalar.String }.forEach { param ->
            val bytes = param.name + "Bytes"
            val allocation = param.name + "Alloc"
            addStatement("val %L = %L.encodeToByteArray()", bytes, param.name)
            when (requireNotNull(param.stringEncodingStrategy)) {
                StringEncodingStrategy.POINTER_AND_LENGTH,
                StringEncodingStrategy.PACKED_POINTER_AND_LENGTH,
                -> {
                    addStatement("val %L = allocator.alloc(%L.size)", allocation, bytes)
                    writeBytes(allocation, bytes)
                }
                StringEncodingStrategy.NULL_TERMINATED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 1)", allocation, bytes)
                    writeBytes(allocation, bytes)
                    addStatement(
                        "%M(store, %L, %L + %L.size, byteArrayOf(0)).%M(%S)",
                        CHASM_WRITE_BYTES,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocation,
                        bytes,
                        CHASM_EXPECT,
                        "Failed to write null terminator",
                    )
                }
                StringEncodingStrategy.LENGTH_PREFIXED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 4)", allocation, bytes)
                    addStatement(
                        "%M(store, %L, %L, %L.size).%M(%S)",
                        CHASM_WRITE_INT,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocation,
                        bytes,
                        CHASM_EXPECT,
                        "Failed to write string length",
                    )
                    addStatement(
                        "%M(store, %L, %L + 4, %L).%M(%S)",
                        CHASM_WRITE_BYTES,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocation,
                        bytes,
                        CHASM_EXPECT,
                        "Failed to write string",
                    )
                }
            }
            if (param.stringAllocationStrategy?.freeAfterCall == true) allocationsToFree.add(allocation)
        }
        return allocationsToFree
    }

    private fun FunSpec.Builder.writeBytes(allocation: String, bytes: String) {
        addStatement(
            "%M(store, %L, %L, %L).%M(%S)",
            CHASM_WRITE_BYTES,
            DEFAULT_MEMORY_BACKING_NAME,
            allocation,
            bytes,
            CHASM_EXPECT,
            "Failed to write string",
        )
    }

    private fun FunSpec.Builder.writeArguments(function: Function) {
        if (function.params.isEmpty()) {
            addStatement("val args = emptyList<%T>()", CHASM_EXECUTION_VALUE_CLASS_NAME)
            return
        }

        var inputIndex = 0
        function.params.forEach { param ->
            if (param.type == Scalar.String) {
                val bytes = param.name + "Bytes"
                val allocation = param.name + "Alloc"
                when (requireNotNull(param.stringEncodingStrategy)) {
                    StringEncodingStrategy.POINTER_AND_LENGTH -> {
                        setArgument(inputIndex++, CHASM_I32_VALUE_CLASS_NAME, allocation)
                        setArgument(inputIndex++, CHASM_I32_VALUE_CLASS_NAME, "$bytes.size")
                    }
                    StringEncodingStrategy.NULL_TERMINATED,
                    StringEncodingStrategy.LENGTH_PREFIXED,
                    -> setArgument(inputIndex++, CHASM_I32_VALUE_CLASS_NAME, allocation)
                    StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                        addStatement(
                            "%L[%L] = %T((%L.toLong() shl 32) or (%L.size.toLong() and 0xFFFFFFFFL))",
                            FUNCTION_INPUT_BUFFER_NAME,
                            inputIndex++,
                            CHASM_I64_VALUE_CLASS_NAME,
                            allocation,
                            bytes,
                        )
                    }
                }
            } else {
                setArgument(inputIndex++, param.type.chasmValueClass(), param.name)
            }
        }
        addStatement("val args = %L", functionInputBufferName(function.inputCount))
    }

    private fun FunSpec.Builder.setArgument(index: Int, type: ClassName, value: String) {
        addStatement("%L[%L] = %T(%L)", FUNCTION_INPUT_BUFFER_NAME, index, type, value)
    }

    private fun FunSpec.Builder.writeReturn(
        function: Function,
        returnType: TypeName,
        allocationsToFree: List<String>,
    ) {
        addStatement(
            "val values = %L(args).%M(%S)",
            preparedFunctionPropertyName(function),
            CHASM_EXPECT,
            "Failed to invoke function ${function.name}",
        )
        when (val type = function.returns.type) {
            Scalar.Integer,
            Scalar.Long,
            Scalar.Float,
            Scalar.Double,
            -> {
                addStatement("val result = (values.first() as %T).value", type.chasmValueClass())
                free(allocationsToFree)
                addStatement("return result")
            }
            Scalar.Unit -> free(allocationsToFree)
            Scalar.String -> {
                writeStringReturn(function)
                free(allocationsToFree)
                addStatement("return result")
            }
            is Aggregate -> {
                addCode(
                    CodeBlock.builder()
                        .add("val result = %T(\n", returnType)
                        .indent()
                        .apply {
                            type.generated.fields.forEachIndexed { index, field ->
                                add("r%L = (values[%L] as %T).value,\n", index, index, field.type.chasmValueClass())
                            }
                        }.unindent()
                        .add(")\n")
                        .build(),
                )
                free(allocationsToFree)
                addStatement("return result")
            }
        }
    }

    private fun FunSpec.Builder.writeStringReturn(function: Function) {
        when (requireNotNull(function.returns.stringEncodingStrategy)) {
            StringEncodingStrategy.POINTER_AND_LENGTH -> {
                addStatement("val pointer = (values[0] as %T).value", CHASM_I32_VALUE_CLASS_NAME)
                addStatement("val length = (values[1] as %T).value", CHASM_I32_VALUE_CLASS_NAME)
                addStatement(
                    "val result = %M(store, %L, pointer, length).%M(%S)",
                    CHASM_READ_STRING,
                    DEFAULT_MEMORY_BACKING_NAME,
                    CHASM_EXPECT,
                    "Failed to read string result for ${function.name}",
                )
            }
            StringEncodingStrategy.NULL_TERMINATED -> {
                addStatement("val pointer = (values[0] as %T).value", CHASM_I32_VALUE_CLASS_NAME)
                addStatement(
                    "val result = %M(store, %L, pointer).%M(%S)",
                    CHASM_READ_NULL_STRING,
                    DEFAULT_MEMORY_BACKING_NAME,
                    CHASM_EXPECT,
                    "Failed to read string result for ${function.name}",
                )
            }
            StringEncodingStrategy.LENGTH_PREFIXED -> {
                addStatement("val pointer = (values[0] as %T).value", CHASM_I32_VALUE_CLASS_NAME)
                addStatement(
                    "val length = %M(store, %L, pointer).%M(%S)",
                    CHASM_READ_INT,
                    DEFAULT_MEMORY_BACKING_NAME,
                    CHASM_EXPECT,
                    "Failed to read string length",
                )
                addStatement(
                    "val result = %M(store, %L, pointer + 4, length).%M(%S)",
                    CHASM_READ_STRING,
                    DEFAULT_MEMORY_BACKING_NAME,
                    CHASM_EXPECT,
                    "Failed to read string result for ${function.name}",
                )
            }
            StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                addStatement("val packed = (values[0] as %T).value", CHASM_I64_VALUE_CLASS_NAME)
                addStatement("val pointer = (packed ushr 32).toInt()")
                addStatement("val length = packed.toInt()")
                addStatement(
                    "val result = %M(store, %L, pointer, length).%M(%S)",
                    CHASM_READ_STRING,
                    DEFAULT_MEMORY_BACKING_NAME,
                    CHASM_EXPECT,
                    "Failed to read string result for ${function.name}",
                )
            }
        }
    }

    private fun FunSpec.Builder.free(allocations: List<String>) {
        allocations.forEach { allocation -> addStatement("allocator.free(%L)", allocation) }
    }

    private fun memoryImplementation(
        packageName: String,
        interfaceName: String,
    ): TypeSpec = TypeSpec.classBuilder("MemoryImpl")
        .addModifiers(KModifier.PRIVATE)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("store", CHASM_STORE_CLASS_NAME)
                .addParameter("memory", CHASM_MEMORY_CLASS_NAME)
                .build(),
        ).addSuperinterface(ClassName(packageName, interfaceName, "Memory"))
        .addProperty(PropertySpec.builder("store", CHASM_STORE_CLASS_NAME).addModifiers(KModifier.PRIVATE).initializer("store").build())
        .addProperty(PropertySpec.builder("memory", CHASM_MEMORY_CLASS_NAME).addModifiers(KModifier.PRIVATE).initializer("memory").build())
        .addFunction(
            FunSpec.builder("read")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("buffer", ByteArray::class)
                .addParameter("memoryPointer", INT)
                .addParameter("bufferPointer", INT)
                .addParameter("bytesToRead", INT)
                .returns(ByteArray::class)
                .addStatement(
                    "return %M(store, memory, buffer, memoryPointer, bytesToRead, bufferPointer).%M(%S)",
                    CHASM_READ_BYTES,
                    CHASM_EXPECT,
                    "Failed to read memory",
                ).build(),
        ).addFunction(
            FunSpec.builder("write")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("pointer", INT)
                .addParameter("buffer", ByteArray::class)
                .addParameter("bufferPointer", INT)
                .addParameter("bytesToWrite", INT)
                .addStatement(
                    "%M(store, memory, pointer, buffer, bufferPointer, bytesToWrite).%M(%S)",
                    CHASM_WRITE_BYTES,
                    CHASM_EXPECT,
                    "Failed to write memory",
                ).build(),
        ).build()

    private fun functionReturnType(packageName: String, function: Function): TypeName = when (val type = function.returns.type) {
        Scalar.Integer -> INT
        Scalar.Long -> LONG
        Scalar.Float -> FLOAT
        Scalar.Double -> DOUBLE
        Scalar.String -> STRING
        Scalar.Unit -> UNIT
        is Aggregate -> ClassName(packageName, type.generated.name)
        else -> error("Unsupported return type: $type")
    }

    private fun Type.chasmValueClass(): ClassName = when (this) {
        Scalar.Integer -> CHASM_I32_VALUE_CLASS_NAME
        Scalar.Long -> CHASM_I64_VALUE_CLASS_NAME
        Scalar.Float -> CHASM_F32_VALUE_CLASS_NAME
        Scalar.Double -> CHASM_F64_VALUE_CLASS_NAME
        Scalar.String,
        Scalar.Unit,
        is Aggregate,
        -> error("Cannot convert $this to a Chasm execution value")
        else -> error("Unknown type: $this")
    }

    private fun ImplementationVisibility.modifier(): KModifier = when (this) {
        ImplementationVisibility.INTERNAL -> KModifier.INTERNAL
        ImplementationVisibility.PUBLIC -> KModifier.PUBLIC
        ImplementationVisibility.PRIVATE -> KModifier.PRIVATE
    }
}

private val CHASM_EXECUTION_VALUE_MUTABLE_LIST =
    ClassName("kotlin.collections", "MutableList").parameterizedBy(CHASM_EXECUTION_VALUE_CLASS_NAME)
private val CHASM_WASM32_ALLOCATOR_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.shapes", "Wasm32Allocator")

private val CHASM_PREPARE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "prepareFunction")
private val CHASM_INVOKE = MemberName("io.github.charlietap.chasm.embedding", "invoke")
private val CHASM_READ_GLOBAL = MemberName("io.github.charlietap.chasm.embedding.global", "readGlobal")
private val CHASM_WRITE_GLOBAL = MemberName("io.github.charlietap.chasm.embedding.global", "writeGlobal")
private val CHASM_READ_BYTES = MemberName("io.github.charlietap.chasm.embedding.memory", "readBytes")
private val CHASM_WRITE_BYTES = MemberName("io.github.charlietap.chasm.embedding.memory", "writeBytes")
private val CHASM_READ_INT = MemberName("io.github.charlietap.chasm.embedding.memory", "readInt")
private val CHASM_WRITE_INT = MemberName("io.github.charlietap.chasm.embedding.memory", "writeInt")
private val CHASM_READ_STRING = MemberName("io.github.charlietap.chasm.embedding.memory", "readUtf8String")
private val CHASM_READ_NULL_STRING = MemberName("io.github.charlietap.chasm.embedding.memory", "readNullTerminatedUtf8String")
