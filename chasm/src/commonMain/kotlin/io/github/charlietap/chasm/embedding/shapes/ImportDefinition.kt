package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.type.ExternalType

data class ImportDefinition(val moduleName: String, val entityName: String, val type: ExternalType)
