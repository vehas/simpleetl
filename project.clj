(defproject etl "0.1.0-SNAPSHOT"
  :description "simple etl"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]]
  :aot [etl.core]
  :main etl.core)
