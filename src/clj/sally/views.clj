(ns sally.views
  (:require (hiccup [page :as page])))

(defn root-page [request]
  (page/html5
    [:h1 "Sally"]
    [:h2 "Welcome!"]))
