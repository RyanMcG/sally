(ns sally.checkers.kibit
  (:require [kibit.check :as kc]
            [clojure.edn :as edn]
            [sally.checkers.meta :refer [add-sally-metadata]])
  (:import [java.io File Reader]
           [clojure.lang PersistentList]))

(defprotocol KibitCheck
  (check [thing]))

(extend-protocol KibitCheck
  Reader
  (check [r] (kc/check-reader r))

  File
  (check [f] (kc/check-file f))

  PersistentList
  (check [expr] (kc/check-expr expr))

  String
  (check [s] (check (edn/read-string s))))

(add-sally-metadata check
                    :name "kibit"
                    :description "A static code analyzer for Clojure which uses
                                 `core.logic` to search for patterns of code for
                                 which there might exist a more idiomatic
                                 function or macro."
                    :source "https://github.com/jonase/kibit")
