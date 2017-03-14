(ns mickopedia.mickify
  (:require [clojure.string :as s]
            [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(def ^:dynamic *wikipedia-search-url*
  "http://en.wikipedia.org/wiki/Special:Search?go=Go&search=")

(def phrases
  "Phrases that can be inserted to replace periods."
  [". Bejaysus. ",". Jasus. ",". Jaysis. ",". Jaykers! ",". Sufferin'
  Jaysus. ",". Chrisht Almighty. ",". Jesus, Mary and Joseph. ",". In fairness
  now. ",". C'mere til I tell ya. ",". C'mere til I tell ya now. ",". C'mere til
  I tell yiz. ",". G'wan now. ",". Arra' would ye listen to this. ",". Arra'
  would ye listen to this shite? ",". Story? ",". Stop the lights! ",". Jesus,
  Mary and holy Saint Joseph. ",". Whisht now. ",". Right so. ",". I hope yiz
  are all ears now. ",". Whisht now and listen to this wan. ",". Here's a quare
  one. ",". Here's another quare one. ",". Here's a quare one for ye. ",".
  Here's another quare one for ye. ",". Jesus Mother of Chrisht almighty. ",".
  Whisht now and eist liom. ",". Be the hokey here's a quare wan. ",". G'wan now
  and listen to this wan. ",". Bejaysus here's a quare one right here now. ",".
  Sufferin' Jaysus listen to this. ",". Holy blatherin' Joseph, listen to
  this. ",". Listen up now to this fierce wan. ",". Would ye believe this
  shite?",". Be the holy feck, this is a quare wan. ",". Me head is hurtin' with
  all this raidin'. ",". Would ye swally this in a minute now?",". Bejaysus this
  is a quare tale altogether. " ", to be sure. ",", for the craic. ",",
  like. ",", bejaysus. ",", the hoor. ",", the cute hoor. ",", bedad. ",", Lord
  bless us and save us. ",", fair play. ",", what? ",", game ball! ",", enda
  story. ",". Sure this is it. ",". Soft oul' day. ",", you know yerself. ",",
  grand so. ",", so it is. ",", you know yourself like. ",", begorrah. ",", be
  the hokey! ",", would ye believe it? ",", would ye swally that? ",", the
  shitehawk. ",", that's fierce now what? "])

(defn random-collection-choice
  "Return a random phrase from the phrases list, or a default '.', since we
  don't want to *always* replace a period. There's a one in three chance the
  period will be returned."
  [collection default]
  (let [phrase-count (count collection)
        total (+ phrase-count (quot phrase-count 2))]
    (nth collection (rand-int total) default)))

(def aReps
  "Replacements for 'a'"
  [" a bleedin' ", " an oul' ", " a feckin' ", " a holy "])

(def theReps
  "Replacements for 'the'"
  [" the oul' ", " the bleedin' ", " the feckin' "])

(defn mickify-text-node
  "Mickify a given string."
  [content]
  (-> content
      (s/replace #"\bsl\B" "shl")
      (s/replace #"\Bing\b" "in'")
      (s/replace "Wikipedia" "Mickopedia")
      (s/replace #"\bbroken\b" "banjaxed")
      (s/replace #"\bpunch\b" "clatter")
      (s/replace #"\bMother\b" "Mammy")
      (s/replace #"\bmother\b" "mammy")
      (s/replace #"\bmothers\b" "mammies")
      (s/replace #"\bhim\b" "yer man")
      (s/replace #"\bvagina\b" "gee")
      (s/replace #"\bVagina\b" "Gee")
      (s/replace #"\bpenis\b" "mickey")
      (s/replace #"\bPenis\b" "Mickey")

      (s/replace #" a " #(random-collection-choice aReps %))
      (s/replace #" the " #(random-collection-choice theReps %))
      (s/replace #"\. " #(random-collection-choice phrases %))))

(defn mickify-alink-node
  "Given an enlive node of an <a> element with a href attribute pointing back to
  Wikipedia, return an enlive equivalent with the href pointing at mickopedia"
  [{:keys [content tag attrs] :as node}]
  (let [href (:href attrs)]
    (if (and href (s/starts-with? href "/wiki/"))
      {:content content
       :tag tag
       :attrs (assoc attrs :href
                     (s/replace-first href "/wiki/"
                                      "http://mickopedia.org/mickify?topic="))}
      node)))

(def mickification
  "The mickopedia transformations."
  (html/transformation
   [:head] (html/prepend (html/html [:base {:href "http://en.wikipedia.org"}]))
   [ #{:h1 :div} html/text-node] mickify-text-node
   [:a] mickify-alink-node
   [:form#searchform] (html/set-attr :action "http://mickopedia.org/mickify")
   [:div#simpleSearch :> :input] (html/remove-attr :name)
   [:input#searchInput] (html/set-attr :placeholder "Search Mickopedia"
                                       :name "topic")
   [:div#p-logo :a.mw-wiki-logo] (html/set-attr
                                  :href "http://mickopedia.org"
                                  :style "background-image: url(http://mickopedia.org/smallmiki.png)")))

(defn transform
  "Given a collection of enlive nodes of a wikipedia page, apply the mickopedia
  transformations and return a reconstituted html page."
  [nodes]
  (-> nodes
      mickification
      html/emit*
      (->> (apply str))
      ))

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
   html/html-snippet))

(defn mickify [in]
  (-> (str *wikipedia-search-url* in)
      parse-wikipedia-page
      transform))
