package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.gradle.ext.asExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue

internal class PointerAndLengthStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            """
        val memory = instance.exports.firstNotNullOf { it.value as? %T }
        return %M(store, instance, %S, args).%M { (pointer, length) ->
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
    }.build()
}

internal class NullTerminatedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            """
        val memory = instance.exports.firstNotNullOf { it.value as? %T }
        return %M(store, instance, %S, args).%M { (pointer) ->
            %M(store, memory, (pointer as %T).value)
        }.expect(%S)
            """.trimIndent(),
            Memory::class,
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            READ_NULL_STRING_FUNCTION,
            NumberValue.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class LengthPrefixedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            """
        val memory = instance.exports.firstNotNullOf { it.value as? %T }
        return %M(store, instance, %S, args).%M { (pointer) ->
            val length = %M(store, memory, (pointer as %T).value).%M(%S)
            %M(store, memory, (pointer as %T).value + 4, length)
        }.expect(%S)
            """.trimIndent(),
            Memory::class,
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            READ_INT_FUNCTION,
            NumberValue.I32::class,
            EXPECT_RESULT_FUNCTION,
            "Failed to read string length",
            READ_STRING_FUNCTION,
            NumberValue.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class PackedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            """
        val memory = instance.exports.firstNotNullOf { it.value as? %T }
        return %M(store, instance, %S, args).%M { (pointerAndLength) ->
            val packed = (pointerAndLength as %T).value
            val pointer = (packed ushr 32).toInt()
            val length = packed.toInt()
            %M(store, memory, pointer, length)
        }.expect(%S)
            """.trimIndent(),
            Memory::class,
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            NumberValue.I64::class,
            READ_STRING_FUNCTION,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class StringFunctionReturnImplementationGenerator(
    private val pointerAndLengthStrategy: PointerAndLengthStringFunctionReturnGenerator = PointerAndLengthStringFunctionReturnGenerator(),
    private val nullTerminatedStrategy: NullTerminatedStringFunctionReturnGenerator = NullTerminatedStringFunctionReturnGenerator(),
    private val lengthPrefixedStrategy: LengthPrefixedStringFunctionReturnGenerator = LengthPrefixedStringFunctionReturnGenerator(),
    private val packedStringStrategy: PackedStringFunctionReturnGenerator = PackedStringFunctionReturnGenerator(),
) {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ): CodeBlock = when (requireNotNull(function.returns.stringEncodingStrategy)) {
        StringEncodingStrategy.POINTER_AND_LENGTH -> pointerAndLengthStrategy(function, proxy)
        StringEncodingStrategy.NULL_TERMINATED -> nullTerminatedStrategy(function, proxy)
        StringEncodingStrategy.LENGTH_PREFIXED -> lengthPrefixedStrategy(function, proxy)
        StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> packedStringStrategy(function, proxy)
    }
}

internal class FunctionReturnImplementationGenerator(
    private val stringReturnGenerator: StringFunctionReturnImplementationGenerator = StringFunctionReturnImplementationGenerator(),
) {
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
                val code = stringReturnGenerator(function, proxy)
                addCode(code)
            }

            is Aggregate -> {

                val generatedType = type.generated.fields.mapIndexed { idx, field ->
                    CodeBlock.of("r%L = (it[%L] as %T).value", idx, idx, field.type.asExecutionValue())
                }.joinToCode(",\n")

                addCode(
                    CodeBlock.builder()
                        .addStatement(
                            """
                            return %M(store, instance, %S, args).%M {
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
