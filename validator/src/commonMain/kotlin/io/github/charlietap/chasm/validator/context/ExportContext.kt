package io.github.charlietap.chasm.validator.context

internal interface ExportContext {
    val exportNames: HashSet<String>
}

internal data class ExportContextImpl(
    override val exportNames: HashSet<String> = hashSetOf(),
) : ExportContext
