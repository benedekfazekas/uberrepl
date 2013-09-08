(ns project-repl.backend
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [backend.system :as bes]))

(def port
  3001)

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'system
    (constantly (bes/create-dev-system port))))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'system bes/start))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'system bes/stop))

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
  (refresh :after 'project-repl.backend/create-and-start))
