(ns sally.main
  (:require [sally.server :refer [create-http-server]]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defn -main [& _] (component/start (create-http-server)))
