(ns frontend.system
  (:require [frontend.core :as fec]
            [ring.adapter.jetty :as jetty]))

(defn create-system* [backend-url]
  {:backend-url backend-url
   :handler (fec/create-handler backend-url)})

(defn create-system []
  (let [backend-url "http://localhost:3030"]
    (create-system* backend-url)))

(defn create-dev-system [port backend-url]
  (assoc (create-system* backend-url)
    :port port
    :backend-url backend-url))

(def handler
  (:handler (create-system)))

(defn start [system]
  (let [server (jetty/run-jetty (:handler system) {:port (:port system)
                                                   :join? false})]
    (assoc system :server server)))

(defn stop [system]
  (when (:server system)
    (.stop (:server system)))
  (dissoc system :server))
