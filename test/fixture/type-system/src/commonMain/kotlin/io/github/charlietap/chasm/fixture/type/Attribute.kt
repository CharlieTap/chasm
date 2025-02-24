package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.TagType.Attribute

fun attribute(): Attribute = exceptionAttribute()

fun exceptionAttribute() = TagType.Attribute.Exception
