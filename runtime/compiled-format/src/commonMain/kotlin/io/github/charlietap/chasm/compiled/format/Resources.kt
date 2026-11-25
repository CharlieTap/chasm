package io.github.charlietap.chasm.compiled.format

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
const val DATA_EXTENSION = "chasmdata"

@InternalChasmApi
fun dataResource(resourceRoot: String): String {
    return "$resourceRoot.$DATA_EXTENSION"
}

@InternalChasmApi
fun dexFileName(index: Int): String {
    return if (index == 0) "classes.dex" else "classes${index + 1}.dex"
}
