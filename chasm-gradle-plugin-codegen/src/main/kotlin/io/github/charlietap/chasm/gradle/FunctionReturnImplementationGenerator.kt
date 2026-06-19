package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.gradle.ext.asValue
import io.github.charlietap.chasm.vm.WasmVirtualMachine

internal class PointerAndLengthStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
        resultTypes: CodeBlock,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args, %L).%M { (pointer, length) ->\n" +
                "    virtualMachine.%L(store, memory, (pointer as %T).value, (length as %T).value)\n" +
                "}.expect(%S)",
            INVOKE_TYPED_FUNCTION,
            proxy.name,
            resultTypes,
            FLATMAP_RESULT_FUNCTION,
            READ_STRING_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            WasmVirtualMachine.Value.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class NullTerminatedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
        resultTypes: CodeBlock,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args, %L).%M { (pointer) ->\n" +
                "    virtualMachine.%L(store, memory, (pointer as %T).value)\n" +
                "}.expect(%S)",
            INVOKE_TYPED_FUNCTION,
            proxy.name,
            resultTypes,
            FLATMAP_RESULT_FUNCTION,
            READ_NULL_STRING_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class LengthPrefixedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
        resultTypes: CodeBlock,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args, %L).%M { (pointer) ->\n" +
                "    val length = virtualMachine.%L(store, memory, (pointer as %T).value).%M(%S)\n" +
                "    virtualMachine.%L(store, memory, (pointer as %T).value + 4, length)\n" +
                "}.expect(%S)",
            INVOKE_TYPED_FUNCTION,
            proxy.name,
            resultTypes,
            FLATMAP_RESULT_FUNCTION,
            READ_INT_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            EXPECT_RESULT_FUNCTION,
            "Failed to read string length",
            READ_STRING_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class PackedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
        resultTypes: CodeBlock,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args, %L).%M { (pointerAndLength) ->\n" +
                "    val packed = (pointerAndLength as %T).value\n" +
                "    val pointer = (packed ushr 32).toInt()\n" +
                "    val length = packed.toInt()\n" +
                "    virtualMachine.%L(store, memory, pointer, length)\n" +
                "}.expect(%S)",
            INVOKE_TYPED_FUNCTION,
            proxy.name,
            resultTypes,
            FLATMAP_RESULT_FUNCTION,
            WasmVirtualMachine.Value.I64::class,
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
        resultTypes: CodeBlock,
    ): CodeBlock = when (requireNotNull(function.returns.stringEncodingStrategy)) {
        StringEncodingStrategy.POINTER_AND_LENGTH -> pointerAndLengthStrategy(function, proxy, resultTypes)
        StringEncodingStrategy.NULL_TERMINATED -> nullTerminatedStrategy(function, proxy, resultTypes)
        StringEncodingStrategy.LENGTH_PREFIXED -> lengthPrefixedStrategy(function, proxy, resultTypes)
        StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> packedStringStrategy(function, proxy, resultTypes)
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
        resultTypes: CodeBlock,
        freeAllocs: List<String>,
    ) = builder.apply {
        when (val type = function.returns.type) {
            Scalar.Integer,
            Scalar.Long,
            Scalar.Float,
            Scalar.Double,
            -> {
                val expectFirstMember = when (type) {
                    Scalar.Integer -> EXPECT_FIRST_INT_FUNCTION
                    Scalar.Long -> EXPECT_FIRST_LONG_FUNCTION
                    Scalar.Float -> EXPECT_FIRST_FLOAT_FUNCTION
                    Scalar.Double -> EXPECT_FIRST_DOUBLE_FUNCTION
                    else -> EXPECT_RESULT_FUNCTION
                }
                addStatement(
                    "val result = virtualMachine.%L(store, instance, %S, args, %L).%M(%S)",
                    INVOKE_TYPED_FUNCTION,
                    proxy.name,
                    resultTypes,
                    expectFirstMember,
                    "Failed to invoke function ${function.name}",
                )
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
                addStatement("return result")
            }

            Scalar.Unit -> {
                addStatement(
                    "virtualMachine.%L(store, instance, %S, args, %L).%M(%S)",
                    INVOKE_TYPED_FUNCTION,
                    proxy.name,
                    resultTypes,
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
            }

            Scalar.String -> {
                val expr = stringReturnGenerator(function, proxy, resultTypes)
                addStatement("val result = %L", expr)
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
                addStatement("return result")
            }

            is Aggregate -> {
                val generatedType = type.generated.fields.mapIndexed { idx, field ->
                    CodeBlock.of("r%L = (it[%L] as %T).value", idx, idx, field.type.asValue())
                }.joinToCode(",\n")

                addStatement(
                    """
                    val result = virtualMachine.%L(store, instance, %S, args, %L).%M {
                        %T(
                            %L
                        )
                    }.%M(%S)
                    """.trimIndent(),
                    INVOKE_TYPED_FUNCTION,
                    proxy.name,
                    resultTypes,
                    MAP_RESULT_FUNCTION,
                    returnType,
                    generatedType,
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
                addStatement("return result")
            }
        }
    }
}
