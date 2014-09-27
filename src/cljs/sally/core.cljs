(ns sally.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [figwheel.client :as figwheel :include-macros true]
            [weasel.repl :as weasel]))

(defonce app-state
  (atom {}))

(defn textarea-key-up [e]
  (.log js/console e))

(defn checking-textarea [data]
  (om/component
    (html [:textarea {:on-key-up textarea-key-up}])))

(om/root
  checking-textarea
  app-state
  {:target (. js/document (getElementById "app"))})

(def is-dev? (.contains (.. js/document -body -classList) "is-dev"))

(when is-dev?
  (enable-console-print!)
  (figwheel/watch-and-reload
   :websocket-url "ws://localhost:3449/figwheel-ws"
   :jsload-callback (fn [] (print "reloaded")))
  (weasel/connect "ws://localhost:9001" :verbose true))
