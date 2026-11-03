(module
  (memory 16)

  (func (export "sum") (param $n i32) (result i64)
    (local $i i32) (local $acc i64)
    (loop $next
      (local.set $acc (i64.add (local.get $acc) (i64.mul (i64.extend_i32_u (local.get $i)) (i64.const 3))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br_if $next (i32.lt_u (local.get $i) (local.get $n))))
    (local.get $acc))

  (func $fib (export "fib") (param $n i32) (result i32)
    (if (result i32) (i32.lt_u (local.get $n) (i32.const 2))
      (then (local.get $n))
      (else (i32.add (call $fib (i32.sub (local.get $n) (i32.const 1)))
                     (call $fib (i32.sub (local.get $n) (i32.const 2)))))))

  ;; Counts primes below n (n <= 1 MiB) with a byte sieve.
  (func (export "sieve") (param $n i32) (result i32)
    (local $i i32) (local $j i32) (local $count i32)
    (memory.fill (i32.const 0) (i32.const 1) (local.get $n))
    (local.set $i (i32.const 2))
    (block $done (loop $outer
      (br_if $done (i32.ge_u (i32.mul (local.get $i) (local.get $i)) (local.get $n)))
      (if (i32.load8_u (local.get $i)) (then
        (local.set $j (i32.mul (local.get $i) (local.get $i)))
        (block $inner_done (loop $inner
          (br_if $inner_done (i32.ge_u (local.get $j) (local.get $n)))
          (i32.store8 (local.get $j) (i32.const 0))
          (local.set $j (i32.add (local.get $j) (local.get $i)))
          (br $inner)))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $outer)))
    (local.set $i (i32.const 2))
    (block $counted (loop $count_loop
      (br_if $counted (i32.ge_u (local.get $i) (local.get $n)))
      (local.set $count (i32.add (local.get $count) (i32.load8_u (local.get $i))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $count_loop)))
    (local.get $count))

  ;; n x n f64 matrices at 0, n*n*8 and 2*n*n*8 (n <= 64); returns the trace of A*B.
  (func (export "matmul") (param $n i32) (result f64)
    (local $i i32) (local $j i32) (local $k i32) (local $sum f64) (local $b i32) (local $c i32) (local $trace f64)
    (local.set $b (i32.mul (i32.mul (local.get $n) (local.get $n)) (i32.const 8)))
    (local.set $c (i32.mul (local.get $b) (i32.const 2)))
    (local.set $i (i32.const 0))
    (loop $fill
      (f64.store (i32.shl (local.get $i) (i32.const 3)) (f64.convert_i32_u (i32.rem_u (local.get $i) (i32.const 7))))
      (f64.store (i32.add (local.get $b) (i32.shl (local.get $i) (i32.const 3))) (f64.convert_i32_u (i32.rem_u (local.get $i) (i32.const 5))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br_if $fill (i32.lt_u (local.get $i) (i32.mul (local.get $n) (local.get $n)))))
    (local.set $i (i32.const 0))
    (loop $rows
      (local.set $j (i32.const 0))
      (loop $cols
        (local.set $sum (f64.const 0))
        (local.set $k (i32.const 0))
        (loop $dot
          (local.set $sum (f64.add (local.get $sum)
            (f64.mul
              (f64.load (i32.shl (i32.add (i32.mul (local.get $i) (local.get $n)) (local.get $k)) (i32.const 3)))
              (f64.load (i32.add (local.get $b) (i32.shl (i32.add (i32.mul (local.get $k) (local.get $n)) (local.get $j)) (i32.const 3)))))))
          (local.set $k (i32.add (local.get $k) (i32.const 1)))
          (br_if $dot (i32.lt_u (local.get $k) (local.get $n))))
        (f64.store (i32.add (local.get $c) (i32.shl (i32.add (i32.mul (local.get $i) (local.get $n)) (local.get $j)) (i32.const 3))) (local.get $sum))
        (local.set $j (i32.add (local.get $j) (i32.const 1)))
        (br_if $cols (i32.lt_u (local.get $j) (local.get $n))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br_if $rows (i32.lt_u (local.get $i) (local.get $n))))
    (local.set $i (i32.const 0))
    (loop $diag
      (local.set $trace (f64.add (local.get $trace)
        (f64.load (i32.add (local.get $c) (i32.shl (i32.mul (local.get $i) (i32.add (local.get $n) (i32.const 1))) (i32.const 3))))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br_if $diag (i32.lt_u (local.get $i) (local.get $n))))
    (local.get $trace))

  ;; Bitwise CRC-32 over n bytes of memory.
  (func (export "crc32") (param $n i32) (result i32)
    (local $i i32) (local $crc i32) (local $bit i32)
    (local.set $crc (i32.const -1))
    (loop $bytes
      (local.set $crc (i32.xor (local.get $crc) (i32.load8_u (local.get $i))))
      (local.set $bit (i32.const 0))
      (loop $bits
        (local.set $crc (i32.xor (i32.shr_u (local.get $crc) (i32.const 1))
          (i32.and (i32.const 0xEDB88320) (i32.sub (i32.const 0) (i32.and (local.get $crc) (i32.const 1))))))
        (local.set $bit (i32.add (local.get $bit) (i32.const 1)))
        (br_if $bits (i32.lt_u (local.get $bit) (i32.const 8))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br_if $bytes (i32.lt_u (local.get $i) (local.get $n))))
    (i32.xor (local.get $crc) (i32.const -1)))
)
