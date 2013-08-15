(ns backend.system
  (:require [backend.core :as bec]
            [ring.adapter.jetty :as jetty]))

(defn create-system []
  (let [counter (bec/create-counter)]
    {:counter counter
     :handler (bec/create-handler counter)}))

(defn create-dev-system [port]
  (assoc (create-system) :port port))

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
