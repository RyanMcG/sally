(ns sally.server
  (:require [clojure.java.io :as io]
            (compojure [core :refer [GET routes]]
                       [route :refer [resources]])
            [com.stuartsierra.component :as component]
            (hiccup [page :as page])
            [taoensso.timbre :refer [report info]]
            (ring.middleware [defaults :refer [wrap-defaults site-defaults]]
                             [format :refer [wrap-restful-format]])
            [environ.core :refer (env)]
            [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]
            (sally [views :refer :all])
            [clojure.tools.nrepl.server :as nrepl]
            [org.httpkit.server :refer [run-server]]))

(defn- create-app
  "Create a ring application that is a deverlopment friendly server."
  []
  (-> (routes (resources "/")
              (resources "/react" {:root "react"})
              (GET "/" req (root-page req)))
      (wrap-restful-format)
      (wrap-defaults site-defaults)))

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
