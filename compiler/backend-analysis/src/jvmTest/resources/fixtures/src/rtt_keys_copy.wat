(module
  (type $unrelated (func))
  (type $leaf (struct (field i64)))
  (rec
    (type $first (struct (field (ref null $leaf))))
    (type $second (struct (field (ref null $leaf)))))
)
