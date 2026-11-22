package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.bindgen.ext.asTypeName

internal class ChasmClassImplementationGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: ImplementationVisibility,
        wasmInterface: WasmInterface,
        controls: List<StoreControl>,
        api: EmbeddingApi,
    ): TypeSpec {
        return TypeSpec.classBuilder(interfaceName + "Impl").apply {

            addModifiers(visibility.modifier())
            addSuperinterface(ClassName(packageName, interfaceName))
            addConstructor(api)
            addPreparedFunctions(wasmInterface, api)
            addProperties(api.functionCallConvention.properties(wasmInterface.functions))
            addBindings(packageName, interfaceName, wasmInterface, api)

            if (wasmInterface.initializers.isNotEmpty()) {
                addInitializerBlock(initializerBlock(wasmInterface.initializers, api))
            }
            if (wasmInterface.memories.any { memory -> memory.exposed }) {
                addType(memoryImplementation(packageName, interfaceName, api))
            }
            wasmInterface.functions.forEach { function ->
                addFunction(functionImplementation(packageName, function, api))
            }
            controls.forEach { control -> addFunction(storeControlImplementation(control, api)) }
        }.build()
    }

    private fun storeControlImplementation(
        control: StoreControl,
        api: EmbeddingApi,
    ): FunSpec {
        val arguments = listOf(CodeBlock.of("store")) + control.parameters.map { parameter -> CodeBlock.of("%N", parameter) }
        val call = CodeBlock.of("%N(%L)", control.function.alias, arguments.joinToCode())
        return control.signature()
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("return %L", call)
            .build()
    }

    private fun TypeSpec.Builder.addConstructor(api: EmbeddingApi) {
        primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("store", api.store)
                .addParameter("instance", api.instance)
                .build(),
        )
        addProperty(
            PropertySpec.builder("store", api.store)
                .addModifiers(KModifier.PRIVATE)
                .initializer("store")
                .build(),
        )
        addProperty(
            PropertySpec.builder("instance", api.instance)
                .addModifiers(KModifier.PRIVATE)
                .initializer("instance")
                .build(),
        )
    }

    private fun TypeSpec.Builder.addPreparedFunctions(
        wasmInterface: WasmInterface,
        api: EmbeddingApi,
    ) {
        wasmInterface.functions.forEach { function ->
            val proxy = function.implementation as FunctionProxy
            addProperty(
                PropertySpec.builder(preparedFunctionPropertyName(function), api.preparedFunction)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(
                        "%M(instance, %S).%M(%S)",
                        api.prepareFunction,
                        proxy.name,
                        CHASM_EXPECT,
                        "Failed to prepare function ${proxy.name}",
                    ).build(),
            )
        }
    }

    private fun TypeSpec.Builder.addBindings(
        packageName: String,
        interfaceName: String,
        wasmInterface: WasmInterface,
        api: EmbeddingApi,
    ) {
        wasmInterface.memories.forEach { memory ->
            addProperty(importableProperty(memory.backingName, memory.source, api.memory, ExportKind.Memory, api))
        }

        wasmInterface.allocator?.let { allocator ->
            addProperty(
                PropertySpec.builder("allocator", api.allocator)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(
                        "%T(instance, %S, %S)",
                        api.allocator,
                        allocator.allocationFunction,
                        allocator.deallocationFunction,
                    ).build(),
            )
        }

        wasmInterface.properties.forEach { property ->
            val proxy = property.implementation as GlobalProxy
            addProperty(importableProperty("_${property.name}", proxy.name, api.global, ExportKind.Global, api))
            addProperty(globalProperty(property, api))
        }

        wasmInterface.memories.filter { memory -> memory.exposed }.forEach { memory ->
            addProperty(
                PropertySpec.builder(memory.name, ClassName(packageName, interfaceName, "Memory"))
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%T(%N)", ClassName(packageName, interfaceName + "Impl", "MemoryImpl"), memory.backingName)
                    .build(),
            )
        }
    }

    private fun importableProperty(
        propertyName: String,
        exportName: String,
        type: ClassName,
        kind: ExportKind,
        api: EmbeddingApi,
    ): PropertySpec {
        return PropertySpec.builder(propertyName, type)
            .addModifiers(KModifier.PRIVATE)
            .initializer(api.exportLookup(kind, exportName))
            .build()
    }

    private fun globalProperty(
        property: Property,
        api: EmbeddingApi,
    ): PropertySpec {
        return PropertySpec.builder(property.name, property.type.asTypeName()).apply {

            addModifiers(KModifier.OVERRIDE)
            mutable(property.const.not())
            getter(
                FunSpec.getterBuilder()
                    .addStatement("return %L", api.globalRead("_${property.name}", property.type))
                    .build(),
            )

            if (!property.const) {
                setter(
                    FunSpec.setterBuilder()
                        .addParameter("newValue", property.type.asTypeName())
                        .addStatement("%L", api.globalWrite("_${property.name}", property.type, "newValue"))
                        .build(),
                )
            }
        }.build()
    }

    private fun initializerBlock(
        initializers: Set<String>,
        api: EmbeddingApi,
    ): CodeBlock {
        return CodeBlock.builder().apply {
            initializers.forEach { name ->
                addStatement(
                    "%M(instance, %S).%M(%S)",
                    api.invoke,
                    name,
                    CHASM_EXPECT,
                    "Initializer function $name failed",
                )
            }
        }.build()
    }

    private fun functionImplementation(
        packageName: String,
        function: Function,
        api: EmbeddingApi,
    ): FunSpec {
        return FunSpec.builder(function.name).apply {

            addModifiers(KModifier.OVERRIDE)
            val returnType = functionReturnType(packageName, function)
            returns(returnType)
            function.params.forEach { param -> addParameter(param.name, param.type.asTypeName()) }

            val allocationsToFree = writeStringParameters(function, api)
            val referenceRelease = api.functionCallConvention.referenceRelease(function)

            if (referenceRelease.isEmpty()) {
                writeCall(function, returnType, allocationsToFree, api)
            } else {
                beginControlFlow("try")
                writeCall(function, returnType, allocationsToFree, api)
                nextControlFlow("finally")
                addCode(referenceRelease)
                endControlFlow()
            }
        }.build()
    }

    private fun FunSpec.Builder.writeCall(
        function: Function,
        returnType: TypeName,
        allocationsToFree: List<String>,
        api: EmbeddingApi,
    ) {
        writeArguments(function, api.functionCallConvention)
        writeReturn(function, returnType, allocationsToFree, api)
    }

    private fun FunSpec.Builder.writeStringParameters(
        function: Function,
        api: EmbeddingApi,
    ): List<String> {
        val allocationsToFree = mutableListOf<String>()
        val linearMemoryStringParams = function.params.filter { param ->
            param.stringEncoding is LinearMemoryString
        }
        linearMemoryStringParams.forEach { param ->
            val bytes = param.name + "Bytes"
            val allocation = param.name + "Alloc"
            addStatement("val %L = %L.encodeToByteArray()", bytes, param.name)
            when (requireNotNull(param.linearMemoryStrategy)) {
                StringEncodingStrategy.POINTER_AND_LENGTH,
                StringEncodingStrategy.PACKED_POINTER_AND_LENGTH,
                -> {
                    addStatement("val %L = allocator.alloc(%L.size)", allocation, bytes)
                    writeBytes(allocation, bytes, api)
                }
                StringEncodingStrategy.NULL_TERMINATED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 1)", allocation, bytes)
                    writeBytes(allocation, bytes, api)
                    addStatement(
                        "%L",
                        api.writeNullTerminator(DEFAULT_MEMORY_BACKING_NAME, CodeBlock.of("%L + %L.size", allocation, bytes)),
                    )
                }
                StringEncodingStrategy.LENGTH_PREFIXED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 4)", allocation, bytes)
                    addStatement("%M(%L, %L, %L.size)", api.writeInt, DEFAULT_MEMORY_BACKING_NAME, allocation, bytes)
                    addStatement("%M(%L, %L + 4, %L)", api.writeBytes, DEFAULT_MEMORY_BACKING_NAME, allocation, bytes)
                }
                StringEncodingStrategy.GC_UTF16_ARRAY,
                StringEncodingStrategy.GC_UTF8_ARRAY,
                -> error("GC array strings are not written to linear memory")
            }
            if (param.stringAllocationStrategy?.freeAfterCall == true) allocationsToFree.add(allocation)
        }
        return allocationsToFree
    }

    private fun FunSpec.Builder.writeBytes(
        allocation: String,
        bytes: String,
        api: EmbeddingApi,
    ) {
        addStatement("%M(%L, %L, %L)", api.writeBytes, DEFAULT_MEMORY_BACKING_NAME, allocation, bytes)
    }

    private fun FunSpec.Builder.writeArguments(
        function: Function,
        callConvention: FunctionCallConvention,
    ) {
        var inputIndex = 0
        function.params.forEach { param ->
            when (val encoding = param.stringEncoding) {
                is LinearMemoryString -> {
                    val bytes = param.name + "Bytes"
                    val allocation = param.name + "Alloc"
                    when (encoding.strategy) {
                        StringEncodingStrategy.POINTER_AND_LENGTH -> {
                            addCode(callConvention.argumentAssignment(function, inputIndex++, Scalar.Integer, CodeBlock.of("%L", allocation)))
                            addCode(callConvention.argumentAssignment(function, inputIndex++, Scalar.Integer, CodeBlock.of("%L.size", bytes)))
                        }
                        StringEncodingStrategy.NULL_TERMINATED,
                        StringEncodingStrategy.LENGTH_PREFIXED,
                        -> addCode(callConvention.argumentAssignment(function, inputIndex++, Scalar.Integer, CodeBlock.of("%L", allocation)))
                        StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                            val packed = CodeBlock.of("(%L.toLong() shl 32) or (%L.size.toLong() and 0xFFFFFFFFL)", allocation, bytes)
                            addCode(callConvention.argumentAssignment(function, inputIndex++, Scalar.Long, packed))
                        }
                        StringEncodingStrategy.GC_UTF16_ARRAY,
                        StringEncodingStrategy.GC_UTF8_ARRAY,
                        -> error("GC array strings are not written to linear memory")
                    }
                }
                is GcArrayString -> addCode(callConvention.stringArrayArgumentAssignment(function, inputIndex++, param.name, encoding))
                null -> addCode(callConvention.argumentAssignment(function, inputIndex++, param.type, CodeBlock.of("%L", param.name)))
            }
        }
    }

    private fun FunSpec.Builder.writeReturn(
        function: Function,
        returnType: TypeName,
        allocationsToFree: List<String>,
        api: EmbeddingApi,
    ) {

        val callConvention = api.functionCallConvention

        addCode(callConvention.invocation(function))

        when (val type = function.returns.type) {
            Scalar.Integer,
            Scalar.Long,
            Scalar.Float,
            Scalar.Double,
            is StructViewType,
            is ArrayReferenceType,
            is AnyReferenceType,
            -> addStatement("val result = %L", callConvention.singleResult(function, type))
            Scalar.Unit -> Unit
            Scalar.String -> writeStringReturn(function, api)
            is Aggregate -> {
                addCode(
                    CodeBlock.builder()
                        .add("val result = %T(\n", returnType)
                        .indent()
                        .apply {
                            type.generated.fields.forEachIndexed { index, field ->
                                add("r%L = %L,\n", index, callConvention.result(function, index, field.type))
                            }
                        }.unindent()
                        .add(")\n")
                        .build(),
                )
            }
        }

        free(allocationsToFree)

        if (function.returns.type != Scalar.Unit) {
            addStatement("return result")
        }
    }

    private fun FunSpec.Builder.writeStringReturn(
        function: Function,
        api: EmbeddingApi,
    ) {

        val callConvention = api.functionCallConvention
        val pointerResult = callConvention.result(function, 0, Scalar.Integer)

        when (val encoding = requireNotNull(function.returns.stringEncodingStrategy)) {
            StringEncodingStrategy.POINTER_AND_LENGTH -> {
                addStatement("val pointer = %L", pointerResult)
                addStatement("val length = %L", callConvention.result(function, 1, Scalar.Integer))
                addStatement("val result = %M(%L, pointer, length)", api.readUtf8String, DEFAULT_MEMORY_BACKING_NAME)
            }
            StringEncodingStrategy.NULL_TERMINATED -> {
                addStatement("val pointer = %L", pointerResult)
                addStatement("val result = %M(%L, pointer)", api.readNullTerminatedUtf8String, DEFAULT_MEMORY_BACKING_NAME)
            }
            StringEncodingStrategy.LENGTH_PREFIXED -> {
                addStatement("val pointer = %L", pointerResult)
                addStatement("val length = %M(%L, pointer)", api.readInt, DEFAULT_MEMORY_BACKING_NAME)
                addStatement("val result = %M(%L, pointer + 4, length)", api.readUtf8String, DEFAULT_MEMORY_BACKING_NAME)
            }
            StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                addStatement("val packed = %L", callConvention.result(function, 0, Scalar.Long))
                addStatement("val pointer = (packed ushr 32).toInt()")
                addStatement("val length = packed.toInt()")
                addStatement("val result = %M(%L, pointer, length)", api.readUtf8String, DEFAULT_MEMORY_BACKING_NAME)
            }
            StringEncodingStrategy.GC_UTF16_ARRAY,
            StringEncodingStrategy.GC_UTF8_ARRAY,
            -> addStatement("val result = %L", callConvention.stringArrayResult(function, encoding))
        }
    }

    private fun FunSpec.Builder.free(allocations: List<String>) {
        allocations.forEach { allocation -> addStatement("allocator.free(%L)", allocation) }
    }

    private fun memoryImplementation(
        packageName: String,
        interfaceName: String,
        api: EmbeddingApi,
    ): TypeSpec {
        return TypeSpec.classBuilder("MemoryImpl").apply {

            addModifiers(KModifier.PRIVATE)
            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("memory", api.memory)
                    .build(),
            )
            addSuperinterface(ClassName(packageName, interfaceName, "Memory"))
            addProperty(PropertySpec.builder("memory", api.memory).addModifiers(KModifier.PRIVATE).initializer("memory").build())

            addFunction(
                FunSpec.builder("read")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("buffer", ByteArray::class)
                    .addParameter("memoryPointer", INT)
                    .addParameter("bufferPointer", INT)
                    .addParameter("bytesToRead", INT)
                    .returns(ByteArray::class)
                    .addStatement("return %M(memory, buffer, memoryPointer, bytesToRead, bufferPointer)", api.readBytes)
                    .build(),
            )
            addFunction(
                FunSpec.builder("write")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("pointer", INT)
                    .addParameter("buffer", ByteArray::class)
                    .addParameter("bufferPointer", INT)
                    .addParameter("bytesToWrite", INT)
                    .addStatement("%M(memory, pointer, buffer, bufferPointer, bytesToWrite)", api.writeBytes)
                    .build(),
            )
        }.build()
    }

    private fun functionReturnType(
        packageName: String,
        function: Function,
    ): TypeName {
        return when (val type = function.returns.type) {
            Scalar.Integer -> INT
            Scalar.Long -> LONG
            Scalar.Float -> FLOAT
            Scalar.Double -> DOUBLE
            Scalar.String -> STRING
            Scalar.Unit -> UNIT
            is Aggregate -> ClassName(packageName, type.generated.name)
            is StructViewType,
            is ArrayReferenceType,
            is AnyReferenceType,
            -> type.asTypeName()
            else -> error("Unsupported return type: $type")
        }
    }

    private fun ImplementationVisibility.modifier(): KModifier {
        return when (this) {
            ImplementationVisibility.INTERNAL -> KModifier.INTERNAL
            ImplementationVisibility.PUBLIC -> KModifier.PUBLIC
            ImplementationVisibility.PRIVATE -> KModifier.PRIVATE
        }
    }
}
