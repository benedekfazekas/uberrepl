(ns repl.frontend
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [frontend.system :as fes]))

(def frontend-port
  3000)

(def backend-url
  "http://localhost:3001")

(def frontend-system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init-frontend
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'frontend-system
    (constantly (fes/create-dev-system frontend-port backend-url))))

(defn start-frontend
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'frontend-system fes/start))

(defn stop-frontend
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'frontend-system fes/stop))

(defn go-frontend
  "Initializes and starts the system running."
  []
  (init-frontend)
  (start-frontend)
  :ready)

(defn reset-frontend
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop-frontend)
  (refresh :after 'repl.frontend/go-frontend))
