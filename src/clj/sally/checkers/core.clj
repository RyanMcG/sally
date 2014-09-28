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

(defmacro ^:private defcheckers [& checkers]
  (let [ns-syms (into '() (set (map ns-sym-from-sym checkers)))
        checker-var-forms (map sym-to-var-form checkers)]
    `(do
       (defn ~'load-checkers [& {:keys [~'reload] :or {~'reload true}}]
         (doseq [~'checker-ns '~ns-syms]
           (if ~'reload
             (require :reload ~'checker-ns)
             (require ~'checker-ns))))
       (~'load-checkers :reload true)
       (def ~'checkers ~(vec checker-var-forms)))))

(defcheckers
  sally.checkers.kibit)

(defn check
  ([checkable checkers]
   (into {} (for [checker checkers]
              [checker (checker checkable)])))
  ([checkable] (check checkable checkers)))
