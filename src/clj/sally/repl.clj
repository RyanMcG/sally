(ns sally.repl
  (:require [taoensso.timbre :as timbre]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            (sally [server :refer [create-http-server]])))

(defn- setup-timbre! []
  (timbre/set-level! (env :log-level))
  (timbre/merge-config! (env :timbre)))

(defonce system (atom nil))

(defn init [& {:as config}]
  (setup-timbre!)
  (swap! system (constantly (create-http-server))))

(defn- start []
  (swap! system component/start))

(defn- stop []
  (swap! system (fn [s] (if s (component/stop s)))))

(defn go [& more]
  (stop)
  (apply init more)
  (start))
