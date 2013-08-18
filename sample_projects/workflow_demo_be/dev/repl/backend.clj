(ns repl.backend
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [backend.system :as bes]))

(def backend-port
  3001)

(def backend-system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init-backend
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'backend-system
    (constantly (bes/create-dev-system backend-port))))

(defn start-backend
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'backend-system bes/start))

(defn stop-backend
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'backend-system bes/stop))

(defn go-backend
  "Initializes and starts the system running."
  []
  (init-backend)
  (start-backend)
  :ready)

(defn reset-backend
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop-backend)
  (refresh :after 'repl.backend/go-backend))
