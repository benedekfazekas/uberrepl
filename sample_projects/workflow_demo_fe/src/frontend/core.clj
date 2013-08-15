(ns frontend.core
  (:require
   [compojure.core :refer [GET POST routes]]
   [compojure.route :refer [not-found]]
   [compojure.handler]
   [ring.util.response]
   [clj-http.client :as client]
   [hiccup.core]))

(def counter-endpoint
  "/counter")

(def increment-endpoint
  "/counter/inc")

(defn render-page [counter]
  (hiccup.core/html
   [:html
    [:div {:align "center"}
     [:h1 "Workflow Demo Frontend"]
     [:span (format "Counter: %s" counter)]
     [:div
     [:form {:action "increment"
             :method "post"}
      [:button {:type "submit"}
       "Increment"]]]]]))

(defn create-handler [backend-url]
  (compojure.handler/site
   (routes
    (GET "/" request
         (let [counter (:body (client/get (str backend-url counter-endpoint)))]
           (render-page counter)))

    (POST "/increment" request
          (client/post (str backend-url increment-endpoint))
          (ring.util.response/redirect-after-post "/")))))
