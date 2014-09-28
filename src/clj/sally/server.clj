(ns sally.server
  (:require [clojure.java.io :as io]
            (compojure [core :refer [POST GET routes]]
                       [route :refer [resources]])
            [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [report info]]
            (ring.middleware [defaults :refer [wrap-defaults api-defaults]]
                             [format-response :refer [wrap-restful-response]]
                             [format-params :refer [make-type-request-pred
                                                    wrap-clojure-params]])
            [environ.core :refer (env)]
            [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]
            (sally [views :refer :all])
            [clojure.tools.nrepl.server :as nrepl]
            [org.httpkit.server :refer [run-server]]))

(def ^:private text-request? (make-type-request-pred #"text/(clojure|plain)"))
(defn- wrap-text-body [handler]
  (wrap-clojure-params handler
                       :predicate text-request?
                       :binary? false
                       :decoder identity))

(defn- create-app
  "Create a ring application that is a deverlopment friendly server."
  []
  (-> (routes (resources "/")
              (resources "/react" {:root "react"})
              (GET "/" req (root-page req))
              (POST "/check" req (handle-check req)))
      (wrap-restful-response :formats [:json-kw :edn])
      (wrap-text-body)
      (wrap-clojure-params)
      (wrap-defaults api-defaults)))

(defn- serve
  "Start a development server."
  []
  (let [port (env :port)
        ip (env :ip "127.0.0.1")]
    (report "Serving server at" (str "http://" ip ":" port \/))
    (run-server (create-app) {:port port
                              :ip ip
                              :thread (env :thread-count 4)})))

(defn- get-nrepl-port-file [] (io/file ".nrepl-port"))
(defn- serve-nrepl [port]
  (let [{:keys [port] :as server} (nrepl/start-server
                                       :port (env :nrepl-port 0))]
    (info "Started nrepl on port " port)
    (spit (get-nrepl-port-file) port)
    server))

(defrecord NreplServer [port server]
  component/Lifecycle
  (start [this]
    (assoc this :server (serve-nrepl port)))
  (stop [this]
    (if server
      (do
        (nrepl/stop-server server)
        (.delete (get-nrepl-port-file))
        (info "Stopped nREPL server on port" port)
        (assoc this :server nil))
      this)))

(defn create-browser-repl []
  (piggieback/cljs-repl :repl-env (weasel/repl-env :ip "127.0.0.1" :port 9001)))

(defrecord HttpServer [port stop-server]
  component/Lifecycle
  (start [this] (assoc this :stop-server (serve)))
  (stop [this]
    (if stop-server
      (do
        (stop-server)
        (report "Stopped HTTP server on port" port)
        (assoc this :stop-server nil))
      this)))

(defn create-http-server []
  (map->HttpServer {:port (env :port)}))

(defn create-nrepl-server []
  (map->NreplServer {:port (env :nrepl-port)}))

(defn create-servers []
  (component/system-map
    :http (create-http-server)
    :nrepl (create-nrepl-server)))
