(ns user
  (:require [clojure.tools.namespace.repl :refer :all]
            [clojure.tools.namespace.file :refer :all]
            [clojure.tools.namespace.move :refer :all]
            [clojure.walk :refer :all])
  (:import (java.io File) (java.util.regex Pattern)))

;; configuration: depending on how you set up your subprojects

(def project-repl-file-dir "project_repl")

(def project-repl-ns-prefix "project-repl")

(def startup-command-prefix "create-and-start")

(def shutdown-command-prefix "stop")

;; convenience methods for batch commands on all subprojects

(defn run-all [prefix]
  (->> (filter #(.contains (name %) project-repl-ns-prefix) (loaded-libs))
       (map ns-publics)
       (apply merge-with (fn [v1 v2] [v1 v2]))
       (filter #(.startsWith (name (first %)) prefix))
       flatten
       (remove symbol?)
       (map #(apply (eval %) {}))
       (postwalk identity)))

(defn uberrepl-startup-all []
  (run-all startup-command-prefix))

(defn uberrepl-shutdown-all []
  (run-all shutdown-command-prefix))

;; reset uberrepl and helpers

(defn unmap-subproject-vars []
  (->> (filter #(.contains (name %) project-repl-ns-prefix) (loaded-libs))
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
                       (.contains (.toString %) project-repl-file-dir)
                       (.isFile ^File %)))
             (map #(second (read-file-ns-decl %)))
             (map symbol))]
    (postwalk identity (map require subproject-repl-files))
    subproject-repl-files))

(defn self-reload []
  (println "project vars unloaded: " (count (unmap-subproject-vars)))
  (load "user")
  (uberrepl-startup-all))

(defn uberrepl-reset []
  (uberrepl-shutdown-all)
  (refresh :after 'user/self-reload))

(println "project repl files used: " (use-subproject-repl-files))
(println "uberrepl user ns loaded")
