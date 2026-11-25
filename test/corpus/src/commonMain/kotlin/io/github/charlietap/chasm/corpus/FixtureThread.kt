package io.github.charlietap.chasm.corpus

interface FixtureThread {

    fun <T> run(body: () -> T): T
}
