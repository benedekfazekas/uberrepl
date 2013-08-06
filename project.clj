(def subproject-dirs
  (filter #(.isDirectory %) (.listFiles (clojure.java.io/file "checkouts"))))

(defn read-subproject [subproject-dir]
  (let [subproject-file (str (.toString subproject-dir) "/project.clj")]
    (leiningen.core.project/read subproject-file [:dev])))

(def subprojects
  (map read-subproject subproject-dirs))

(clojure.walk/postwalk identity subprojects)

(defn subproject-dependency [subproject]
  (let [project-name (symbol (:name subproject))
        project-version (:version subproject)]
    [project-name project-version]))

(defproject clj_uberrepl "0.0.1"
  :description "Provides repl to run all linked applications in"
  :url "https://github.com/MailOnline/clj_uberrepl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~(reduce conj
                         [['org.clojure/clojure "1.5.0"]
                          ['org.clojure/tools.nrepl "0.2.2"]]
                         (map subproject-dependency subprojects))
  :checkout-deps-shares [:source-paths :test-paths :resource-paths]
  :profiles {:dev {:source-paths ["dev"]}})
