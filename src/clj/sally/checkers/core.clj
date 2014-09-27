(ns sally.checkers.core
  (:require (clojure [string :as s]
                     [test :refer [are with-test]])))


(with-test
  (defn- sym-to-var-form [sym]
    (list 'var
          (symbol (if (.contains (str sym) "/")
                    sym
                    (str sym "/check")))))
  (are [in out] (= (second (sym-to-var-form in)) out)
       'keeps.var/here 'keeps.var/here
       'adds.check.to.me 'adds.check.to.me/check))

(with-test
  (defn- ns-sym-from-sym [sym]
    (symbol (first (s/split (str sym) #"/"))))
  (are [in out] (= (ns-sym-from-sym in) out)
       'hey/there 'hey
       'some.cool.ns/func 'some.cool.ns))

(defmacro defcheckers [& checkers]
  (let [checker-namespaces (into '() (set (map ns-sym-from-sym checkers)))
        checker-vars (map sym-to-var-form checkers)]
    `(defn ~'load-checkers [& {:keys [~'reload] :or {~'reload true}}]
       (doseq [~'checker-ns '~checker-namespaces]
         (if ~'reload
           (require :reload ~'checker-ns)
           (require ~'checker-ns)))
       ~(vec checker-vars))))

(defcheckers
  sally.checkers.kibit)

(defn check
  ([checkable checkers]
   (into {} (for [checker checkers]
              [checker (checker checkable)])))
  ([checkable] (check checkable (load-checkers))))
