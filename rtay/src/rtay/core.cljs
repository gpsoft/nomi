(ns rtay.core
  (:require request
            cheerio))

(def ^:private url "https://news.yahoo.co.jp/topics")

(defn- for-node
  [$ $nodes f]
  (.each $nodes #(f $ ($ %2))))

(defn- has-photo?
  [$topic-a]
  (pos? (.-length (.find $topic-a "span.icPhoto"))))

(defn- is-new?
  [$topic-a]
  (pos? (.-length (.find $topic-a "span.icNew"))))

(defn- link
  [$topic-a]
  (.attr $topic-a "href"))

(defn- heading!
  [$topic-a]
  (.remove (.find $topic-a "span"))
  (.text $topic-a))

(defn- render-topic
  [h l n? p?]
  (let [n (if n? "[N]" "[ ]")
        p (if p? "[P]" "[ ]")]
    (str n p h "(" l ")")))

(defn- topic
  [$ $topic]
  (let [$a (.find $topic "a")
        new? (is-new? $a)
        photo? (has-photo? $a)
        heading (heading! $a)]
    (println (render-topic heading (link $a) new? photo?))))

(defn- category
  [$ $cat]
  (println (.text $cat))
  (for-node $ (.nextAll $cat "li") topic))

(defn- topics
  [_ _ html]
  (let [$ (.load cheerio html)
        $cats ($ "div.topicsMod li.ttl")]
    (for-node $ $cats category)))

(defn -main
  [& args]
  (request url topics))

(set! *main-cli-fn* -main)
