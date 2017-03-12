(ns mickopedia.core
  (:require
   [yada.yada :refer [listener resource as-resource] :as yada]
   [yada.resources.classpath-resource :refer [new-classpath-resource]]
   [mickopedia.mickify :refer [mickify]])
  (:gen-class))

(def searcher
  (resource
   {:id :mickopedia/mickify
    :description "Pass search terms to mickify wikipedia mangler."
    :methods {:get {:consumes [{:media-type #{"application/x-www-form-urlencoded"}
                                 :charset "UTF-8"}]
                    :produces #{"text/html"}
                    :parameters {:query {:topic String}}
                    :response (fn [ctx]
                                (let [{:keys [topic]}
                                      (get-in ctx [:parameters :query])]
                                  (mickify topic)))}}}))

(def handler
  "A BIDI map of URLs to resources."
  [""
   [["/mickify" searcher]
    ["" (new-classpath-resource "public" {:index-files ["index.html"]})]]])

(def server-atom (atom nil))

(defn start-server
  [routes port]
  (reset! server-atom (listener handler {:port port})))

(defn stop-server [server-map]
  ((:close server-map)))

(defn reset-server []
  (when @server-atom (stop-server @server-atom))
  (start-server handler 3000))

(defn -main
  "Launch the web-server. Never returns."
  [& args]
  (println "Starting server on port 3000")
  (start-server handler 3000)
  @(promise))  ;; all system activity happens on daemon threads
               ;; so need to block forever here to prevent JVM shutdown
