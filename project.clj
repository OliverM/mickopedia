(defproject mickopedia "0.1.0-SNAPSHOT"
  :description "Mickopedia (in Clojure)"
  :url "http://mickopedia.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]]
  :main ^:skip-aot mickopedia.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})