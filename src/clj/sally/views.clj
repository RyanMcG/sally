(ns sally.views
  (:require (hiccup [page :as page]
                    [core :refer [html]]
                    [element :refer [javascript-tag]])
            [sally.checkers.core :refer [check]]
            [environ.core :refer (env)]))

(def in-dev? (= :dev (env :stage :dev)))

(defn layout [& content]
  (page/html5
    [:head
     (page/include-js "/boots/js/jquery-1.10.2.min.js")
     (page/include-css "normalize.css"
                       "sally.css")]
    [:body {:class (str "is-" (name (env :stage :dev)))}
     [:div#page-wrapper content]

     (if in-dev? (page/include-js "/out/goog/base.js"
                                  "/react/react.js"))
     (page/include-js "/app.js")
     (if in-dev? (javascript-tag "goog.require('sally.core')"))]))

(defn root-page [request]
  (layout
    [:h1 "Sally says..."]
    [:div#app]))

(defn resp [data & [status]]
  {:status (or status 200)
   :body data})

(defn handle-check [{:keys [body-params body] :as req}]
  (resp (check (or body-params body))))
