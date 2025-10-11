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

internal class PrimaryConstructorGenerator {
    operator fun invoke(
        builder: TypeSpec.Builder,
    ) = builder.apply {
        primaryConstructor(
            FunSpec.constructorBuilder().apply {
                addParameter("binary", ByteArray::class)
                addParameter(
                    ParameterSpec.builder(
                        "imports",
                        CODEGEN_IMPORT_LIST_CLASS_NAME,
                    ).defaultValue(CodeBlock.of("emptyList()")).build(),
                )
                addParameter(
                    ParameterSpec.builder(
                        "virtualMachine",
                        WASM_VIRTUAL_MACHINE_CLASS_NAME,
                    ).defaultValue(CodeBlock.of("%M()", VM_FACTORY_CLASS_NAME)).build(),
                )
                addParameter(
                    ParameterSpec.builder(
                        "moduleFactory",
                        MODULE_FACTORY_CLASS_NAME.copy(true),
                    ).defaultValue("null").build(),
                )
                addParameter(
                    ParameterSpec.builder(
                        "instanceFactory",
                        INSTANCE_FACTORY_CLASS_NAME.copy(true),
                    ).defaultValue("null").build(),
                )
            }.build(),
        )

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
    operator fun invoke(builder: TypeSpec.Builder) = builder.apply {
        primaryConstructorGenerator(builder)
    }
}

private fun TypeSpec.Builder.addConstructor(generator: ConstructorGenerator) = generator(this)

internal class InitializerBlockGenerator() {
    operator fun invoke(
        initializers: Set<String>,
    ): CodeBlock = CodeBlock.builder().apply {
        initializers.forEach { name ->
            addStatement(
                "virtualMachine.%L(store, instance, %S, emptyList()).%M(%S)",
                INVOKE_FUNCTION,
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
    proxy: FunctionProxy,
    returnType: TypeName,
    returnGenerator: FunctionReturnImplementationGenerator,
    freeAllocs: List<String> = emptyList(),
) = returnGenerator(this, function, proxy, returnType, freeAllocs)

internal class FunctionProxyImplementationGenerator(
    private val returnImplementationGenerator: FunctionReturnImplementationGenerator = FunctionReturnImplementationGenerator(),
) {
    operator fun invoke(
        builder: FunSpec.Builder,
        function: Function,
        proxy: FunctionProxy,
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
                    addStatement("virtualMachine.%L(store, memory, %L, %L)", WRITE_BYTES_FUNCTION, allocVar, bytesVar)
                }

                StringEncodingStrategy.NULL_TERMINATED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 1)", allocVar, bytesVar)
                    addStatement("virtualMachine.%L(store, memory, %L, %L)", WRITE_BYTES_FUNCTION, allocVar, bytesVar)
                    addStatement("virtualMachine.%L(store, memory, %L + %L.size, byteArrayOf(0))", WRITE_BYTES_FUNCTION, allocVar, bytesVar)
                }

                StringEncodingStrategy.LENGTH_PREFIXED -> {
                    addStatement("val %L = allocator.alloc(%L.size + 4)", allocVar, bytesVar)
                    addStatement("virtualMachine.%L(store, memory, %L, %L.size)", WRITE_INT_FUNCTION, allocVar, bytesVar)
                    addStatement("virtualMachine.%L(store, memory, %L + 4, %L)", WRITE_BYTES_FUNCTION, allocVar, bytesVar)
                }

                StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> {
                    addStatement("val %L = allocator.alloc(%L.size)", allocVar, bytesVar)
                    addStatement("virtualMachine.%L(store, memory, %L, %L)", WRITE_BYTES_FUNCTION, allocVar, bytesVar)
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

        addReturn(function, proxy, returnType, returnImplementationGenerator, allocationsToFree)
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
                    implementation,
                    returnType,
                )
            }
        }
    }.build()
}

internal class ClassPropertiesGenerator(
    private val propertyImplementationGenerator: PropertyImplementationGenerator = PropertyImplementationGenerator(),
) {
    operator fun invoke(
        name: String,
        wasmInterface: WasmInterface,
    ) = buildList {

        val storeProperty = PropertySpec.builder("store", STORE_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(CodeBlock.of("virtualMachine.%L()", CREATE_STORE_FUNCTION))
            .build()
        val moduleProperty = PropertySpec.builder("module", MODULE_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.of(
                    "moduleFactory?.invoke(binary) ?: virtualMachine.%L(binary).expect(%S)",
                    CREATE_MODULE_FUNCTION,
                    "Failed to decode binary",
                ),
            )
            .build()
        val allocatedImportsProperty = PropertySpec.builder("allocatedImports", IMPORT_LIST_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.of(
                    "virtualMachine.%M(store, imports)",
                    IMPORT_FACTORY_CLASS_NAME,
                ),
            )
            .build()
        val instanceProperty = PropertySpec.builder("instance", INSTANCE_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.of(
                    "instanceFactory?.invoke(store, module, allocatedImports) ?: virtualMachine.%L(store, module, allocatedImports).expect(%S)",
                    CREATE_INSTANCE_FUNCTION,
                    "Failed to instantiate module",
                ),
            )
            .build()

        add(storeProperty)
        add(moduleProperty)
        add(allocatedImportsProperty)
        add(instanceProperty)

        val requiresMemory = wasmInterface.functions.any { fn ->
            fn.params.any { it.type == Scalar.String } || fn.returns.type == Scalar.String
        }
        if (requiresMemory) {
            val memoryProperty = PropertySpec.builder("memory", MEMORY_CLASS_NAME)
                .addModifiers(KModifier.PRIVATE)
                .initializer(
                    CodeBlock.of(
                        "virtualMachine.%L(instance, %S).%M(%S)",
                        EXPORT_MEMORY,
                        "memory",
                        EXPECT_RESULT_FUNCTION,
                        "Failed to find memory export",
                    ),
                ).build()
            add(memoryProperty)
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
    }
}

internal class ClassImplementationGenerator(
    private val constructorGenerator: ConstructorGenerator = ConstructorGenerator(),
    private val initializerBlockGenerator: InitializerBlockGenerator = InitializerBlockGenerator(),
    private val functionImplementationGenerator: FunctionImplementationGenerator = FunctionImplementationGenerator(),
    private val propertiesGenerator: ClassPropertiesGenerator = ClassPropertiesGenerator(),
) {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: TypeVisibility,
        wasmInterface: WasmInterface,
    ): TypeSpec = TypeSpec.classBuilder(interfaceName + "Impl").apply {

        val visibilityModifier = when (visibility) {
            TypeVisibility.INTERNAL -> KModifier.INTERNAL
            TypeVisibility.PUBLIC -> KModifier.PUBLIC
        }
        addModifiers(visibilityModifier)

        addSuperinterface(ClassName(packageName, interfaceName))

        addConstructor(constructorGenerator)

        val properties = propertiesGenerator(interfaceName, wasmInterface)
        properties.forEach { property ->
            addProperty(property)
        }

        if (wasmInterface.initializers.isNotEmpty()) {
            addInitializerBlock(initializerBlockGenerator(wasmInterface.initializers))
        }

        wasmInterface.functions.forEach { function ->
            addFunction(functionImplementationGenerator(packageName, function))
        }
    }.build()
}
