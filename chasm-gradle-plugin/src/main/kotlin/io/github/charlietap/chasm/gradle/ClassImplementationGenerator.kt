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
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.reflect.KClass

private val LIST_CLASS_NAME = List::class.asClassName()
private val IMPORT_CLASS_NAME = Import::class.asClassName()
private val LIST_IMPORTS_CLASS_NAME = LIST_CLASS_NAME.parameterizedBy(IMPORT_CLASS_NAME)

private val CREATE_INSTANCE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "instance")
private val CREATE_MODULE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "module")
private val CREATE_STORE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "store")
private val INVOKE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "invoke")
private val READ_GLOBAL_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.global", "readGlobal")
private val WRITE_GLOBAL_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.global", "writeGlobal")
private val EXPECT_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
private val READ_STRING_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.memory", "readUtf8String")
private val MAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "map")
private val FLATMAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "flatMap")

internal class PrimaryConstructorGenerator {
    operator fun invoke(
        builder: TypeSpec.Builder,
        importsParameter: ParameterSpec,
        configParameter: ParameterSpec,
    ) = builder.apply {
        primaryConstructor(
            FunSpec.constructorBuilder().apply {
                addParameter("module", Module::class)
                addParameter(importsParameter)
                addParameter(configParameter)
            }.build(),
        )

        addProperty(
            PropertySpec.builder("module", Module::class)
                .initializer("module")
                .addModifiers(KModifier.PRIVATE)
                .build(),
        )
        addProperty(
            PropertySpec.builder(
                "imports",
                LIST_IMPORTS_CLASS_NAME,
            )
                .initializer("imports")
                .addModifiers(KModifier.PRIVATE)
                .build(),
        )
        addProperty(
            PropertySpec.builder("config", Config::class)
                .initializer("config")
                .addModifiers(KModifier.PRIVATE)
                .build(),
        )
    }
}

internal class AlternateConstructorGenerator {
    operator fun invoke(
        parameterName: String,
        parameterType: KClass<*>,
        importsParameter: ParameterSpec,
        configParameter: ParameterSpec,
    ) = FunSpec.constructorBuilder().apply {
        addParameter(parameterName, parameterType)
        addParameter(importsParameter)
        addParameter(configParameter)
        callThisConstructor(
            CodeBlock.of(
                "module = %M(%L, config.moduleConfig).%M(%S), imports = imports, config = config",
                CREATE_MODULE_FUNCTION,
                parameterName,
                EXPECT_RESULT_FUNCTION,
                "Failed to instantiate module",
            ),
        )
    }.build()
}

internal class ConstructorGenerator(
    private val primaryConstructorGenerator: PrimaryConstructorGenerator = PrimaryConstructorGenerator(),
    private val alternateConstructorGenerator: AlternateConstructorGenerator = AlternateConstructorGenerator(),
) {
    operator fun invoke(builder: TypeSpec.Builder) = builder.apply {

        val importsParameter = ParameterSpec.builder("imports", LIST_IMPORTS_CLASS_NAME).apply {
            defaultValue(CodeBlock.of("emptyList()"))
        }.build()

        val configParameter = ParameterSpec.builder("config", Config::class).apply {
            defaultValue(CodeBlock.of("%T()", Config::class))
        }.build()

        primaryConstructorGenerator(builder, importsParameter, configParameter)

        addFunction(alternateConstructorGenerator("binary", ByteArray::class, importsParameter, configParameter))
        addFunction(alternateConstructorGenerator("reader", SourceReader::class, importsParameter, configParameter))
    }
}

private fun TypeSpec.Builder.addConstructor(generator: ConstructorGenerator) = generator(this)

internal class GlobalPropertyGetterImplementationGenerator {
    operator fun invoke(
        proxy: GlobalProxy,
    ) = FunSpec.getterBuilder().apply {
        addStatement(
            """val global = instance.exports.first { it.name == %S }.value as %T""",
            proxy.name,
            Global::class,
        )
        addStatement(
            "return %M(store, global).%M { (it as %T).value }.%M(%S)",
            READ_GLOBAL_FUNCTION,
            MAP_RESULT_FUNCTION,
            proxy.source,
            EXPECT_RESULT_FUNCTION,
            "Failed to read global ${proxy.name}",
        )
    }.build()
}

internal class InitializerBlockGenerator() {
    operator fun invoke(
        initializers: Set<String>,
    ): CodeBlock = CodeBlock.builder().apply {
        initializers.forEach { name ->
            addStatement(
                "%M(store, instance, %S, emptyList()).%M(%S)",
                INVOKE_FUNCTION,
                name,
                EXPECT_RESULT_FUNCTION,
                "Initializer function $name failed",
            )
        }
    }.build()
}

internal class GlobalPropertySetterImplementationGenerator {
    operator fun invoke(
        type: Type,
        proxy: GlobalProxy,
    ) = FunSpec.setterBuilder().apply {
        addParameter("newValue", type.asTypeName())
        addStatement(
            """val global = instance.exports.first { it.name == %S }.value as %T""",
            proxy.name,
            Global::class,
        )
        addStatement("%M(store, global, %T(newValue))", WRITE_GLOBAL_FUNCTION, proxy.source)
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

internal class FunctionReturnImplementationGenerator() {
    operator fun invoke(
        builder: FunSpec.Builder,
        function: Function,
        proxy: FunctionProxy,
        returnType: TypeName,
    ) = builder.apply {
        when (val type = function.returns.type) {
            Scalar.Integer,
            Scalar.Long,
            Scalar.Float,
            Scalar.Double,
            -> {
                addStatement(
                    "return %M(store, instance, %S, args)" +
                        ".%M { (it.first() as %T).value }" +
                        ".%M(%S)",
                    INVOKE_FUNCTION,
                    proxy.name,
                    MAP_RESULT_FUNCTION,
                    function.returns.type.asExecutionValue(),
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
            }
            Scalar.Unit -> {
                addStatement(
                    "%M(store, instance, %S, args).%M(%S)",
                    INVOKE_FUNCTION,
                    proxy.name,
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
            }
            Scalar.String -> {
                addCode(
                    """
                val memory = instance.exports.firstNotNullOf { it.value as? %T }
                return %M(store, instance, %S).%M { (pointer, length) ->
                    %M(store, memory, (pointer as %T).value, (length as %T).value)
                }.expect(%S)
                    """.trimIndent(),
                    Memory::class,
                    INVOKE_FUNCTION,
                    proxy.name,
                    FLATMAP_RESULT_FUNCTION,
                    READ_STRING_FUNCTION,
                    NumberValue.I32::class,
                    NumberValue.I32::class,
                    "Failed to invoke function ${function.name}",
                )
            }
            is Aggregate -> {

                val generatedType = type.generated.fields.mapIndexed { idx, field ->
                    CodeBlock.of("r%L = (it[%L] as %T).value", idx, idx, field.type.asExecutionValue())
                }.joinToCode(",\n")

                addCode(
                    CodeBlock.builder()
                        .addStatement(
                            """
                            return %M(store, instance, %S).%M {
                                %T(
                                    %L
                                )
                            }.expect(%S)
                            """.trimIndent(),
                            INVOKE_FUNCTION,
                            proxy.name,
                            MAP_RESULT_FUNCTION,
                            returnType,
                            generatedType,
                            "Failed to invoke function ${function.name}",
                        )
                        .build(),
                )
            }
        }
    }
}

private fun FunSpec.Builder.addReturn(
    function: Function,
    proxy: FunctionProxy,
    returnType: TypeName,
    returnGenerator: FunctionReturnImplementationGenerator,
) = returnGenerator(this, function, proxy, returnType)

internal class FunctionImplementationGenerator(
    private val returnImplementationGenerator: FunctionReturnImplementationGenerator = FunctionReturnImplementationGenerator(),
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

                if (function.params.isEmpty()) {
                    addStatement("val args = emptyList<%T>()", ExecutionValue::class)
                } else {
                    val argBlocks = function.params.mapIndexed { idx, param ->
                        CodeBlock.of(
                            "%T(p%L)",
                            param.type.asExecutionValue(),
                            idx,
                        )
                    }
                    addCode(
                        argBlocks.joinToCode(
                            separator = ", ",
                            prefix = "val args = listOf(",
                            suffix = ")\n",
                        ),
                    )
                }

                addReturn(function, implementation, returnType, returnImplementationGenerator)
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

        val storeProperty = PropertySpec.builder("store", Store::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer(CodeBlock.of("%M()", CREATE_STORE_FUNCTION))
            .build()
        val instanceProperty = PropertySpec.builder("instance", Instance::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.of(
                    "%M(store, module, imports, config.runtimeConfig).%M(%S)",
                    CREATE_INSTANCE_FUNCTION,
                    EXPECT_RESULT_FUNCTION,
                    "Failed to instantiate module $name",
                ),
            ).build()

        add(storeProperty)
        add(instanceProperty)
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
        name: String,
        wasmInterface: WasmInterface,
    ): TypeSpec = TypeSpec.classBuilder(name + "Impl").apply {

        addSuperinterface(ClassName(packageName, name))

        addConstructor(constructorGenerator)

        val properties = propertiesGenerator(name, wasmInterface)
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
