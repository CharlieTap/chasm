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
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.reflect.KClass

private val LIST_CLASS_NAME = List::class.asClassName()
private val IMPORT_CLASS_NAME = Import::class.asClassName()
private val LIST_IMPORTS_CLASS_NAME = LIST_CLASS_NAME.parameterizedBy(IMPORT_CLASS_NAME)

private val CREATE_MODULE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "module")
private val EXPECT_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")

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

internal class ClassImplementationGenerator(
    private val constructor: ConstructorGenerator = ConstructorGenerator(),
) {
    operator fun invoke(
        packageName: String,
        name: String,
        wasmInterface: WasmInterface,
    ): TypeSpec = TypeSpec.classBuilder(name + "Impl").apply {

        addSuperinterface(ClassName(packageName, name))

        addConstructor(constructor)

    }.build()
}
