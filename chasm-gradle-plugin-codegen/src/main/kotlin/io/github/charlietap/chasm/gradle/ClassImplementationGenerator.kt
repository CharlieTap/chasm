package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.gradle.ext.asTypeName
import io.github.charlietap.chasm.gradle.ext.asValue
import io.github.charlietap.chasm.vm.Wasm32Allocator
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import io.github.charlietap.chasm.type.NumberType as WasmNumberType
import io.github.charlietap.chasm.type.ValueType as WasmValueType

internal class PrimaryConstructorGenerator {
    operator fun invoke(
        builder: TypeSpec.Builder,
        generateSuspendingFactory: Boolean,
    ) = builder.apply {
        val constructor = FunSpec.constructorBuilder().apply {
            addParameter("binary", ByteArray::class)
            addParameter(
                ParameterSpec.builder(
                    "imports",
                    CODEGEN_IMPORT_LIST_CLASS_NAME,
                ).apply {
                    if (!generateSuspendingFactory) {
                        defaultValue(CodeBlock.of("emptyList()"))
                    }
                }.build(),
            )
            addParameter(
                ParameterSpec.builder(
                    "virtualMachine",
                    WASM_VIRTUAL_MACHINE_CLASS_NAME,
                ).apply {
                    if (!generateSuspendingFactory) {
                        defaultValue(CodeBlock.of("%M()", VM_FACTORY_CLASS_NAME))
                    }
                }.build(),
            )
            addParameter(
                ParameterSpec.builder(
                    "moduleFactory",
                    MODULE_FACTORY_CLASS_NAME.copy(true),
                ).apply {
                    if (!generateSuspendingFactory) {
                        defaultValue("null")
                    }
                }.build(),
            )
            addParameter(
                ParameterSpec.builder(
                    "instanceFactory",
                    INSTANCE_FACTORY_CLASS_NAME.copy(true),
                ).apply {
                    if (!generateSuspendingFactory) {
                        defaultValue("null")
                    }
                }.build(),
            )
            if (generateSuspendingFactory) {
                addModifiers(KModifier.PRIVATE)
                addParameter("runtimeState", ClassName("", "RuntimeState").copy(nullable = true))
            }
        }.build()
        primaryConstructor(constructor)

        if (generateSuspendingFactory) {
            addFunction(
                FunSpec.constructorBuilder()
                    .addParameter("binary", ByteArray::class)
                    .addParameter(
                        ParameterSpec.builder("imports", CODEGEN_IMPORT_LIST_CLASS_NAME)
                            .defaultValue("emptyList()")
                            .build(),
                    ).addParameter(
                        ParameterSpec.builder("virtualMachine", WASM_VIRTUAL_MACHINE_CLASS_NAME)
                            .defaultValue("%M()", VM_FACTORY_CLASS_NAME)
                            .build(),
                    ).addParameter(
                        ParameterSpec.builder("moduleFactory", MODULE_FACTORY_CLASS_NAME.copy(nullable = true))
                            .defaultValue("null")
                            .build(),
                    ).addParameter(
                        ParameterSpec.builder("instanceFactory", INSTANCE_FACTORY_CLASS_NAME.copy(nullable = true))
                            .defaultValue("null")
                            .build(),
                    ).callThisConstructor(
                        "binary",
                        "imports",
                        "virtualMachine",
                        "moduleFactory",
                        "instanceFactory",
                        "null",
                    ).build(),
            )
        }

        addProperty(
            PropertySpec.builder("binary", ByteArray::class)
                .initializer("binary")
                .addModifiers(KModifier.PRIVATE)
                .build(),
        )
        addProperty(
            PropertySpec.builder("imports", CODEGEN_IMPORT_LIST_CLASS_NAME)
                .initializer("imports")
                .addModifiers(KModifier.PRIVATE)
                .build(),
        )
        addProperty(
            PropertySpec.builder(
                "virtualMachine",
                WASM_VIRTUAL_MACHINE_CLASS_NAME,
            ).initializer("virtualMachine").addModifiers(KModifier.PRIVATE).build(),
        )
        addProperty(
            PropertySpec.builder(
                "moduleFactory",
                MODULE_FACTORY_CLASS_NAME.copy(true),
            ).initializer("moduleFactory").addModifiers(KModifier.PRIVATE).build(),
        )
        addProperty(
            PropertySpec.builder(
                "instanceFactory",
                INSTANCE_FACTORY_CLASS_NAME.copy(true),
            ).initializer("instanceFactory").addModifiers(KModifier.PRIVATE).build(),
        )
    }
}

internal class ConstructorGenerator(
    private val primaryConstructorGenerator: PrimaryConstructorGenerator = PrimaryConstructorGenerator(),
) {
    operator fun invoke(
        builder: TypeSpec.Builder,
        generateSuspendingFactory: Boolean,
    ) = builder.apply {
        primaryConstructorGenerator(builder, generateSuspendingFactory)
    }
}

private fun TypeSpec.Builder.addConstructor(
    generator: ConstructorGenerator,
    generateSuspendingFactory: Boolean,
) = generator(this, generateSuspendingFactory)

internal class InitializerBlockGenerator() {
    operator fun invoke(
        initializers: Set<String>,
    ): CodeBlock = CodeBlock.builder().apply {
        initializers.forEach { name ->
            addStatement(
                "virtualMachine.%L(store, instance, %S, emptyList(), emptyList()).%M(%S)",
                INVOKE_TYPED_FUNCTION,
                name,
                EXPECT_RESULT_FUNCTION,
                "Initializer function $name failed",
            )
        }
    }.build()
}

internal class GlobalPropertyGetterImplementationGenerator {
    operator fun invoke(
        proxy: GlobalProxy,
    ) = FunSpec.getterBuilder().apply {
        addStatement(
            """val global = virtualMachine.%L(instance, %S).%M(%S)""",
            EXPORT_GLOBAL,
            proxy.name,
            EXPECT_RESULT_FUNCTION,
            "Failed to find global export with name ${proxy.name}",
        )
        addStatement(
            "return virtualMachine.%L(store, global).%M { (it as %T).value }.%M(%S)",
            READ_GLOBAL_FUNCTION,
            MAP_RESULT_FUNCTION,
            proxy.source,
            EXPECT_RESULT_FUNCTION,
            "Failed to read global ${proxy.name}",
        )
    }.build()
}

internal class GlobalPropertySetterImplementationGenerator {
    operator fun invoke(
        type: Type,
        proxy: GlobalProxy,
    ) = FunSpec.setterBuilder().apply {
        addParameter("newValue", type.asTypeName())
        addStatement(
            """val global = virtualMachine.%L(instance, %S).%M(%S)""",
            EXPORT_GLOBAL,
            proxy.name,
            EXPECT_RESULT_FUNCTION,
            "Failed to find global export with name ${proxy.name}",
        )
        addStatement("virtualMachine.%L(store, global, %T(newValue))", WRITE_GLOBAL_FUNCTION, proxy.source)
    }.build()
}

internal class PropertyImplementationGenerator(
    private val globalPropertyGetter: GlobalPropertyGetterImplementationGenerator = GlobalPropertyGetterImplementationGenerator(),
    private val globalPropertySetter: GlobalPropertySetterImplementationGenerator = GlobalPropertySetterImplementationGenerator(),
) {
    operator fun invoke(
        property: Property,
    ) = PropertySpec.builder(property.name, property.type.asTypeName()).apply {
        addModifiers(KModifier.OVERRIDE)
        mutable(property.const.not())
        when (val implementation = property.implementation) {
            is GlobalProxy -> {
                getter(globalPropertyGetter(implementation))
                if (!property.const) {
                    setter(globalPropertySetter(property.type, implementation))
                }
            }
        }
    }.build()
}

private fun FunSpec.Builder.addReturn(
    function: Function,
    returnType: TypeName,
    returnGenerator: FunctionReturnImplementationGenerator,
    freeAllocs: List<String> = [],
) = returnGenerator(this, function, returnType, freeAllocs)

private fun resultTypesPropertyName(function: Function): String {
    return function.name + "ResultTypes"
}

private fun resultTypesExpression(function: Function): CodeBlock {
    return if (function.resultTypes.isEmpty()) {
        CodeBlock.of("emptyList()")
    } else {
        CodeBlock.of("%L", resultTypesPropertyName(function))
    }
}

internal fun preparedFunctionPropertyName(function: Function): String {
    return function.name + "PreparedFunction"
}

internal class FunctionProxyImplementationGenerator(
    private val returnImplementationGenerator: FunctionReturnImplementationGenerator = FunctionReturnImplementationGenerator(),
) {
    operator fun invoke(
        builder: FunSpec.Builder,
        function: Function,
        returnType: ClassName,
    ) = builder.apply {
        val stringParams = function.params.filter { it.type == Scalar.String }
        val allocationsToFree = mutableListOf<String>()
        stringParams.forEach { param ->
            val bytesVar = param.name + "Bytes"
            val allocVar = param.name + "Alloc"
            addStatement("val %L = %L.encodeToByteArray()", bytesVar, param.name)
            when (requireNotNull(param.stringEncodingStrategy)) {
                StringEncodingStrategy.POINTER_AND_LENGTH -> {
                    addStatement("val %L = allocator.alloc(%L.size)", allocVar, bytesVar)
                    addStatement(
                        "virtualMachine.%L(store, %L, %L, %L)",
                        WRITE_BYTES_FUNCTION,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocVar,
                        bytesVar,
                    )
                }

                StringEncodingStrategy.NULL_TERMINATED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 1)", allocVar, bytesVar)
                    addStatement(
                        "virtualMachine.%L(store, %L, %L, %L)",
                        WRITE_BYTES_FUNCTION,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocVar,
                        bytesVar,
                    )
                    addStatement(
                        "virtualMachine.%L(store, %L, %L + %L.size, byteArrayOf(0))",
                        WRITE_BYTES_FUNCTION,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocVar,
                        bytesVar,
                    )
                }

                StringEncodingStrategy.LENGTH_PREFIXED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 4)", allocVar, bytesVar)
                    addStatement(
                        "virtualMachine.%L(store, %L, %L, %L.size)",
                        WRITE_INT_FUNCTION,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocVar,
                        bytesVar,
                    )
                    addStatement(
                        "virtualMachine.%L(store, %L, %L + 4, %L)",
                        WRITE_BYTES_FUNCTION,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocVar,
                        bytesVar,
                    )
                }

                StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                    addStatement("val %L = allocator.alloc(%L.size)", allocVar, bytesVar)
                    addStatement(
                        "virtualMachine.%L(store, %L, %L, %L)",
                        WRITE_BYTES_FUNCTION,
                        DEFAULT_MEMORY_BACKING_NAME,
                        allocVar,
                        bytesVar,
                    )
                }
            }
            if (param.stringAllocationStrategy?.freeAfterCall == true) {
                allocationsToFree.add(allocVar)
            }
        }

        if (function.params.isEmpty()) {
            addStatement("val args = emptyList<%T>()", WasmVirtualMachine.Value::class)
        } else {
            beginControlFlow("val args = buildList")
            function.params.forEach { param ->
                if (param.type == Scalar.String) {
                    val bytesVar = param.name + "Bytes"
                    val allocVar = param.name + "Alloc"
                    when (requireNotNull(param.stringEncodingStrategy)) {
                        StringEncodingStrategy.POINTER_AND_LENGTH -> {
                            addStatement("add(%T(%L))", WasmVirtualMachine.Value.I32::class, allocVar)
                            addStatement("add(%T(%L.size))", WasmVirtualMachine.Value.I32::class, bytesVar)
                        }

                        StringEncodingStrategy.NULL_TERMINATED -> {
                            addStatement("add(%T(%L))", WasmVirtualMachine.Value.I32::class, allocVar)
                        }

                        StringEncodingStrategy.LENGTH_PREFIXED -> {
                            addStatement("add(%T(%L))", WasmVirtualMachine.Value.I32::class, allocVar)
                        }

                        StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                            addStatement(
                                "add(%T((%L.toLong() shl 32) or (%L.size.toLong() and 0xFFFFFFFFL)))",
                                WasmVirtualMachine.Value.I64::class,
                                allocVar,
                                bytesVar,
                            )
                        }
                    }
                } else {
                    addStatement("add(%T(%L))", param.type.asValue(), param.name)
                }
            }
            endControlFlow()
        }

        addReturn(
            function = function,
            returnType = returnType,
            returnGenerator = returnImplementationGenerator,
            freeAllocs = allocationsToFree,
        )
    }
}

internal class ResultTypePropertiesGenerator {
    operator fun invoke(wasmInterface: WasmInterface): TypeSpec? {
        val functionsWithResults = wasmInterface.functions.filter { it.resultTypes.isNotEmpty() }
        if (functionsWithResults.isEmpty()) {
            return null
        }

        return TypeSpec.companionObjectBuilder().addModifiers(KModifier.PRIVATE).apply {
            functionsWithResults.forEach { function ->
                addProperty(
                    PropertySpec.builder(resultTypesPropertyName(function), VALUE_TYPE_LIST_CLASS_NAME)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer(resultTypesInitializer(function.resultTypes))
                        .build(),
                )
            }
        }.build()
    }

    private fun resultTypesInitializer(types: List<WasmValueType>): CodeBlock {
        return CodeBlock.builder().apply {
            add("listOf(\n")
            indent()
            types.forEach { type ->
                add("%L,\n", resultTypeInitializer(type))
            }
            unindent()
            add(")")
        }.build()
    }

    private fun resultTypeInitializer(type: WasmValueType): CodeBlock {
        return when (type) {
            is WasmValueType.Number -> CodeBlock.of(
                "%T(%T.%L)",
                VALUE_TYPE_NUMBER_CLASS_NAME,
                NUMBER_TYPE_CLASS_NAME,
                type.numberType.asVmNumberTypeName(),
            )
            is WasmValueType.Bottom,
            is WasmValueType.Reference,
            is WasmValueType.Vector,
            -> throw IllegalStateException("Cannot generate VM result type for $type")
        }
    }

    private fun WasmNumberType.asVmNumberTypeName(): String {
        return when (this) {
            WasmNumberType.I32 -> "I32"
            WasmNumberType.I64 -> "I64"
            WasmNumberType.F32 -> "F32"
            WasmNumberType.F64 -> "F64"
        }
    }
}

internal class FunctionImplementationGenerator(
    private val proxyImplementationGenerator: FunctionProxyImplementationGenerator = FunctionProxyImplementationGenerator(),
) {
    operator fun invoke(
        packageName: String,
        function: Function,
    ): FunSpec = FunSpec.builder(function.name).apply {
        addModifiers(KModifier.OVERRIDE)
        val returnType = when (val type = function.returns.type) {
            Scalar.Integer -> INT
            Scalar.Long -> LONG
            Scalar.Float -> FLOAT
            Scalar.Double -> DOUBLE
            Scalar.String -> STRING
            Scalar.Unit -> UNIT
            is Aggregate -> ClassName(packageName, type.generated.name)
            else -> throw IllegalArgumentException("Unsupported return type: $type")
        }
        returns(returnType)

        function.params.forEach { param ->
            addParameter(param.name, param.type.asTypeName())
        }

        when (val implementation = function.implementation) {
            is FunctionProxy -> {
                proxyImplementationGenerator(
                    this,
                    function,
                    returnType,
                )
            }
        }
    }.build()
}

internal class MemoryPropertyImplementationGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        memory: MemoryBinding,
    ): PropertySpec = PropertySpec.builder(
        memory.name,
        ClassName(packageName, interfaceName, "Memory"),
    ).addModifiers(KModifier.OVERRIDE)
        .initializer(
            "%T(store, %N, virtualMachine)",
            ClassName(packageName, interfaceName + "Impl", "MemoryImpl"),
            memory.backingName,
        ).build()
}

internal class MemoryBindingImplementationGenerator {
    operator fun invoke(memory: MemoryBinding): PropertySpec = PropertySpec.builder(
        memory.backingName,
        MEMORY_CLASS_NAME,
    ).addModifiers(KModifier.PRIVATE)
        .initializer(
            "virtualMachine.%L(instance, %S).%M(%S)",
            EXPORT_MEMORY,
            memory.source,
            EXPECT_RESULT_FUNCTION,
            "Failed to find memory export with name ${memory.source}",
        ).build()
}

internal class MemoryImplementationGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
    ): TypeSpec {
        val constructor = FunSpec.constructorBuilder()
            .addParameter("store", STORE_CLASS_NAME)
            .addParameter("memory", MEMORY_CLASS_NAME)
            .addParameter("virtualMachine", WASM_VIRTUAL_MACHINE_CLASS_NAME)
            .build()

        return TypeSpec.classBuilder("MemoryImpl")
            .addModifiers(KModifier.PRIVATE)
            .primaryConstructor(constructor)
            .addSuperinterface(ClassName(packageName, interfaceName, "Memory"))
            .addProperty(
                PropertySpec.builder("store", STORE_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("store")
                    .build(),
            ).addProperty(
                PropertySpec.builder("memory", MEMORY_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("memory")
                    .build(),
            ).addProperty(
                PropertySpec.builder("virtualMachine", WASM_VIRTUAL_MACHINE_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("virtualMachine")
                    .build(),
            ).addFunction(
                FunSpec.builder("read")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("buffer", ByteArray::class)
                    .addParameter("memoryPointer", INT)
                    .addParameter("bufferPointer", INT)
                    .addParameter("bytesToRead", INT)
                    .returns(ByteArray::class)
                    .addStatement(
                        "return virtualMachine.%L(store, memory, memoryPointer, bytesToRead, buffer, bufferPointer).%M(%S)",
                        READ_BYTES_FUNCTION,
                        EXPECT_RESULT_FUNCTION,
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
                        "virtualMachine.%L(store, memory, pointer, buffer, bufferPointer, bytesToWrite).%M(%S)",
                        WRITE_BYTES_FUNCTION,
                        EXPECT_RESULT_FUNCTION,
                        "Failed to write memory",
                    ).build(),
            ).build()
    }
}

internal class ClassPropertiesGenerator(
    private val propertyImplementationGenerator: PropertyImplementationGenerator = PropertyImplementationGenerator(),
    private val memoryBindingImplementationGenerator: MemoryBindingImplementationGenerator =
        MemoryBindingImplementationGenerator(),
    private val memoryPropertyImplementationGenerator: MemoryPropertyImplementationGenerator =
        MemoryPropertyImplementationGenerator(),
) {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        wasmInterface: WasmInterface,
        generateSuspendingFactory: Boolean,
    ) = buildList {

        val createStore = CodeBlock.of("virtualMachine.%L()", CREATE_STORE_FUNCTION)
        val storeProperty = PropertySpec.builder("store", STORE_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(runtimeStateOr("store", createStore, generateSuspendingFactory))
            .build()
        val createModule = CodeBlock.of(
            "moduleFactory?.invoke(binary) ?: virtualMachine.%L(binary).%M(%S)",
            CREATE_MODULE_FUNCTION,
            EXPECT_RESULT_FUNCTION,
            "Failed to decode binary",
        )
        val moduleProperty = PropertySpec.builder("module", MODULE_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(runtimeStateOr("module", createModule, generateSuspendingFactory))
            .build()
        val createImports = CodeBlock.of(
            "virtualMachine.%M(store, imports)",
            IMPORT_FACTORY_CLASS_NAME,
        )
        val allocatedImportsProperty = PropertySpec.builder("allocatedImports", IMPORT_LIST_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(runtimeStateOr("allocatedImports", createImports, generateSuspendingFactory))
            .build()
        val createInstance = CodeBlock.of(
            "instanceFactory?.invoke(store, module, allocatedImports) ?: virtualMachine.%L(store, module, allocatedImports).%M(%S)",
            CREATE_INSTANCE_FUNCTION,
            EXPECT_RESULT_FUNCTION,
            "Failed to instantiate module",
        )
        val instanceProperty = PropertySpec.builder("instance", INSTANCE_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(runtimeStateOr("instance", createInstance, generateSuspendingFactory))
            .build()

        add(storeProperty)
        add(moduleProperty)
        add(allocatedImportsProperty)
        add(instanceProperty)

        wasmInterface.functions.forEach { function ->
            val proxy = function.implementation as FunctionProxy
            add(
                PropertySpec.builder(preparedFunctionPropertyName(function), PREPARED_FUNCTION_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(
                        CodeBlock.of(
                            "virtualMachine.%L(store, instance, %S, %L).%M(%S)",
                            PREPARE_FUNCTION,
                            proxy.name,
                            resultTypesExpression(function),
                            EXPECT_RESULT_FUNCTION,
                            "Failed to prepare function ${proxy.name}",
                        ),
                    ).build(),
            )
        }

        wasmInterface.memories.forEach { memory ->
            add(memoryBindingImplementationGenerator(memory))
        }

        wasmInterface.allocator?.let { allocator ->
            val allocationProperty = PropertySpec.builder("allocator", Wasm32Allocator::class)
                .addModifiers(KModifier.PRIVATE)
                .initializer(
                    CodeBlock.of(
                        "%T(virtualMachine, store, instance, %S, %S)",
                        Wasm32Allocator::class,
                        allocator.allocationFunction,
                        allocator.deallocationFunction,
                    ),
                ).build()
            add(allocationProperty)
        }

        wasmInterface.properties.forEach { property ->
            add(propertyImplementationGenerator(property))
        }

        wasmInterface.memories.filter { it.exposed }.forEach { memory ->
            add(memoryPropertyImplementationGenerator(packageName, interfaceName, memory))
        }
    }

    private fun runtimeStateOr(
        property: String,
        fallback: CodeBlock,
        generateSuspendingFactory: Boolean,
    ): CodeBlock {
        return if (generateSuspendingFactory) {
            CodeBlock.of("runtimeState?.%L ?: %L", property, fallback)
        } else {
            fallback
        }
    }
}

internal class RuntimeStateGenerator {
    operator fun invoke(): TypeSpec {
        val constructor = FunSpec.constructorBuilder()
            .addParameter("store", STORE_CLASS_NAME)
            .addParameter("module", MODULE_CLASS_NAME)
            .addParameter("allocatedImports", IMPORT_LIST_CLASS_NAME)
            .addParameter("instance", INSTANCE_CLASS_NAME)
            .build()

        return TypeSpec.classBuilder("RuntimeState")
            .addModifiers(KModifier.PRIVATE)
            .primaryConstructor(constructor)
            .addProperty(
                PropertySpec.builder("store", STORE_CLASS_NAME)
                    .initializer("store")
                    .build(),
            ).addProperty(
                PropertySpec.builder("module", MODULE_CLASS_NAME)
                    .initializer("module")
                    .build(),
            ).addProperty(
                PropertySpec.builder("allocatedImports", IMPORT_LIST_CLASS_NAME)
                    .initializer("allocatedImports")
                    .build(),
            ).addProperty(
                PropertySpec.builder("instance", INSTANCE_CLASS_NAME)
                    .initializer("instance")
                    .build(),
            ).build()
    }
}

internal class SuspendingFactoryGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
    ): FunSpec = FunSpec.builder("create")
        .addModifiers(KModifier.SUSPEND)
        .addParameter("binary", ByteArray::class)
        .addParameter(
            ParameterSpec.builder("imports", CODEGEN_IMPORT_LIST_CLASS_NAME)
                .defaultValue("emptyList()")
                .build(),
        ).addParameter(
            ParameterSpec.builder("virtualMachine", SUSPENDING_WASM_VIRTUAL_MACHINE_CLASS_NAME)
                .defaultValue("%M()", SUSPENDING_VM_FACTORY_CLASS_NAME)
                .build(),
        ).addParameter(
            ParameterSpec.builder("moduleFactory", MODULE_FACTORY_CLASS_NAME.copy(nullable = true))
                .defaultValue("null")
                .build(),
        ).addParameter(
            ParameterSpec.builder("instanceFactory", INSTANCE_FACTORY_CLASS_NAME.copy(nullable = true))
                .defaultValue("null")
                .build(),
        ).returns(ClassName(packageName, interfaceName + "Impl"))
        .addStatement("val store = virtualMachine.%L()", CREATE_STORE_FUNCTION)
        .addStatement(
            "val module = moduleFactory?.invoke(binary) ?: virtualMachine.%L(binary).%M(%S)",
            CREATE_MODULE_SUSPENDING_FUNCTION,
            EXPECT_RESULT_FUNCTION,
            "Failed to decode binary",
        ).addStatement(
            "val allocatedImports = virtualMachine.%M(store, imports)",
            IMPORT_FACTORY_CLASS_NAME,
        ).addStatement(
            "val instance = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                "virtualMachine.%L(store, module, allocatedImports).%M(%S)",
            CREATE_INSTANCE_SUSPENDING_FUNCTION,
            EXPECT_RESULT_FUNCTION,
            "Failed to instantiate module",
        ).addStatement(
            "return %T(binary, imports, virtualMachine, moduleFactory, instanceFactory, " +
                "RuntimeState(store, module, allocatedImports, instance))",
            ClassName(packageName, interfaceName + "Impl"),
        ).build()
}

internal class ClassImplementationGenerator(
    private val constructorGenerator: ConstructorGenerator = ConstructorGenerator(),
    private val initializerBlockGenerator: InitializerBlockGenerator = InitializerBlockGenerator(),
    private val functionImplementationGenerator: FunctionImplementationGenerator = FunctionImplementationGenerator(),
    private val propertiesGenerator: ClassPropertiesGenerator = ClassPropertiesGenerator(),
    private val resultTypePropertiesGenerator: ResultTypePropertiesGenerator = ResultTypePropertiesGenerator(),
    private val runtimeStateGenerator: RuntimeStateGenerator = RuntimeStateGenerator(),
    private val suspendingFactoryGenerator: SuspendingFactoryGenerator = SuspendingFactoryGenerator(),
    private val memoryImplementationGenerator: MemoryImplementationGenerator = MemoryImplementationGenerator(),
) {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: TypeVisibility,
        wasmInterface: WasmInterface,
        generateSuspendingFactory: Boolean = false,
    ): TypeSpec = TypeSpec.classBuilder(interfaceName + "Impl").apply {

        val visibilityModifier = when (visibility) {
            TypeVisibility.INTERNAL -> KModifier.INTERNAL
            TypeVisibility.PUBLIC -> KModifier.PUBLIC
        }
        addModifiers(visibilityModifier)

        addSuperinterface(ClassName(packageName, interfaceName))

        addConstructor(constructorGenerator, generateSuspendingFactory)

        val properties = propertiesGenerator(packageName, interfaceName, wasmInterface, generateSuspendingFactory)
        properties.forEach { property ->
            addProperty(property)
        }

        if (wasmInterface.initializers.isNotEmpty()) {
            addInitializerBlock(initializerBlockGenerator(wasmInterface.initializers))
        }

        if (wasmInterface.memories.any { it.exposed }) {
            addType(memoryImplementationGenerator(packageName, interfaceName))
        }

        val resultTypeProperties = resultTypePropertiesGenerator(wasmInterface)
        if (generateSuspendingFactory) {
            addType(runtimeStateGenerator())
            addType(
                TypeSpec.companionObjectBuilder().apply {
                    addFunction(suspendingFactoryGenerator(packageName, interfaceName))
                    resultTypeProperties?.propertySpecs?.forEach(::addProperty)
                }.build(),
            )
        } else {
            resultTypeProperties?.let(::addType)
        }

        wasmInterface.functions.forEach { function ->
            addFunction(functionImplementationGenerator(packageName, function))
        }
    }.build()
}
