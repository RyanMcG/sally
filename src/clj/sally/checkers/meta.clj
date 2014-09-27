(ns sally.checkers.meta)

(defn add-sally-metadata* [a-var & {:as md}]
  (alter-meta! a-var assoc :sally md))

(defmacro add-sally-metadata [a-var & args]
  `(add-sally-metadata* (var ~a-var) ~@args))
