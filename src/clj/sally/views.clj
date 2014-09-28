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
     (page/include-css "/boots/css/bootstrap.min.css"
                       "/boots/css/font-awesome.min.css"
                       "/boots/css/local.css")
     (page/include-js "/boots/js/jquery-1.10.2.min.js"
                      "/boots/js/bootstrap.min.js")]
    [:body {:class (str "is-" (name (env :stage :dev)))}
     [:div#wrapper
      [:nav.navbar.navbar-inverse.navbar-fixed-top {:role "navigation"}
       [:div.navbar-header
        [:button.navbar-toggle {:data-toggle "collase"
                                :type "button"
                                :data-target ".navbar-ex1-collapse"}
         [:span.sr-only "Toggle navigation"]
         [:span.icon-bar]
         [:span.icon-bar]
         [:span.icon-bar]]
        [:a.navbar-brand {:href "/"} "Sally"]]
       [:div.collapse.navbar-collapse.navbar-ex1-collapse
        [:ul#active.nav.navbar-nav.side-nav
         [:li.selected [:a {:href "/"} [:i.fa.fa-bullseye " Home"]]]]
        [:ul.nav.navbar-nav.navbar-right.navbar-user
         [:li [:a {:href "/something"} "Anything"]]]]]
      [:div#page-wrapper content]]

     (if in-dev? (page/include-js "/out/goog/base.js"
                                  "/react/react.js"))
     (page/include-js "/app.js")
     (if in-dev? (javascript-tag "goog.require('sally.core')"))]))

(defn root-page [request]
  (layout
    [:h1 "Welcome!"]
    [:h2 "Enter some code:"]
    [:div#app]))

(defn resp [data & [status]]
  {:status (or status 200)
   :body data})

(defn handle-check [{:keys [body-params body] :as req}]
  (resp (check (or body-params body))))
