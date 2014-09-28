(defproject sally "0.1.0-SNAPSHOT"
  :description "Static analysis library and service"
  :url "http://sally.clojurecup.com/"
  :license {:name "MIT"}

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2342"]
                 [ring "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring-middleware-format "0.4.0"]
                 [compojure "1.1.9"]
                 [http-kit "2.1.18"]
                 [hiccup "1.0.5"]
                 [com.stuartsierra/component "0.2.2"]
                 [om "0.7.1"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [environ "1.0.0"]
                 [com.cemerick/piggieback "0.1.3"]
                 [com.taoensso/timbre "3.1.6"]
                 [sablono "0.2.22"]
                 [cljs-ajax "0.3.0"]
                 [weasel "0.4.0-SNAPSHOT"]
                 [jonase/kibit "0.0.8"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]]

  :min-lein-version "2.0.0"

  :uberjar-name "sally.jar"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/app.js"
                                        :output-dir    "resources/public/out"
                                        :source-map    "resources/public/out.js.map"
                                        :preamble      ["react/react.min.js"]
                                        :externs       ["react/externs/react.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:dependencies []
                   :repl-options {:init-ns sally.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :plugins [[lein-figwheel "0.1.4-SNAPSHOT"]]
                   :figwheel {:http-server-root "public"
                              :port 3449}
                   :env {:stage :dev
                         :port 5000
                         :nrepl-port 5001
                         :ip "127.0.0.1"
                         :log-level :info}}

             :uberjar {:hooks [leiningen.cljsbuild]
                       :env {:stage :production
                             :ip "185.12.6.20"
                             :log-level :warn
                             :port 80}
                       :omit-source true
                       :aot []
                       :cljsbuild {:builds {:app
                                            {:compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}}

  :main sally.main)
