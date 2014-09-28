(ns sally.core
  (:require [om.core :as om :include-macros true]
            [jayq.core :as jq]
            [clojure.string :as s]
            [sablono.core :as html :refer-macros [html]]
            [figwheel.client :as figwheel :include-macros true]
            [weasel.repl :as weasel]))

(def ^:private kibit-example (str '(when (not anything) something)))

(defonce app-state
  (atom {:code kibit-example
         :issues []}))

(defn- get-ref-value [owner ref-name]
  (-> (om/get-node owner ref-name) .-value))

(defn ig [f & more] (fn [_] (apply f more)))

(defn change-code [cursor owner]
  (om/update! cursor
              [:code] (get-ref-value owner "code-area")
              :code-change))

(defn checking-textarea [data owner]
  (om/component
    (html [:div#code-area
           [:textarea {:ref "code-area"
                       :on-change (ig change-code data owner)
                       :value (:code data)}]])))

(defn code->hiccup [class-name code]
  [:pre {:class class-name}
   [:code (s/trim (pr-str code))]])

(defn display-issue [{:keys [alt expr line column]}]
  (reify
    om/IDisplayName
    (display-name [_] "Issue")
    om/IRender
    (render [_]
      (html [:li.issue
             [:div.loc {:title (str "On line " line ", column " column)}
              [:span.line line]
              [:span.column column]]
             [:div.suggestion
              "Use:"
              (code->hiccup :alt alt)
              "instead of:"
              (code->hiccup :expr expr)]]))))

(defn display-checker [{:keys [name source description issues]}]
  (om/component
    (html [:div.checker {:id (str "checker-" (.toLowerCase name))}
           [:h3.checker-name [:a {:href source
                                  :title description} name]
            " found..."]
           [:ul.issues (om/build-all display-issue issues)]])))


(defn- edn-post [url [content-type data] success-fn]
  (jq/ajax url
           {:type "POST"
            :data data
            :headers {:Accept "application/edn"}
            :contentType content-type
            :success success-fn}))

(defn check [root-cursor & [data]]
  (edn-post "/check"
            ["text/plain" (or data (:code @root-cursor))]
            (fn code-change-post-success [resp]
              (om/update! root-cursor [:issues] resp))))

(defn live-checking [data owner]
  (reify
    om/IWillMount
    (will-mount [_] (check data kibit-example))
    om/IRender
    (render [_]
      (html [:div#live-checking
             (om/build checking-textarea data)
             [:div#issues-display
              (om/build-all display-checker (get data :issues))]]))))

(defmulti listen (fn [tx-data _] (:tag tx-data)))
(defmethod listen :default [_ _] nil)

(defmethod listen :code-change [_ root-cursor] (check root-cursor))

(om/root
  live-checking
  app-state
  {:tx-listen listen
   :target (. js/document (getElementById "app"))})

(def is-dev? (.contains (.. js/document -body -classList) "is-dev"))

(when is-dev?
  (enable-console-print!)
  (figwheel/watch-and-reload
   :websocket-url "ws://localhost:3449/figwheel-ws"
   :jsload-callback (fn [] (print "reloaded")))
  #_(weasel/connect "ws://localhost:9001" :verbose true))
