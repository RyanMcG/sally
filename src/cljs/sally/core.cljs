(ns sally.core
  (:require [om.core :as om :include-macros true]
            [ajax.core :refer [POST]]
            [sablono.core :as html :refer-macros [html]]
            [figwheel.client :as figwheel :include-macros true]
            [weasel.repl :as weasel]))

(defonce app-state
  (atom {:code ""
         :issues {{:name "kibit"} ["something"]}}))

(defn- get-ref-value [owner ref-name]
  (-> (om/get-node owner ref-name) .-value))

(defn ig [f & more] (fn [_] (apply f more)))

(defn textarea-key-up [cursor owner]
  (om/transact! cursor
                :code (ig get-ref-value owner "code-area")
                :code-change)
  (println @cursor))

(defn checking-textarea [data owner]
  (om/component
    (html [:textarea {:ref "code-area"
                      :on-key-up (ig textarea-key-up data owner)}
           (:code data)])))

(defn display-issue [issue]
  (reify
    om/IDisplayName
    (display-name [_] "Issue")
    om/IRender
    (render [_] (html [:li.issue (pr-str issue)]))))

(defn display-checker [[checker issues]]
  (om/component
    (html [:ul {:id (str "checker-" (:name checker))}
           (om/build-all display-issue issues)])))

(defn live-checking [data owner]
  (om/component
    (html [:div#live-checking
           (om/build checking-textarea data)
           [:div#issues-display
            (om/build-all display-checker (get data :issues))]])))

(om/root

  live-checking
  app-state
  {:target (. js/document (getElementById "app"))})

(def is-dev? (.contains (.. js/document -body -classList) "is-dev"))

(when is-dev?
  (enable-console-print!)
  (figwheel/watch-and-reload
   :websocket-url "ws://localhost:3449/figwheel-ws"
   :jsload-callback (fn [] (print "reloaded")))
  #_(weasel/connect "ws://localhost:9001" :verbose true))
