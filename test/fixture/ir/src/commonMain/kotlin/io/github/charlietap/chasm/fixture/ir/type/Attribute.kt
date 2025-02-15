package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.TagType
import io.github.charlietap.chasm.ir.type.TagType.Attribute

fun attribute(): Attribute = exceptionAttribute()

fun exceptionAttribute() = TagType.Attribute.Exception
