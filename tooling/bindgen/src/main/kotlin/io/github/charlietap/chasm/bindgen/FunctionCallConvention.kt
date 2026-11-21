package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG_ARRAY
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ValueType

internal interface FunctionCallConvention {

    val passesReferences: Boolean

    fun reservedNames(functions: List<Function>): List<String>

    fun properties(functions: List<Function>): List<PropertySpec>

    fun argumentAssignment(
        function: Function,
        index: Int,
        type: Type,
        value: CodeBlock,
    ): CodeBlock

    fun stringArrayArgumentAssignment(
        function: Function,
        index: Int,
        value: String,
        encoding: GcArrayString,
    ): CodeBlock

    fun invocation(function: Function): CodeBlock

    fun singleResult(
        function: Function,
        type: Type,
    ): CodeBlock

    fun result(
        function: Function,
        index: Int,
        type: Type,
    ): CodeBlock

    fun stringArrayResult(
        function: Function,
        encoding: StringEncodingStrategy,
    ): CodeBlock

    fun referenceRelease(function: Function): CodeBlock
}

internal class BoxedFunctionCallConvention(
    private val executionValue: ClassName,
    private val numberValue: ClassName,
) : FunctionCallConvention {

    override val passesReferences = false

    override fun reservedNames(functions: List<Function>): List<String> {
        return inputCounts(functions).flatMap { inputCount ->
            listOf(FUNCTION_INPUT_BUFFER_NAME, functionInputBufferName(inputCount))
        }
    }

    override fun properties(functions: List<Function>): List<PropertySpec> {

        val inputCounts = inputCounts(functions).sorted()
        val maximumInputCount = inputCounts.maxOrNull() ?: return emptyList()
        val executionValues = MUTABLE_LIST.parameterizedBy(executionValue)

        val buffer = PropertySpec.builder(FUNCTION_INPUT_BUFFER_NAME, executionValues)
            .addModifiers(KModifier.PRIVATE)
            .initializer("MutableList<%T>(%L) { %T(0) }", executionValue, maximumInputCount, numberValueClass(Scalar.Integer))
            .build()
        val views = inputCounts.map { inputCount ->

            val initializer = if (inputCount == maximumInputCount) {
                CodeBlock.of("%L", FUNCTION_INPUT_BUFFER_NAME)
            } else {
                CodeBlock.of("%L.subList(0, %L)", FUNCTION_INPUT_BUFFER_NAME, inputCount)
            }

            PropertySpec.builder(functionInputBufferName(inputCount), executionValues)
                .addModifiers(KModifier.PRIVATE)
                .initializer(initializer)
                .build()
        }

        return listOf(buffer) + views
    }

    override fun argumentAssignment(
        function: Function,
        index: Int,
        type: Type,
        value: CodeBlock,
    ): CodeBlock {
        return CodeBlock.builder()
            .addStatement("%L[%L] = %T(%L)", FUNCTION_INPUT_BUFFER_NAME, index, numberValueClass(type), value)
            .build()
    }

    override fun stringArrayArgumentAssignment(
        function: Function,
        index: Int,
        value: String,
        encoding: GcArrayString,
    ): CodeBlock {
        error("GC array strings require ExecutionTier.Jit")
    }

    override fun invocation(function: Function): CodeBlock {

        val arguments = if (function.params.isEmpty()) {
            CodeBlock.of("emptyList<%T>()", executionValue)
        } else {
            CodeBlock.of("%L", functionInputBufferName(function.inputCount))
        }

        return CodeBlock.builder()
            .addStatement("val args = %L", arguments)
            .addStatement(
                "val values = %L(args).%M(%S)",
                preparedFunctionPropertyName(function),
                CHASM_EXPECT,
                "Failed to invoke function ${function.name}",
            )
            .build()
    }

    override fun singleResult(
        function: Function,
        type: Type,
    ): CodeBlock {
        return CodeBlock.of("(values.first() as %T).value", numberValueClass(type))
    }

    override fun result(
        function: Function,
        index: Int,
        type: Type,
    ): CodeBlock {
        return CodeBlock.of("(values[%L] as %T).value", index, numberValueClass(type))
    }

    override fun stringArrayResult(
        function: Function,
        encoding: StringEncodingStrategy,
    ): CodeBlock {
        error("GC array strings require ExecutionTier.Jit")
    }

    override fun referenceRelease(function: Function): CodeBlock {
        return CodeBlock.of("")
    }

    private fun inputCounts(functions: List<Function>): List<Int> {
        return functions.map(Function::inputCount)
            .filter { inputCount -> inputCount > 0 }
            .distinct()
    }

    private fun numberValueClass(type: Type): ClassName {
        return numberValue.nestedClass(numberValueName(type))
    }
}

internal class UnboxedFunctionCallConvention : FunctionCallConvention {

    override val passesReferences = true

    override fun reservedNames(functions: List<Function>): List<String> {

        val lanes = functions.flatMap { function ->
            listOf(functionWordsPropertyName(function), functionReferencesPropertyName(function))
        }
        val arrayTypes = functions.flatMap(Function::gcArrayTypeIndices).map(::gcArrayTypePropertyName)

        return lanes + listOf(NO_WORDS_NAME, NO_REFERENCES_NAME) + arrayTypes
    }

    override fun properties(functions: List<Function>): List<PropertySpec> {

        val lanes = functions.flatMap { function ->
            buildList {
                if (function.invocationSlotCount > 0) {
                    add(wordsProperty(functionWordsPropertyName(function), function.invocationSlotCount))
                }
                if (passesReferences(function)) {
                    add(referencesProperty(functionReferencesPropertyName(function), function.invocationSlotCount))
                }
            }
        }
        val sharedLanes = buildList {
            if (functions.any { function -> function.invocationSlotCount == 0 }) {
                add(wordsProperty(NO_WORDS_NAME, 0))
            }
            if (functions.any { function -> !passesReferences(function) }) {
                add(referencesProperty(NO_REFERENCES_NAME, 0))
            }
        }

        val arrayTypes = functions.flatMap(Function::gcArrayTypeIndices)
            .distinct()
            .sorted()
            .map { typeIndex ->
                PropertySpec.builder(gcArrayTypePropertyName(typeIndex), HOST_GC_TYPE_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("%M(instance, %L)", RUNTIME_TYPE, typeIndex)
                    .build()
            }

        return lanes + sharedLanes + arrayTypes
    }

    override fun argumentAssignment(
        function: Function,
        index: Int,
        type: Type,
        value: CodeBlock,
    ): CodeBlock {
        return if (type.isReference) {
            CodeBlock.builder()
                .addStatement("%N[%L] = %L", referencesLane(function), index, valueToReference(value, type))
                .build()
        } else {
            CodeBlock.builder()
                .addStatement("%N[%L] = %L", wordsLane(function), index, valueToWord(value, type))
                .build()
        }
    }

    override fun stringArrayArgumentAssignment(
        function: Function,
        index: Int,
        value: String,
        encoding: GcArrayString,
    ): CodeBlock {

        val contents = when (encoding.elementType) {
            PackedType.I16 -> CodeBlock.of("%L.toCharArray()", value)
            PackedType.I8 -> CodeBlock.of("%L.encodeToByteArray()", value)
        }

        return CodeBlock.builder()
            .addStatement(
                "%N[%L] = %M(store, %N, %L)",
                referencesLane(function),
                index,
                UNSAFE_ALLOCATE_ARRAY,
                gcArrayTypePropertyName(encoding.typeIndex),
                contents,
            )
            .build()
    }

    override fun invocation(function: Function): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "%N.%M(%N, %N).%M(%S)",
                preparedFunctionPropertyName(function),
                INVOKE_UNBOXED,
                wordsLane(function),
                referencesLane(function),
                CHASM_EXPECT,
                "Failed to invoke function ${function.name}",
            )
            .build()
    }

    override fun singleResult(
        function: Function,
        type: Type,
    ): CodeBlock {
        return result(function, 0, type)
    }

    override fun result(
        function: Function,
        index: Int,
        type: Type,
    ): CodeBlock {
        return if (type.isReference) {
            referenceToValue(CodeBlock.of("%N[%L]", referencesLane(function), index), type)
        } else {
            wordToValue(CodeBlock.of("%N[%L]", wordsLane(function), index), type)
        }
    }

    override fun stringArrayResult(
        function: Function,
        encoding: StringEncodingStrategy,
    ): CodeBlock {

        val array = CodeBlock.of("%N[0] as %T", referencesLane(function), ARRAY_REFERENCE_CLASS_NAME)

        return when (encoding) {
            StringEncodingStrategy.GC_UTF16_ARRAY -> CodeBlock.of("%M(%L).concatToString()", UNSAFE_BORROW_CHARS, array)
            StringEncodingStrategy.GC_UTF8_ARRAY -> CodeBlock.of("%M(%L).decodeToString()", UNSAFE_BORROW_BYTES, array)
            else -> error("$encoding does not store strings in GC arrays")
        }
    }

    override fun referenceRelease(function: Function): CodeBlock {
        return CodeBlock.builder().apply {
            referenceSlots(function).forEach { slot ->
                addStatement("%N[%L] = null", referencesLane(function), slot)
            }
        }.build()
    }

    private fun referenceSlots(function: Function): List<Int> {

        var slot = 0
        val argumentSlots = function.params.flatMap { param ->

            val first = slot
            slot += if (param.linearMemoryStrategy == StringEncodingStrategy.POINTER_AND_LENGTH) 2 else 1

            if (param.type.isReference || param.stringEncoding is GcArrayString) listOf(first) else emptyList()
        }
        val resultSlots = function.resultTypes.indices.filter { index ->
            function.resultTypes[index] is ValueType.Reference
        }

        return (argumentSlots + resultSlots).distinct().sorted()
    }

    private fun passesReferences(function: Function): Boolean {
        return referenceSlots(function).isNotEmpty()
    }

    private fun wordsLane(function: Function): String {
        return if (function.invocationSlotCount > 0) functionWordsPropertyName(function) else NO_WORDS_NAME
    }

    private fun referencesLane(function: Function): String {
        return if (passesReferences(function)) functionReferencesPropertyName(function) else NO_REFERENCES_NAME
    }

    private fun wordsProperty(
        name: String,
        slots: Int,
    ): PropertySpec {
        return PropertySpec.builder(name, LONG_ARRAY)
            .addModifiers(KModifier.PRIVATE)
            .initializer("LongArray(%L)", slots)
            .build()
    }

    private fun referencesProperty(
        name: String,
        slots: Int,
    ): PropertySpec {
        return PropertySpec.builder(name, NULLABLE_ANY_ARRAY)
            .addModifiers(KModifier.PRIVATE)
            .initializer("arrayOfNulls<%T>(%L)", ANY, slots)
            .build()
    }

    private fun functionWordsPropertyName(function: Function): String {
        return function.name + "Words"
    }

    private fun functionReferencesPropertyName(function: Function): String {
        return function.name + "References"
    }
}

private const val NO_WORDS_NAME = "noWords"
private const val NO_REFERENCES_NAME = "noReferences"

private val MUTABLE_LIST = ClassName("kotlin.collections", "MutableList")
private val NULLABLE_ANY_ARRAY = ClassName("kotlin", "Array").parameterizedBy(ANY.copy(nullable = true))
private val HOST_GC_TYPE_CLASS_NAME = ClassName(COMPILED_PACKAGE, "HostGcType")
private val RUNTIME_TYPE = MemberName(COMPILED_PACKAGE, "runtimeType")
private val INVOKE_UNBOXED = MemberName(COMPILED_PACKAGE, "invokeUnboxed", isExtension = true)
private val UNSAFE_ALLOCATE_ARRAY = MemberName(COMPILED_PACKAGE, "unsafeAllocateArray")
private val UNSAFE_BORROW_CHARS = MemberName(COMPILED_PACKAGE, "unsafeBorrowChars")
private val UNSAFE_BORROW_BYTES = MemberName(COMPILED_PACKAGE, "unsafeBorrowBytes")
