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
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.gradle.ext.asExecutionValue
import io.github.charlietap.chasm.gradle.ext.asTypeName
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.reflect.KClass

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
                    val argBlocks = function.params.map { param ->
                        CodeBlock.of(
                            "%T(%L)",
                            param.type.asExecutionValue(),
                            param.name,
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
