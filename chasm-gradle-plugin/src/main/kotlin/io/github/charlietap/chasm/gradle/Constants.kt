package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import io.github.charlietap.chasm.embedding.shapes.Import

internal val LIST_CLASS_NAME = List::class.asClassName()
internal val IMPORT_CLASS_NAME = Import::class.asClassName()
internal val LIST_IMPORTS_CLASS_NAME = LIST_CLASS_NAME.parameterizedBy(IMPORT_CLASS_NAME)

internal val CREATE_INSTANCE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "instance")
internal val CREATE_MODULE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "module")
internal val CREATE_STORE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "store")
internal val INVOKE_FUNCTION = MemberName("io.github.charlietap.chasm.embedding", "invoke")
internal val READ_GLOBAL_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.global", "readGlobal")
internal val WRITE_GLOBAL_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.global", "writeGlobal")
internal val EXPECT_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
internal val READ_STRING_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.memory", "readUtf8String")
internal val READ_NULL_STRING_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.memory", "readNullTerminatedUtf8String")
internal val READ_INT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.memory", "readInt")
internal val WRITE_INT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.memory", "writeInt")
internal val MAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "map")
internal val FLATMAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.embedding.shapes", "flatMap")
