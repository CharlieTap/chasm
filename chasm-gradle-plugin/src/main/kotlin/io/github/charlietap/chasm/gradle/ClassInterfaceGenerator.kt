package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.gradle.ext.asTypeName

internal class FunctionGenerator {
    operator fun invoke(
        packageName: String,
        function: Function,
    ) = FunSpec.builder(function.name).apply {
        addModifiers(KModifier.ABSTRACT)
        function.params.forEach { param ->
            addParameter(param.name, param.type.asTypeName())
        }
        val type = when (val type = function.returns.type) {
            Scalar.Integer -> INT
            Scalar.Long -> LONG
            Scalar.Float -> FLOAT
            Scalar.Double -> DOUBLE
            Scalar.String -> STRING
            Scalar.Unit -> UNIT
            is Aggregate -> ClassName(packageName, type.generated.name)
            else -> throw IllegalArgumentException("Unsupported return type: $type")
        }
        returns(type)
    }.build()
}

internal class PropertyGenerator {
    operator fun invoke(
        property: Property,
    ) = PropertySpec.builder(property.name, property.type.asTypeName()).apply {
        mutable(property.const.not())
    }.build()
}

internal class MemoryInterfaceGenerator {
    operator fun invoke(): TypeSpec = TypeSpec.interfaceBuilder("Memory")
        .addFunction(
            FunSpec.builder("read")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter("buffer", ByteArray::class)
                .addParameter("memoryPointer", INT)
                .addParameter(
                    ParameterSpec.builder("bufferPointer", INT)
                        .defaultValue("0")
                        .build(),
                ).addParameter(
                    ParameterSpec.builder("bytesToRead", INT)
                        .defaultValue("buffer.size - bufferPointer")
                        .build(),
                ).returns(ByteArray::class)
                .build(),
        ).addFunction(
            FunSpec.builder("write")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter("pointer", INT)
                .addParameter("buffer", ByteArray::class)
                .addParameter(
                    ParameterSpec.builder("bufferPointer", INT)
                        .defaultValue("0")
                        .build(),
                ).addParameter(
                    ParameterSpec.builder("bytesToWrite", INT)
                        .defaultValue("buffer.size - bufferPointer")
                        .build(),
                ).returns(UNIT)
                .build(),
        ).build()
}

internal class MemoryPropertyGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        memory: MemoryBinding,
    ): PropertySpec = PropertySpec.builder(
        memory.name,
        ClassName(packageName, interfaceName, "Memory"),
    ).build()
}

internal class ClassInterfaceGenerator(
    private val functionGenerator: FunctionGenerator = FunctionGenerator(),
    private val propertyGenerator: PropertyGenerator = PropertyGenerator(),
    private val memoryInterfaceGenerator: MemoryInterfaceGenerator = MemoryInterfaceGenerator(),
    private val memoryPropertyGenerator: MemoryPropertyGenerator = MemoryPropertyGenerator(),
) {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: TypeVisibility,
        wasmInterface: WasmInterface,
    ) = TypeSpec.interfaceBuilder(interfaceName).apply {

        val visibilityModifier = when (visibility) {
            TypeVisibility.INTERNAL -> KModifier.INTERNAL
            TypeVisibility.PUBLIC -> KModifier.PUBLIC
        }
        addModifiers(visibilityModifier)

        val exposedMemories = wasmInterface.memories.filter { it.exposed }
        if (exposedMemories.isNotEmpty()) {
            addType(memoryInterfaceGenerator())
        }

        wasmInterface.functions.forEach { function ->
            addFunction(functionGenerator(packageName, function))
        }

        wasmInterface.properties.forEach { property ->
            addProperty(propertyGenerator(property))
        }

        exposedMemories.forEach { memory ->
            addProperty(memoryPropertyGenerator(packageName, interfaceName, memory))
        }
    }.build()
}
