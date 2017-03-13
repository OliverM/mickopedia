(defproject mickopedia "0.2.0"
  :description "Mickopedia (in Clojure)"
  :url "http://mickopedia.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]
                 [clj-http "2.3.0"]
                 [yada "1.2.1"]]
  :main ^:skip-aot mickopedia.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
