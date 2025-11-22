# AGENTS.md

## Build & Test

- **Fast dev loop:** run `./gradlew test`. This executes the runtime unit
  tests and the official Wasm test suite via the JVM backend, giving quick
  signal without longer-running native targets.
- **Full matrix:** invoke the usual platform tasks (e.g. `./gradlew jvmTest`,
  `./gradlew macosArm64Test`, etc.) when you need exhaustive coverage or are
  touching platform-specific code.
- The project is Kotlin Multiplatform; ensure the appropriate toolchains
  (Xcode CLI tools for macOS/iOS, etc.) are installed before running those
  additional tasks.

## Formatting & ABI

- Run `./gradlew fmt` before committing to keep formatting consistent.
- If you’ve changed a public ABI, first check it with `./gradlew
  checkLegacyAbi`. If it reports changes, record them with `./gradlew
  updateLegacyAbi`.
- In Cursor, use the `read_lints` command before finishing to surface IDE
  diagnostics (detekt/ktlint) for the files you edited.
