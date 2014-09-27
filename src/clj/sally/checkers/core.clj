(ns sally.checkers.core)

(defmacro defcheckers [& checkers]
  (let [checkers (for [sym checkers] (list 'var sym))]
    `(def ~'checkers ~(vec checkers))))

(defcheckers)

(defn- invoke-on [v]
  (fn [f]
    (f v)))

(defn check
  ([exprs checkers] (map (invoke-on exprs) checkers))
  ([exprs] (check exprs checkers)))
