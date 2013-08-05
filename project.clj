(def subproject-dirs
  (filter #(.isDirectory %) (.listFiles (clojure.java.io/file "checkouts"))))

(defn subproject-dependency [subproject-dir]
  (let [project-info (->> (slurp (str (.toString subproject-dir) "/project.clj"))
                          (re-find #"defproject\s+([0-9a-z_-]+).+\"(.*)\".*")
                          reverse)
        project-name (symbol (second project-info))
        project-version (first project-info)]
    [project-name project-version]))

(defn install-subproject [project-dir]
  (let [path (.toString project-dir)
        result (clojure.java.shell/sh "lein" "install" :dir path)]
    (println (format "installing project %s" path))
    (println (:out result))
    (format "Result %s for project %s.\n"
            (if (== 0 (:exit result))
              "success"
              "failure")
            path)))

(println (apply str "lein install results:\n\n" (map install-subproject subproject-dirs)))

(defproject clj_uberrepl "0.0.1"
  :description "Provides repl to run all linked applications in"
  :url "https://github.com/MailOnline/clj_uberrepl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~(reduce conj
                         [['org.clojure/clojure "1.5.0"]
                          ['org.clojure/tools.nrepl "0.2.2"]]
                         (map subproject-dependency subproject-dirs))
  :checkout-deps-shares [:source-paths :test-paths :resource-paths]
  :profiles {:dev {:source-paths ["dev"]}})
