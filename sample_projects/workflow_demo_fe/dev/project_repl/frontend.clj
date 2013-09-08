(ns project-repl.frontend
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [frontend.system :as fes]))

(def port
  3000)

(def backend-url
  "http://localhost:3001")

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'system
    (constantly (fes/create-dev-system port backend-url))))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'system fes/start))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'system fes/stop))

(defn create-and-start
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after 'project-repl.frontend/create-and-start))
