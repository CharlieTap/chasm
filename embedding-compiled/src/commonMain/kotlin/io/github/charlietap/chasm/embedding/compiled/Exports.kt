package io.github.charlietap.chasm.embedding.compiled

class Exports(
    val all: List<Export>,
) {

    private val byName = all.associateBy(Export::name)

    fun function(name: String): Function? {
        return byName[name]?.value as? Function
    }

    fun memory(name: String): Memory? {
        return byName[name]?.value as? Memory
    }

    fun global(name: String): Global? {
        return byName[name]?.value as? Global
    }

    fun table(name: String): Table? {
        return byName[name]?.value as? Table
    }

    fun tag(name: String): Tag? {
        return byName[name]?.value as? Tag
    }
}

fun exports(instance: Instance): Exports {
    return instance.engine.exports(instance)
}
