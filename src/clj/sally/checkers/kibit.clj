(ns sally.checkers.kibit
  (:require [kibit.check :as kc]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [sally.checkers.meta :refer [add-sally-metadata]])
  (:import [java.io File Reader StringReader]
           [clojure.lang PersistentList]))

(defprotocol KibitCheck
  (check-impl [thing]))

(extend-protocol KibitCheck
  Reader
  (check-impl [r] (kc/check-reader r))

  File
  (check-impl [f] (kc/check-file f))

  PersistentList
  (check-impl [expr] (list (kc/check-expr expr)))

  String
  (check-impl [s] (check-impl (StringReader. s))))

(def kibit->issues identity) ;; No transformation necessary
(defn check [thing] (kibit->issues (check-impl thing)))

(add-sally-metadata check
                    :name "kibit"
                    :description "A static code analyzer for Clojure which uses
                                 `core.logic` to search for patterns of code for
                                 which there might exist a more idiomatic
                                 function or macro."
                    :source "https://github.com/jonase/kibit")
