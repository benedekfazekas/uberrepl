(def subproject-dirs
  (filter #(.isDirectory %) (.listFiles (clojure.java.io/file "checkouts"))))

(defn read-subproject [subproject-dir]
  (let [subproject-file (str (.toString subproject-dir) "/project.clj")]
    (leiningen.core.project/read subproject-file [:dev])))

(def subprojects
  (map read-subproject subproject-dirs))

(defn install-subproject [project]
  (let [path (:root project)
        result (clojure.java.shell/sh "lein" "install" :dir path)]
    (println (format "installing project %s" path))
    (println (:out result))
    (format "Result %s for project %s.\n"
            (if (== 0 (:exit result))
              "success"
              "failure")
            path)))

(println (apply str "lein install results:\n\n" (map install-subproject subprojects)))

(defn subproject-dependency [subproject]
  (let [project-name (symbol (:name subproject))
        project-version (:version subproject)]
    [project-name project-version]))

(defn subproject-dev-dependencies []
  (->> (map #(-> (:profiles %) :dev :dependencies) subprojects)
       (reduce concat)))

(defproject clj_uberrepl "0.0.1"
  :description "Provides repl to run multiple, possible related projects in one repl"
  :url "https://github.com/benedekfazekas/uberrepl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~(reduce conj (reduce conj
                                      [['org.clojure/clojure "1.5.0"]
                                       ['org.clojure/tools.namespace "0.2.4"]
                                       ['org.clojure/tools.nrepl "0.2.2"]]
                                      (map subproject-dependency subprojects))
                         (subproject-dev-dependencies))
  :checkout-deps-shares [:source-paths :test-paths :resource-paths
                         ~(fn [p] (str (:root p) "/dev"))]
  :profiles {:dev {:source-paths ["dev"]}})
