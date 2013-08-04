(ns user)

(def project-root
  (last (re-find #"(.*/)dev/user.clj"
                 (. (java.io.File. *file*) getCanonicalPath))))

(def subproject-dirs
  (filter #(.isDirectory %) (.listFiles (clojure.java.io/file "checkouts"))))

(println (slurp (str project-root "bootstrap-msg.txt")))

(defn load-subproject-user [project-dir]
  (let [project-path (.toString project-dir)
        repl-file (->> (file-seq project-dir)
                       (filter #(.endsWith (.toString %) "user.clj"))
                       first)]
    (println "------------------------------------------------------------------------")
    (println (format "Asking the project %s to start rumbling..." project-path))
    (println "using repl file: " (.toString repl-file))
    (load-file (str repl-file))
    (format "Project %s done.\n" project-path)))

(defn run-all [prefix]
    (->> (keys (ns-publics 'user))
         (filter #(.startsWith (name %) prefix))
         (map #(apply (eval %) {}))))

(println)
(println (apply str (map load-subproject-user subproject-dirs)))
