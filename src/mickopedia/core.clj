(ns mickopedia.core
  (:require [net.cgrand.enlive-html :as html]
            [yada.yada :refer [listener resource as-resource]])
  (:gen-class))

(def ^:dynamic *base-url* "https://wikipedia.org")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def svr
  (listener
   ["/index.html" (as-resource (clojure.java.io/resource "public/index.html"))]
   {:port 3000}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
