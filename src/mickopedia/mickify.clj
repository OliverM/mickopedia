(ns mickopedia.mickify
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(def ^:dynamic *wikipedia-search-url*  "http://en.wikipedia.org/wiki/Special:Search?go=Go&search=")

(defn search-wikipedia [url]
  (-> (client/get url
                  {:headers
                   {"User-Agent" "Mickifier/0.2 mickopedia.org mickopedia@gmail.com"}})
      :body))

(defn mickify [in]
  (search-wikipedia (str *wikipedia-search-url* in)))
