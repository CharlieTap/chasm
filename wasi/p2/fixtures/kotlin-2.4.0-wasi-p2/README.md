# Kotlin 2.4.0 WASI Preview 2 fixture

From this directory, build the source with Kotlin 2.4.0, download the pinned
WASI Preview 1 adapter, then wrap the core module as a WASI Preview 2 command
component:

```shell
../../../../gradlew -p . wasmWasiNodeProductionExecutableCompileSync
curl -L \
  https://github.com/bytecodealliance/wasmtime/releases/download/v45.0.0/wasi_snapshot_preview1.command.wasm \
  -o wasi_snapshot_preview1.command.wasm
wasm-tools component new \
  build/compileSync/wasmWasi/main/productionExecutable/optimized/kotlin-p2-cli.wasm \
  --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.command.wasm \
  -o kotlin-2.4.0-wasi-p2.wasm
cp kotlin-2.4.0-wasi-p2.wasm ../../src/commonTest/resources/
```

The adapter is Wasmtime 45.0.0's
[`wasi_snapshot_preview1.command.wasm`](https://github.com/bytecodealliance/wasmtime/releases/download/v45.0.0/wasi_snapshot_preview1.command.wasm)
release artifact. The checked fixture was produced with `wasm-tools 1.239.0`
and has these SHA-256 hashes:

```text
d766f58126cb67f9ca1f762b871f0cfb249c15e395c024c974e7776e82f242b7  kotlin-2.4.0-wasi-p2.wasm
a8e803047be79619fe116f36b6d60118a751d0ee3e33a53f74ce6ad409f95e07  kotlin-p2-cli.wasm
eb843effeade4b79d7b9e9bf0e21ba33c24c26d54f347414a1ba72bcb65fac74  wasi_snapshot_preview1.command.wasm
```
