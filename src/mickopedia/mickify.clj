(ns mickopedia.mickify
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(def ^:dynamic *wikipedia-search-url*
  "http://en.wikipedia.org/wiki/Special:Search?go=Go&search=")

(defn transform
  "Given a collection of enlive nodes of a wikipedia page, apply the mickopedia
  transformations and return a reconstituted html page."
  [nodes]
  (-> nodes
      html/emit*
      (->> (apply str))))

(defn parse-wikipedia-page
  "Given a populated wikipedia search url, return a collection of enlive nodes
  of wikipedia's response."
  [url]
  (->
   (client/get url
               {:headers
                {"User-Agent"
                 "Mickifier/0.2 mickopedia.org mickopedia@gmail.com"}})
   :body
   html/html-snippet
   transform))

(defn mickify [in]
  (parse-wikipedia-page (str *wikipedia-search-url* in)))
