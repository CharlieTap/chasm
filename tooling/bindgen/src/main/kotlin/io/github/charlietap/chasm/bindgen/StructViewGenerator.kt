package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.charlietap.chasm.bindgen.ext.asTypeName
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_INTS_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_REFERENCES_FIELD
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.type.PackedType

internal class StructViewGenerator {
    operator fun invoke(view: StructView): TypeSpec {

        val constructor = FunSpec.constructorBuilder()
            .addParameter("reference", STRUCT_REFERENCE_CLASS_NAME)
            .build()
        val reference = PropertySpec.builder("reference", STRUCT_REFERENCE_CLASS_NAME)
            .initializer("reference")
            .build()
        val storage = view.storage

        return TypeSpec.classBuilder(view.className).apply {

            addAnnotation(JVM_INLINE)
            addModifiers(KModifier.VALUE)
            primaryConstructor(constructor)
            addProperty(reference)

            if (storage == null) {
                addProperties(view.fields.map(::fieldProperty))
            } else {
                addAnnotation(
                    AnnotationSpec.builder(OPT_IN_CLASS_NAME)
                        .addMember("%T::class", CHASM_INTERNAL_API_CLASS_NAME)
                        .build(),
                )
                addProperties(view.fields.map { field -> laneProperty(field, storage.fieldLanes[field.index]) })
                addFunction(storageFunction(view.className, storage))
            }
        }.build()
    }

    private fun fieldProperty(field: StructViewField): PropertySpec {

        val getter = FunSpec.getterBuilder()
            .addStatement("return %L", fieldReadExpression(field))
            .build()
        val setter = setterBuilder(field)
            .addStatement("%L", fieldWriteExpression(field))
            .build()

        return property(field, getter, setter)
    }

    private fun laneProperty(
        field: StructViewField,
        fieldLanes: FieldLanes,
    ): PropertySpec {

        val getter = FunSpec.getterBuilder()
        val setter = setterBuilder(field)

        when (fieldLanes) {
            is NumberLanes -> if (fieldLanes.lanes.size == 1) {
                numberLane(field, fieldLanes, getter, setter)
            } else {
                wideNumberLanes(field, fieldLanes, getter, setter)
            }
            is DefinedReferenceLane -> {
                getter.addStatement("return %L", referenceToValue(laneRead("storage()", fieldLanes.lane), field.type))
                definedReferenceWrite(field, fieldLanes, setter)
            }
            is AbstractReferenceLane -> {
                getter.addStatement("return %L", referenceToValue(laneRead("storage()", fieldLanes.lane), field.type))
                setter.addStatement("%L", fieldWriteExpression(field))
            }
        }

        return property(field, getter.build(), setter.build())
    }

    private fun numberLane(
        field: StructViewField,
        numberLanes: NumberLanes,
        getter: FunSpec.Builder,
        setter: FunSpec.Builder,
    ) {

        val lane = laneRead("storage()", numberLanes.lanes.single())

        if (field.type == Scalar.Float) {
            getter.addStatement("return %T.fromBits(%L)", FLOAT, lane)
            setter.addStatement("%L = newValue.toRawBits()", lane)
        } else {
            getter.addStatement("return %L", lane)
            setter.addStatement("%L = %L", lane, packedValue(numberLanes.packedType))
        }
    }

    private fun wideNumberLanes(
        field: StructViewField,
        numberLanes: NumberLanes,
        getter: FunSpec.Builder,
        setter: FunSpec.Builder,
    ) {

        val (low, high) = numberLanes.lanes
        val bits = CodeBlock.of(
            "(%L.toLong() and 0xFFFFFFFFL) or (%L.toLong() shl 32)",
            laneRead("storage", low),
            laneRead("storage", high),
        )
        val value = if (field.type == Scalar.Double) "bits" else "newValue"

        getter.addStatement("val storage = storage()")
        setter.addStatement("val storage = storage()")

        if (field.type == Scalar.Double) {
            getter.addStatement("return %T.fromBits(%L)", DOUBLE, bits)
            setter.addStatement("val bits = newValue.toRawBits()")
        } else {
            getter.addStatement("return %L", bits)
        }

        setter.addStatement("%L = %L.toInt()", laneRead("storage", low), value)
        setter.addStatement("%L = (%L ushr 32).toInt()", laneRead("storage", high), value)
    }

    private fun definedReferenceWrite(
        field: StructViewField,
        lane: DefinedReferenceLane,
        setter: FunSpec.Builder,
    ) {

        val compiledClass = when (val type = field.type) {
            is StructViewType -> COMPILED_STRUCT_CLASS_NAME.copy(nullable = type.nullable)
            is ArrayReferenceType -> COMPILED_ARRAY_CLASS_NAME.copy(nullable = type.nullable)
            else -> error("Field ${field.index} does not hold a struct or array reference")
        }
        val sameStoreAndType = CodeBlock.of("value.rtt.engine·===·storage.rtt.engine && %L", rttCheck("value.rtt", lane.check))
        val acceptsValue = if (compiledClass.isNullable) {
            CodeBlock.of("value·==·null || (%L)", sameStoreAndType)
        } else {
            sameStoreAndType
        }

        setter.addStatement("val storage = storage()")
        setter.addStatement("val value = %L as %T", valueToReference(CodeBlock.of("newValue"), field.type), compiledClass)
        setter.addStatement("require(%L) { %S }", acceptsValue, "the value does not match the type of field ${field.index}")
        setter.addStatement("%L = value", laneRead("storage", lane.lane))
    }

    private fun storageFunction(
        className: ClassName,
        storage: StructViewStorage,
    ): FunSpec {
        return FunSpec.builder("storage")
            .addModifiers(KModifier.PRIVATE)
            .returns(storage.storageClass)
            .addStatement("val struct = reference as %T", storage.storageClass)
            .addStatement("require(%L) { %S }", rttCheck("struct.rtt", storage.check), "the struct is not a ${className.simpleName}")
            .addStatement("return struct")
            .build()
    }

    private fun rttCheck(
        rtt: String,
        check: RttCheck,
    ): CodeBlock {
        return when {
            check.final -> CodeBlock.of("%L.key·===·%S", rtt, check.key)
            check.depth == 0 -> CodeBlock.of("%L.supertypes[0].key·===·%S", rtt, check.key)
            else -> CodeBlock.of("%L.depth·>=·%L && %L.supertypes[%L].key·===·%S", rtt, check.depth, rtt, check.depth, check.key)
        }
    }

    private fun laneRead(
        receiver: String,
        lane: Lane,
    ): CodeBlock {
        return when {
            lane.spilled && lane.kind == LaneKind.INT -> CodeBlock.of("%L.%L!![%L]", receiver, SPILLED_INTS_FIELD, lane.index)
            lane.spilled -> CodeBlock.of("%L.%L!![%L]", receiver, SPILLED_REFERENCES_FIELD, lane.index)
            lane.kind == LaneKind.INT -> CodeBlock.of("%L.i%L", receiver, lane.index)
            else -> CodeBlock.of("%L.r%L", receiver, lane.index)
        }
    }

    private fun packedValue(packedType: PackedType?): CodeBlock {
        return when (packedType) {
            PackedType.I8 -> CodeBlock.of("newValue and 0xFF")
            PackedType.I16 -> CodeBlock.of("newValue and 0xFFFF")
            null -> CodeBlock.of("newValue")
        }
    }

    private fun setterBuilder(field: StructViewField): FunSpec.Builder {
        return FunSpec.setterBuilder().addParameter("newValue", field.type.asTypeName())
    }

    private fun property(
        field: StructViewField,
        getter: FunSpec,
        setter: FunSpec,
    ): PropertySpec {
        return PropertySpec.builder(field.name, field.type.asTypeName()).apply {

            mutable(field.mutable)
            getter(getter)

            if (field.mutable) {
                setter(setter)
            }
        }.build()
    }

    private fun fieldReadExpression(field: StructViewField): CodeBlock {
        return if (field.type.isReference) {
            referenceToValue(CodeBlock.of("%M(reference, %L)", READ_STRUCT_REFERENCE, field.index), field.type)
        } else {
            wordToValue(CodeBlock.of("%M(reference, %L)", READ_STRUCT_FIELD, field.index), field.type)
        }
    }

    private fun fieldWriteExpression(field: StructViewField): CodeBlock {

        val value = CodeBlock.of("newValue")

        return if (field.type.isReference) {
            CodeBlock.of("%M(reference, %L, %L)", WRITE_STRUCT_REFERENCE, field.index, valueToReference(value, field.type))
        } else {
            CodeBlock.of("%M(reference, %L, %L)", WRITE_STRUCT_FIELD, field.index, valueToWord(value, field.type))
        }
    }
}

private const val REFERENCE_PACKAGE = "io.github.charlietap.chasm.embedding.reference"
private const val COMPILED_GC_PACKAGE = "io.github.charlietap.chasm.runtime.compiled.gc"

private val JVM_INLINE = ClassName("kotlin.jvm", "JvmInline")
private val COMPILED_STRUCT_CLASS_NAME = ClassName(COMPILED_GC_PACKAGE, "CompiledStruct")
private val COMPILED_ARRAY_CLASS_NAME = ClassName(COMPILED_GC_PACKAGE, "CompiledArray")
private val READ_STRUCT_FIELD = MemberName(REFERENCE_PACKAGE, "readStructField")
private val READ_STRUCT_REFERENCE = MemberName(REFERENCE_PACKAGE, "readStructReference")
private val WRITE_STRUCT_FIELD = MemberName(REFERENCE_PACKAGE, "writeStructField")
private val WRITE_STRUCT_REFERENCE = MemberName(REFERENCE_PACKAGE, "writeStructReference")
