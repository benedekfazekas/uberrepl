(ns user
  (:require [clojure.tools.namespace.repl :refer :all]
            [clojure.tools.namespace.file :refer :all]
            [clojure.tools.namespace.move :refer :all]
            [clojure.walk :refer :all])
  (:import (java.io File) (java.util.regex Pattern)))

(def startup-command-prefix "create-and-start-")

(def shutdown-command-prefix "stop-")

(defn run-all [prefix]
  (->> (keys (ns-refers 'user))
       (filter #(.startsWith (name %) prefix))
       (map #(apply (eval %) {}))
       (postwalk identity)))

(defn startup-all []
  (run-all startup-command-prefix))

(defn shutdown-all []
  (run-all shutdown-command-prefix))

(defn unmap-subproject-vars []
  (->> (ns-refers *ns*)
       (filter
        #(.contains (.toString (second %)) "project-repl"))
       (map first)
       (map (partial ns-unmap *ns*))
       (postwalk identity)))

(defn use-subproject-repl-files []
  (println "using subprojects repl files")
  (let [subproject-repl-files
        (->> (clojure.string/split
              (System/getProperty "java.class.path")
              (Pattern/compile (Pattern/quote File/pathSeparator)))
             (filter #(and (.contains % "checkouts") (.contains % "dev")))
             (map #(File. ^String %))
             (filter #(.isDirectory ^File %))
             (map file-seq)
             (flatten)
             (filter #(and
                       (.contains (.toString %) "project_repl")
                       (.isFile ^File %)))
             (map #(second (read-file-ns-decl %)))
             (map symbol))]
    (postwalk identity (map use subproject-repl-files))
    subproject-repl-files))

(defn self-reload []
  (println "project vars unloaded: " (count (unmap-subproject-vars)))
  (load "user"))

(defn uberrepl-reset []
  (shutdown-all)
  (refresh :after 'user/self-reload))

(println "project repl files used: " (use-subproject-repl-files))
(println "uberrepl user ns loaded")
