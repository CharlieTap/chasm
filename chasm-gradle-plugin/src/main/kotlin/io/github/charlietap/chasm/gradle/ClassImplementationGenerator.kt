package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.reflect.KClass

private val LIST_CLASS_NAME = List::class.asClassName()
private val IMPORT_CLASS_NAME = Import::class.asClassName()
private val LIST_IMPORTS_CLASS_NAME = LIST_CLASS_NAME.parameterizedBy(IMPORT_CLASS_NAME)

private val CREATE_INSTANCE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "instance")
private val CREATE_MODULE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "module")
private val CREATE_STORE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "store")
private val READ_GLOBAL_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.global", "readGlobal")
private val WRITE_GLOBAL_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.global", "writeGlobal")
private val EXPECT_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
private val MAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "map")

internal class PrimaryConstructorGenerator
{
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

internal class AlternateConstructorGenerator
{
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
    private val primaryConstructorGenerator: PrimaryConstructorGenerator= PrimaryConstructorGenerator(),
    private val alternateConstructorGenerator: AlternateConstructorGenerator= AlternateConstructorGenerator(),
) {
    operator fun invoke(builder: TypeSpec.Builder) = builder.apply {

        val importsParameter = ParameterSpec.builder("imports", LIST_IMPORTS_CLASS_NAME,).apply {
            defaultValue(CodeBlock.of("emptyList()"))
        }.build()

        val configParameter = ParameterSpec.builder("config", Config::class,).apply {
            defaultValue(CodeBlock.of("%T()", Config::class))
        }.build()

        primaryConstructorGenerator(builder, importsParameter, configParameter)

        addFunction(alternateConstructorGenerator("binary", ByteArray::class, importsParameter, configParameter))
        addFunction(alternateConstructorGenerator("reader", SourceReader::class, importsParameter, configParameter))
    }
}

private fun TypeSpec.Builder.addConstructor(generator: ConstructorGenerator) = generator(this)

internal class GlobalPropertyGetterImplementationGenerator
{
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
            "Failed to read global",
        )
    }.build()
}

internal class GlobalPropertySetterImplementationGenerator
{
    operator fun invoke(
        type: Type,
        proxy: GlobalProxy,
    ) = FunSpec.setterBuilder().apply {
        addParameter("value", type.asTypeName())
        addStatement(
            """val global = instance.exports.first { it.name == %S }.value as %T""",
            proxy.name,
            Global::class,
        )
        addStatement("%M(store, global, %T(value))", WRITE_GLOBAL_FUNCTION, proxy.source)
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
        when(val implementation = property.implementation) {
            is GlobalProxy -> {
                getter(globalPropertyGetter(implementation))
                if(!property.const) {
                    setter(globalPropertySetter(property.type, implementation))
                }
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
                )
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

    }.build()
}
