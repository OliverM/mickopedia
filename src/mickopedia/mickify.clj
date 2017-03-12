(ns mickopedia.mickify
  (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "https://wikipedia.org")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn mickify [in]
  (str "Janey! " in))
