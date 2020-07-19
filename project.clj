(defproject apollo "0.1.0"
  :description "A music programming language written in Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "1.0.194"]]
  :plugins [[cider/cider-nrepl "0.24.0"]]
  :main ^:skip-aot apollo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
