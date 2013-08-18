(ns backend.core
  (:require [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [not-found]]
            [compojure.handler]
            [ring.util.response]))


(defn create-counter []
  (atom 0))

(defn counter-value [counter]
  @counter)

(defn inc-counter [counter]
  (swap! counter inc))

(defn create-handler [counter]
  (compojure.handler/site
   (routes
    (GET "/counter" request
         (str "" (counter-value counter)))

    (POST "/counter/inc" request
          (inc-counter counter)
          (ring.util.response/redirect-after-post "/counter"))

    (not-found "Not found"))))
